package kr.hhplus.be.server.domain.coupon.application.service;

import kr.hhplus.be.server.config.aop.lock.DistributedLock;
import kr.hhplus.be.server.config.aop.lock.LockType;
import kr.hhplus.be.server.config.aop.lock.Resource;
import kr.hhplus.be.server.config.aop.lock.ResourceKey;
import kr.hhplus.be.server.domain.coupon.application.Coupon;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import kr.hhplus.be.server.domain.coupon.application.exception.InvalidCouponException;
import kr.hhplus.be.server.domain.coupon.application.generator.CouponCodeGenerator;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponIssueRepository;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.controller.dto.CouponIssueRequest;
import kr.hhplus.be.server.domain.coupon.controller.dto.CouponIssueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;
    private final CouponCodeGenerator codeGenerator;

    public List<Coupon> getValidCoupons(){
        return couponRepository.findValidCouponList(LocalDateTime.now(), 0L);
    }

    public Coupon getCoupon(Long couponId){
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 쿠폰 입니다."));
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
