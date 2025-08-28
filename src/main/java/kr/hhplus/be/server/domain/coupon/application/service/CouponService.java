package kr.hhplus.be.server.domain.coupon.application.service;

import kr.hhplus.be.server.config.aop.lock.DistributedLock;
import kr.hhplus.be.server.config.aop.lock.LockType;
import kr.hhplus.be.server.config.aop.lock.Resource;
import kr.hhplus.be.server.config.aop.lock.ResourceKey;
import kr.hhplus.be.server.domain.coupon.adapter.cache.CouponCacheKeyProvider;
import kr.hhplus.be.server.domain.coupon.adapter.cache.CouponCacheQueue;
import kr.hhplus.be.server.domain.coupon.adapter.cache.CouponCacheRepository;
import kr.hhplus.be.server.domain.coupon.adapter.cache.CouponIssuedCacheRepository;
import kr.hhplus.be.server.domain.coupon.application.Coupon;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import kr.hhplus.be.server.common.exception.coupon.InvalidCouponException;
import kr.hhplus.be.server.domain.coupon.application.generator.CouponCodeGenerator;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponIssueRepository;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.controller.dto.CouponClaimCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import static kr.hhplus.be.server.domain.coupon.adapter.cache.CouponCacheKeyProvider.CouponClaimStatus.*;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;
    private final CouponCodeGenerator codeGenerator;

    private final CouponCacheRepository couponCacheRepository;
    private final CouponIssuedCacheRepository couponIssuedCacheRepository;
    private final CouponCacheQueue couponCacheQueue;

    public List<Coupon> getValidCoupons(){
        return couponRepository.findValidCouponList(LocalDateTime.now(), 0L);
    }

    public Coupon getCoupon(Long couponId){
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 쿠폰 입니다."));
    }

    // 유저 쿠폰 발급 큐 요청
    public CouponClaimCommand.CouponClaimResponse couponClaim(Long couponId, Long userId, String couponCode){

        CouponClaimCommand.CouponClaimResponse response = null;

        CouponCacheKeyProvider.CouponClaimStatus issuedStatus = couponIssuedCacheRepository.getClaimStatus(couponId, userId).get();

        if(issuedStatus.equals(ISSUED)){
            if(couponCode.isBlank()){
                throw new InvalidCouponException("INVALID COUPON CODE");
            }
            // 이미 발급된 쿠폰 입니다.
            // 쿠폰 발급 캐시 데이터 전송 or 쿠폰 발급 DB 데이터 캐싱 후 데이터 전송
            Optional<CouponIssue> iss = couponIssuedCacheRepository.getIssuedCoupon(couponId, userId);

            if(iss.isEmpty()){
                iss = couponIssueRepository.findByCouponCode(couponCode);
                couponIssuedCacheRepository.cacheCouponIssuedData(couponId, userId, iss.get(), Duration.ofDays(7));
            }
            response = CouponClaimCommand.issued(iss.get());
        }else if(issuedStatus.equals(FAILED)){
            // 발급에 실패하였습니다.
            throw new InvalidCouponException("FAILED TO ISSUE COUPON");
        }else if(issuedStatus.equals(WAITED) || issuedStatus.equals(PROCESSING)){
            // 현재 발급 중입니다. 앞선 대기 순위 n 번 째
            Long rank = couponCacheQueue.getRank(couponId, userId);
            response = CouponClaimCommand.ranked(couponId, userId, rank);
        }else if(issuedStatus.equals(INIT)){
            // todo: DB에 해당 유저의 발급내역이 있는지 확인해준 후 진행
            //couponIssueRepositiory.findByCouponIdAndUserId

            Long remains = couponCacheRepository.getRemainingStock(couponId);
            if(remains <= 0){
                throw new InvalidCouponException("FAILED TO ISSUE COUPON");
            }

            boolean issued = couponCacheQueue.enqueueClaim(couponId, userId);
            if(issued){
                // 발급 요청이 정상적으로 진행되었습니다.
                response = CouponClaimCommand.init(couponId, userId);
            }else{
                throw new InvalidCouponException("FAILED TO ISSUE COUPON");
            }
        }

        return response;
    }

    @DistributedLock(
            type = LockType.COUPON,
            keys = { @ResourceKey(resource = Resource.COUPON, key = "#couponId") }
    )
    @Transactional
    public CouponIssue newCouponIssue(Long userId, Long couponId) {
        Coupon coupon = //couponRepository.findByIdWithPessimisticLock(couponId)
                couponRepository.findById(couponId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 쿠폰입니다."));

        String code = codeGenerator.generate(coupon, coupon.getCouponId(), coupon.getRemaining());
        CouponIssue issue = coupon.issueTo(userId, code);

        // 변경된 coupon, 발급된 issue 둘 다 저장
        couponRepository.save(coupon);
        return couponIssueRepository.save(issue);
    }

    public CouponIssue getCouponIssue(Long userId, String couponCode){
        if(couponCode.isEmpty() || couponCode.isBlank()){
            return null;
        }

        CouponIssue ci = couponIssueRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new InvalidCouponException(couponCode));

        if(!Objects.equals(userId, ci.getUserId()) || !ci.isValid()){
            throw new InvalidCouponException(couponCode);
        }
        return ci;
    }

    @Transactional
    public CouponIssue couponAppliedByOrder(Long userId, String cCode){
        if(cCode.isEmpty() || cCode.isBlank()){
            return null;
        }

        CouponIssue ci = getCouponIssue(userId, cCode);
        ci.setCouponValid("N");

        couponIssueRepository.save(ci);

        return ci;
    }

    @Transactional
    public CouponIssue getCouponIssueForRestore(Long userId, String couponCode){
        if(couponCode.isEmpty() || couponCode.isBlank()){
            return null;
        }

        CouponIssue ci = couponIssueRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new InvalidCouponException(couponCode));

        if(!Objects.equals(userId, ci.getUserId())){
            throw new InvalidCouponException(couponCode);
        }
        return ci;
    }

    @Transactional
    public CouponIssue couponRestoreByPayment(Long userId, String cCode){
        if(cCode.isEmpty() || cCode.isBlank()){
            return null;
        }

        CouponIssue ci = getCouponIssueForRestore(userId, cCode);
        ci.setCouponValid("Y");

        couponIssueRepository.save(ci);

        return ci;
    }


}
