package kr.hhplus.be.server.domain.coupon.application.service;

import jakarta.validation.ConstraintViolationException;
import kr.hhplus.be.server.domain.coupon.adapter.entity.CouponJpaEntity;
import kr.hhplus.be.server.domain.coupon.application.Coupon;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponIssueRepository;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.dto.CouponIssueRequest;
import kr.hhplus.be.server.domain.coupon.dto.CouponIssueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;

    public List<Coupon> getAllCoupons(){
        return couponRepository.findAll();
    }

    public Coupon getCoupon(Long couponId){
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 쿠폰 입니다."));
    }

    public CouponIssue newCouponIssue(Long userId, Long couponId){
        Coupon coupon = getCoupon(couponId);

        // 쿠폰 유효한지는 나중에 Redis를 통해서 확인해야될수도있어서 서비스에서 처리함
        if(coupon.hasRemaining()){
            String couponCode = coupon.getCouponId() + "-" + userId + "-" + coupon.getRemaining();

            CouponIssue couponIssue = CouponIssue.builder()
                    .couponValid("VALID")
                    .couponCode(couponCode)
                    .couponId(coupon.getCouponId())
                    .userId(userId)
                    .discountRate(new BigDecimal(20))
                    .expireDate(LocalDateTime.of(9999, 12, 31, 0, 0))
                    .build();

            coupon.decrease();
            couponRepository.save(coupon);

            return couponIssueRepository.save(couponIssue);
        }else{
            throw new IllegalStateException("쿠폰 발급에 실패하였습니다.");
        }
    }

    public CouponIssueResponse couponIssueRes(CouponIssueRequest req){
        CouponIssue couponIssue = newCouponIssue(req.userId(), req.couponId());
        return new CouponIssueResponse(
                couponIssue.getCouponIssueId(),
                couponIssue.getCouponCode(),
                couponIssue.getCouponId(),
                couponIssue.getUpdateDt().toString(),
                couponIssue.getExpireDate().toString()
        );
    }

}
