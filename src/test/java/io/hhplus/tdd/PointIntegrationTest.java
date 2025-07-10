package io.hhplus.tdd;

import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import netscape.javascript.JSObject;
import org.apache.catalina.User;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PointIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PointService pointService;

    /**
     * 포인트를 충전한다.
     * @throws Exception
     */
    @Test
    void chargePointTest() throws Exception {
        // given
        long id = 1;
        long chargePoint = 3000;

        // when, then
        // 유저의 포인트 조회
        mockMvc.perform(get("/point/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.point").value(0));

        // 포인트 충전
        mockMvc.perform(patch("/point/{id}/charge", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Long.toString(chargePoint)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.point").value(chargePoint));
    }

    /**
     * 포인트를 사용한다.
     */
    @Test
    void usePointTest() throws Exception {
        // given
        long id = 1;
        long chargePoint = 3000;
        long usePoint = 2000;

        pointService.chargePoint(id, chargePoint);


        // when, then
        mockMvc.perform(patch("/point/{id}/use", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Long.toString(usePoint)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.point").value(chargePoint - usePoint));
    }

    /**
     * 포인트를 조회한다.
     */
    @Test
    void pointTest() throws Exception {
        // given
        long id = 1;
        long chargePoint = 3000;
        long usePoint = 2000;

        pointService.chargePoint(id, chargePoint);
        pointService.usePoint(id, usePoint);

        // when, then
        mockMvc.perform(get("/point/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.point").value(chargePoint - usePoint));

    }

    /**
     * 포인트 히스토리를 조회한다.
     */
    @Test
    void historyTest() throws Exception {
        // given
        long id = 1;
        long chargePoint = 3000;
        long usePoint = 2000;

        pointService.chargePoint(id, chargePoint);
        pointService.usePoint(id, usePoint);

        // when
        mockMvc.perform(get("/point/{id}/histories", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2)) // 배열 길이
                .andExpect(jsonPath("$[0].userId").value(Long.toString(id)))
                .andExpect(jsonPath("$[0].amount").value(Long.toString(chargePoint)))
                .andExpect(jsonPath("$[0].type").value("CHARGE"))
                .andExpect(jsonPath("$[1].userId").value(Long.toString(id)))
                .andExpect(jsonPath("$[1].amount").value(Long.toString(usePoint)))
                .andExpect(jsonPath("$[1].type").value("USE"));
        // then
    }
}
