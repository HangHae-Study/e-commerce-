package kr.hhplus.be.server.config.aop.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionInvoker {
    @Transactional
    public Object proceedWithinTransaction(ProceedingJoinPoint pjp) throws Throwable {
        return pjp.proceed();
    }
}