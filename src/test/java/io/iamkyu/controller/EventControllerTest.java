package io.iamkyu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.iamkyu.app.EventCreateRequest;
import io.iamkyu.common.TestDescription;
import io.iamkyu.domain.Event;
import io.iamkyu.domain.EventStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_UTF8_VALUE;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
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

                // validate headers
                .andExpect(status().isCreated())
                .andExpect(header().string(CONTENT_TYPE, HAL_JSON_UTF8_VALUE))
                .andExpect(header().exists(LOCATION))

                // validate bodies. 필드 존재 여부는 아래 Rest Docs 생성 과정에서 테스트 됨.
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))

                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("자신을 가르키는 링크"),
                                linkWithRel("profile").description("프로필을 가르키는 링크"),
                                linkWithRel("query-events").description("이벤트들을 조회하는 링크"),
                                linkWithRel("update-event").description("이벤트를 갱신하는 링크")
                        ),
                        requestHeaders(
                                headerWithName(ACCEPT).description(MediaType.APPLICATION_JSON_UTF8),
                                headerWithName(CONTENT_TYPE).description(MediaTypes.HAL_JSON)
                        ),
                        requestFields(
                                fieldWithPath("name").description("이벤트명"),
                                fieldWithPath("description").description("이벤트 설명"),
                                fieldWithPath("beginEnrollmentDateTime").description("이벤트 등록 시작일"),
                                fieldWithPath("closeEnrollmentDateTime").description("이벤트 등록 종료일"),
                                fieldWithPath("beginEventDateTime").description("이벤트 시작일"),
                                fieldWithPath("endEventDateTime").description("이벤트 종료일"),
                                fieldWithPath("location").description("이벤트 장소"),
                                fieldWithPath("basePrice").description("이벤트 가격"),
                                fieldWithPath("maxPrice").description("이벤트 최대 가격"),
                                fieldWithPath("limitOfEnrollment").description("이벤트 최대 등록 가능 수")
                        ),
                        responseHeaders(
                                headerWithName(LOCATION).description("새로 등록된 이벤트 조회 가능한 링크"),
                                headerWithName(CONTENT_TYPE).description(HAL_JSON_UTF8_VALUE)
                        ),
                        responseFields(
                                fieldWithPath("id").description(""),
                                fieldWithPath("name").description("이벤트명"),
                                fieldWithPath("description").description("이벤트 설명"),
                                fieldWithPath("beginEnrollmentDateTime").description("이벤트 등록 시작일"),
                                fieldWithPath("closeEnrollmentDateTime").description("이벤트 등록 종료일"),
                                fieldWithPath("beginEventDateTime").description("이벤트 시작일"),
                                fieldWithPath("endEventDateTime").description("이벤트 종료일"),
                                fieldWithPath("location").description("이벤트 장소"),
                                fieldWithPath("basePrice").description("이벤트 가격"),
                                fieldWithPath("maxPrice").description("이벤트 최대 가격"),
                                fieldWithPath("limitOfEnrollment").description("이벤트 최대 등록 가능 수"),
                                fieldWithPath("offline").description("오프라인 이벤트 여부"),
                                fieldWithPath("free").description("무료 이벤트 여부"),
                                fieldWithPath("eventStatus").description("이벤트 상태"),
                                fieldWithPath("_links.self.href").description("자신을 가르키는 링크"),
                                fieldWithPath("_links.profile.href").description("프로필을 가르키는 링크"),
                                fieldWithPath("_links.query-events.href").description("이벤트들을 조회하는 링크"),
                                fieldWithPath("_links.update-event.href").description("이벤트를 갱신하는 링크")
                        )
                ));
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