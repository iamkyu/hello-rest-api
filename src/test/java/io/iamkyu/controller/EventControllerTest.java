package io.iamkyu.controller;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_UTF8_VALUE;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.iamkyu.common.TestDescription;
import io.iamkyu.domain.Event;
import io.iamkyu.domain.EventStatus;
import io.iamkyu.app.EventCreateRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @TestDescription("이벤트 생성에 성공한다")
    public void createEvent_200() throws Exception {
        //given
        EventCreateRequest request = EventCreateRequest.builder()
                .name("New Event")
                .description("Foo bar")
                .beginEnrollmentDateTime(december(1))
                .closeEnrollmentDateTime(december(10))
                .beginEventDateTime(december(24))
                .endEventDateTime(december(25))
                .basePrice(10000)
                .maxPrice(50000)
                .limitOfEnrollment(100)
                .location("서울특별시")
                .build();

        //when then
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(CONTENT_TYPE, HAL_JSON_UTF8_VALUE))
                .andExpect(header().exists(LOCATION))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));
    }

    @Test
    public void createEvent_알수없는_파라미터_400() throws Exception {
        //given
        Event request = Event.builder()
                .id(100)
                .build();

        //when then
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void createEvent_빈_파라미터_400() throws Exception {
        //given
        EventCreateRequest request = EventCreateRequest.builder().build();

        //when then
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void createEvent_최대가격_보다_높은_최소가격_400() throws Exception {
        //given
        EventCreateRequest request = EventCreateRequest.builder()
                .name("New Event")
                .description("Foo bar")
                .beginEnrollmentDateTime(december(1))
                .closeEnrollmentDateTime(december(10))
                .beginEventDateTime(december(24))
                .endEventDateTime(december(25))
                .basePrice(2000)
                .maxPrice(1000)
                .limitOfEnrollment(100)
                .location("서울특별시")
                .build();

        //when then
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
                .andDo(print());
    }

    @Test
    public void createEvent_이벤트_시작일_보다_빠른_이벤트_종료일_400() throws Exception {
        //given
        EventCreateRequest request = EventCreateRequest.builder()
                .name("New Event")
                .description("Foo bar")
                .beginEnrollmentDateTime(december(1))
                .closeEnrollmentDateTime(december(10))
                .beginEventDateTime(december(25))
                .endEventDateTime(december(24))
                .basePrice(1000)
                .maxPrice(2000)
                .limitOfEnrollment(100)
                .location("서울특별시")
                .build();

        //when then
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
                .andDo(print());
    }

    private LocalDateTime december(int date) {
        return LocalDateTime.of(2018, 12, date, 0, 0);
    }
}