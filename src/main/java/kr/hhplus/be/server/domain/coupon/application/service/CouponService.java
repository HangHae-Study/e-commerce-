package kr.hhplus.be.server.domain.coupon.application.service;

import kr.hhplus.be.server.domain.coupon.application.Coupon;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
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

    @Transactional
    public CouponIssue newCouponIssue(Long userId, Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 쿠폰입니다."));

        // 쿠폰이 스스로 발급 로직을 수행 → 남은 수량 감소, CouponIssue 생성
        CouponIssue issue = coupon.issueTo(userId, codeGenerator);

        // 변경된 coupon, 발급된 issue 둘 다 저장
        couponRepository.save(coupon);
        return couponIssueRepository.save(issue);
    }

    public CouponIssue getCouponIssue(Long userId, String couponCode){
        if(couponCode.isEmpty() || couponCode.isBlank()){
            return null;
        }

        CouponIssue ci = couponIssueRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new NoSuchElementException("발급(유효)되지 않은 쿠폰입니다."));

        if(!Objects.equals(userId, ci.getUserId()) || !ci.isValid()){
            throw new IllegalStateException("유효하지 않은 쿠폰 입니다");
        }
        return ci;
    }

    public CouponIssue couponAppliedByOrder(String couponIssue){

        return null;
    }

}
