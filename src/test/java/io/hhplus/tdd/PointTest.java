package io.hhplus.tdd;

import io.hhplus.tdd.point.UserPoint;
import org.apache.catalina.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PointTest {

    /**
     * 요구사항 1. 특정 금액을 충전하면, 그 금액만큼 잔액에서 더해진다.
     * 1000포인트를 충전하면, 1000원이 증가한다.
     */
    @Test
    void chargeTest1() {

        // given
        long id = 1;
        long chargePoint = 1000;

        // 객체 생성
        UserPoint userPoint = new UserPoint(id, 0, System.currentTimeMillis());

        // when
        // 1000원을 충전한다.
        userPoint = userPoint.chargePoint(chargePoint);

        // then
        assertThat(userPoint.point()).isEqualTo(1000);
    }

    /**
     * 요구사항 2. 최대 10,000 포인트까지만 적립이 가능하다.
     */
    @Test
    void chargeTest2() {
        // given
        long id = 1;
        long chargePoint = 5000;
        // 초기 포인트 = 6000
        UserPoint userPoint = new UserPoint(id, 6000, System.currentTimeMillis());

        // when
        userPoint = userPoint.chargePoint(chargePoint);

        // then
        assertThat(userPoint.point()).isEqualTo(10000);
    }

    /**
     * 요구사항 1. 특정 금액을 사용하면, 그 금액만큼 잔액에서 차감된다.
     */
    @Test
    void useTest1() {
        // given
        long id = 1;
        long usePoint = 1000;
        // 초기 포인트 = 2000
        UserPoint userPoint = new UserPoint(id, 2000, System.currentTimeMillis());

        // when
        userPoint = userPoint.usePoint(usePoint);

        // then
        assertThat(userPoint.point()).isEqualTo(1000);
    }

    /**
     * 요구사항 2. 잔액이 0원 이하로 포인트를 사용할 수 없으며, ‘잔액이 부족합니다’ 에러메시지가 뜬다.
     */
    @Test
    void useTest2() {
        // given
        long id = 1;
        long usePoint = 2000;
        // 초기포인트 = 1000
        UserPoint userPoint = new UserPoint(id, 1000, System.currentTimeMillis());

        // when, then
        assertThrows(IllegalArgumentException.class, () -> userPoint.usePoint(usePoint));

    }

    /**
     * 포인트를 조회한다.
     * 요구사항 1. 해당 id의 잔액을 확인 할 수 있다.
     */
    @Test
    void getPoint() {
        // given
        long id = 1;
        // 초기포인트 = 2000;
        UserPoint userPoint = new UserPoint(id, 2000, System.currentTimeMillis());

        // when, then
        assertThat(userPoint.getPoint(id)).isEqualTo(2000);
    }
}
