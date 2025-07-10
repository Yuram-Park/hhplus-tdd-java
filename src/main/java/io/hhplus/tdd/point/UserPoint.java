package io.hhplus.tdd.point;

import ch.qos.logback.core.net.SyslogOutputStream;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public UserPoint chargePoint(long amount) {
        long newPoint = point + amount;
        if(newPoint > 10_000) {
            System.out.println("포인트는 최대 10,000 포인트까지만 적립이 가능합니다.");
            newPoint = 10_000;
        }
        return new UserPoint(this.id, newPoint, System.currentTimeMillis());
    }

    public UserPoint usePoint(long amount) {
        long newPoint = point - amount;
        if(newPoint < 0) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        return new UserPoint(this.id, this.point - amount, System.currentTimeMillis());
    }

    public long getPoint(long id) {
        return point;
    }

}
