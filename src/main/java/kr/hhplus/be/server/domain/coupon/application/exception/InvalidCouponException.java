package kr.hhplus.be.server.domain.coupon.application.exception;

/**
 * 쿠폰 코드가 올바르지 않거나 존재하지 않을 때 던지는 예외입니다.
 */
public class InvalidCouponException extends RuntimeException {
    private final String couponCode;

    /**
     * @param couponCode 문제가 된 쿠폰 코드
     */
    public InvalidCouponException(String couponCode) {
        super("유효하지 않은 쿠폰 입니다 : " + couponCode);
        this.couponCode = couponCode;
    }

    /**
     * @param couponCode 문제가 된 쿠폰 코드
     * @param cause      근본 원인 예외
     */
    public InvalidCouponException(String couponCode, Throwable cause) {
        super("유효하지 않은 쿠폰 입니다 : " + couponCode, cause);
        this.couponCode = couponCode;
    }

    /**
     * @return 예외가 발생한 쿠폰 코드
     */
    public String getCouponCode() {
        return couponCode;
    }
}
