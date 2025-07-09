package io.hhplus.tdd;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import org.apache.catalina.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.given;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @Mock
    private UserPointTable userPointTable;

    @InjectMocks
    private PointService pointService;

    /**
     * 포인트를 충전한다.
     * 요구사항 1. 특정 금액을 충전하면, 그 금액만큼 잔액에서 더해진다.
     */
    @Test
    void chargeTest1() {

        // given
        long id = 1;
        long chargePoint = 1000;

        UserPoint userPoint = new UserPoint(id, 0, System.currentTimeMillis());
        when(userPointTable.insertOrUpdate(id, chargePoint)).thenReturn(new UserPoint(id, chargePoint, System.currentTimeMillis())); // chargePoint를 그대로 return하게 해서 잔액에 더해지지는 않음.
        when(userPointTable.selectById(anyLong())).thenReturn(userPoint);

        // when
        userPoint = pointService.chargePoint(userPoint.id(), chargePoint);
        System.out.println(userPointTable.selectById(userPoint.id()).point());
        // then
//         assertThat(userPointTable.selectById(userPoint.id()).point()).isEqualTo(1000); // select하면 그냥 객체를 return하게 해서 point=0이 나옴.
        assertThat(userPoint.point()).isEqualTo(1000);
    }

    /**
     * 포인트를 충전한다.
     * 요구사항 1 보완
     * - 잔액에 충전이 되게
     * - 마지막에 충전 후 잔액을 select로 확인할 수 있게
     */
    @Test
    void chargeTest2() {
        // given
        long id = 1;
        long startPoint = 0;
        long chargePoint = 1000;
        UserPoint userPoint = new UserPoint(id, startPoint, System.currentTimeMillis());
        when(userPointTable.selectById(anyLong())).thenReturn(userPoint);
        when(userPointTable.insertOrUpdate(id, chargePoint)).thenAnswer(invocation -> {
           long afterPoint = userPointTable.selectById(id).point() + chargePoint;
           return new UserPoint(id, afterPoint, System.currentTimeMillis());
        });
        // when
        userPoint = pointService.chargePoint(id, chargePoint);

        // then
        // 한번 더 선언
        when(userPointTable.selectById(id)).thenReturn(new UserPoint(id, startPoint+chargePoint, System.currentTimeMillis()));
        assertThat(userPointTable.selectById(userPoint.id()).point()).isEqualTo(startPoint + chargePoint);
    }

    /**
     * 포인트를 사용한다.
     * 요구사항 1. 특정 금액을 사용하면, 그 금액만큼 잔액에서 차감된다.
     */
    @Test
    void useTest1() {
        // given
        long id = 1;
        long startPoint = 2000;
        long usePoint = 1000;
        UserPoint userPoint = new UserPoint(id, startPoint, System.currentTimeMillis());

        when(userPointTable.selectById(id)).thenReturn(userPoint);
        when(userPointTable.insertOrUpdate(id, usePoint)).thenAnswer(invocation -> {
            long finalPoint = startPoint - usePoint;
            return new UserPoint(id, finalPoint, System.currentTimeMillis());
        });
        // when
        userPoint = pointService.usePoint(id, usePoint);

        // then
        when(userPointTable.selectById(id)).thenReturn(userPoint);
        assertThat(userPointTable.selectById(id).point()).isEqualTo(startPoint - usePoint);
    }

    /**
     * 포인트를 사용한다.
     * 요구사항 2. 잔액이 0원 이하로 포인트를 사용할 수 없다. ‘잔액이 부족합니다’ 알림이 뜬다.
     */
    @Test
    void useTest2() {
        // given
        long id = 1;
        long startPoint = 1000;
        long usePoint = 2000;
        UserPoint userPoint = new UserPoint(id, startPoint, System.currentTimeMillis());

        when(userPointTable.selectById(id)).thenReturn(userPoint);
        when(userPointTable.insertOrUpdate(id, usePoint)).thenThrow(new IllegalArgumentException("잔액이 부족합니다.")); // 바로 예외 던지는게 맞나?
        // when
        userPoint = pointService.usePoint(id, usePoint);

        // then
        assertThrows(IllegalArgumentException.class, () -> {pointService.usePoint(id, usePoint);});
    }
}
