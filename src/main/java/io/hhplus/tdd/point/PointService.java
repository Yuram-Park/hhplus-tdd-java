package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PointService {

    private final UserPointTable userPointTable;

    public UserPoint chargePoint(long id, long chargePoint) {
        UserPoint userPoint = userPointTable.selectById(id);
        userPoint = userPoint.chargePoint(chargePoint);
        return userPointTable.insertOrUpdate(userPoint.id(), userPoint.point());
    }

    public UserPoint usePoint(long id, long usePoint) {
        UserPoint userPoint = userPointTable.selectById(id);
        userPoint = userPoint.usePoint(usePoint);
        return userPointTable.insertOrUpdate(userPoint.id(), userPoint.point());
    }
}
