package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PointService {

    private final UserPointTable userPointTable;

    private final PointHistoryTable pointHistoryTable;

    /**
     * 해당 id에 포인트를 충전하고 히스토리에 충전내역을 저장한다.
     * @param id
     * @param chargePoint
     * @return
     */
    public UserPoint chargePoint(long id, long chargePoint) {
        UserPoint userPoint = userPointTable.selectById(id);
        // 포인트 충전
        userPoint = userPoint.chargePoint(chargePoint);
        // 히스토리 저장
        pointHistoryTable.insert(userPoint.id(), chargePoint, TransactionType.CHARGE, System.currentTimeMillis());
        return userPointTable.insertOrUpdate(userPoint.id(), userPoint.point());
    }

    /**
     * 해당 id의 포인트를 사용하고, 사용 히스토리를 저장한다.
     * @param id
     * @param usePoint
     * @return
     */
    public UserPoint usePoint(long id, long usePoint) {
        UserPoint userPoint = userPointTable.selectById(id);
        // 포인트 사용
        userPoint = userPoint.usePoint(usePoint);
        // 히스토리 저장
        pointHistoryTable.insert(userPoint.id(), usePoint, TransactionType.USE, System.currentTimeMillis());
        return userPointTable.insertOrUpdate(userPoint.id(), userPoint.point());
    }

    /**
     * 해당 id의 잔여 포인트를 조회한다.
     * @param id
     * @return
     */
    public UserPoint getPoint(long id) {
        return userPointTable.selectById(id);
    }

    /**
     * 해당 id의 포인트 사용 히스토리를 조회한다.
     * @param id
     * @return
     */
    public List<PointHistory> getHistory(long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }
}
