package kr.hhplus.be.server.config.aop.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // 트랜잭션보다 먼저 실행 보장
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAspect {
    private final RedissonClient redisson;

    private static final Map<Resource, String> NS = Map.of(
            Resource.COUPON, "lock:coupon:",
            Resource.POINT,  "lock:point:",
            Resource.ORDER,  "lock:order:",
            Resource.STOCK,  "lock:stock:"
    );

    private final ExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer nameDisc = new DefaultParameterNameDiscoverer();

    @Around(value = "@annotation(kr.hhplus.be.server.config.aop.lock.DistributedLock)"
            , argNames = "pjp")
    public Object lockFirstThenProceed(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        Method method = sig.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        // 1) SpEL 컨텍스트 구성
        String[] paramNames = nameDisc.getParameterNames(method);
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        if (paramNames != null) {
            Object[] args = pjp.getArgs();
            for (int i = 0; i < paramNames.length; i++) {
                ctx.setVariable(paramNames[i], args[i]); // #paramName 접근
            }
        }

        // 2) 자원별 키 생성(정렬해 데드락 예방)
        List<String> keys = Arrays.stream(distributedLock.keys())
                .sorted(Comparator.comparing(k -> k.resource().name()))
                .map(k -> {
                    Object v = parser.parseExpression(k.key()).getValue(ctx);
                    if (v == null) {
                        throw new IllegalArgumentException("DistributedLock key evaluated to null: " + k);
                    }
                    String ns = NS.get(k.resource());
                    if (ns == null) {
                        throw new IllegalStateException("No namespace for resource: " + k.resource());
                    }
                    return ns + v;
                })
                .collect(Collectors.toList());

        if (keys.isEmpty()) {
            throw new IllegalArgumentException("@DistributedLock.keys is empty");
        }

        // 3) (멀티)락 구성
        RLock lock = (keys.size() == 1)
                ? redisson.getLock(keys.get(0))
                : new RedissonMultiLock(keys.stream().map(redisson::getLock).toArray(RLock[]::new));

        boolean acquired = false;
        try {
            acquired = lock.tryLock(distributedLock.waitTime(),
                    distributedLock.leaseTime(),
                    distributedLock.timeUnit());
            if (!acquired) {
                throw new IllegalStateException("Failed to acquire lock: " + keys);
            }

            // 4) 이제 proceed → 여기서 트랜잭션 어드바이스가 개입해 @Transactional 시작됨
            return pjp.proceed();

        } finally {
            // 5) 커밋/롤백 이후 finally에서 해제
            try {
                if (acquired && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            } catch (Throwable t) {
                log.warn("Failed to unlock: {}", t.toString());
            }
        }
    }
}

