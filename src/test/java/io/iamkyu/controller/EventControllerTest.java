package io.iamkyu.controller;

import io.iamkyu.app.EventCreateRequest;
import io.iamkyu.common.TestDescription;
import io.iamkyu.domain.Event;
import io.iamkyu.domain.EventRepository;
import io.iamkyu.domain.EventStatus;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EventControllerTest extends ControllerTest {

    @Autowired
    private EventRepository eventRepository;

    @After
    public void tearDown() {
        eventRepository.deleteAll();
    }

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
                                fieldWithPath("id").description("이벤트 아이디"),
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
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
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
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
                .andDo(print());
    }

    @Test
    @TestDescription("페이징 된 이벤트 조회")
    public void queryEvents_200() throws Exception {
        //given
        int expectedPage = 1;
        int expectedPageSize = 10;
        String expectedSort = "name,DESC";
        int expectedEventCount = 30;

        generateEventsCountOf(expectedEventCount);

        //when then
        mockMvc.perform(get("/api/events")
                .param("page", String.valueOf(expectedPage))
                .param("size", String.valueOf(expectedPageSize))
                .param("sort", expectedSort)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())

                // validate headers
                .andExpect(status().isOk())
                .andExpect(header().string(CONTENT_TYPE, HAL_JSON_UTF8_VALUE))

                // validate bodies
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("page.size").value(expectedPageSize))
                .andExpect(jsonPath("page.totalElements").value(expectedEventCount))
                .andExpect(jsonPath("page.totalPages").value(expectedEventCount / expectedPageSize))
                .andExpect(jsonPath("page.number").value(expectedPage))

                .andDo(document("get-events",
                        links(
                                linkWithRel("self").description("자신을 가르키는 링크"),
                                linkWithRel("profile").description("프로필을 가르키는 링크"),
                                linkWithRel("prev").description("이전 페이지를 가르키는 링크"),
                                linkWithRel("next").description("다음 페이지를 가르키는 링크"),
                                linkWithRel("first").description("첫 페이지를 가르키는 링크"),
                                linkWithRel("last").description("마지막 페이지를 가르키는 링크")
                        ),
                        requestHeaders(
                                headerWithName(ACCEPT).description(MediaType.APPLICATION_JSON_UTF8),
                                headerWithName(CONTENT_TYPE).description(MediaTypes.HAL_JSON)
                        ),
                        requestParameters(
                                parameterWithName("page").optional().description("페이지"),
                                parameterWithName("size").optional().description("페이지 사이즈"),
                                parameterWithName("sort").optional().description("페이지 정렬 기준 및 순서")
                        ),
                        responseHeaders(
                                headerWithName(CONTENT_TYPE).description(HAL_JSON_UTF8_VALUE)
                        ),
                        responseFields(
                                fieldWithPath("_embedded.eventList[]").description("이벤트 리스트"),
                                fieldWithPath("_embedded.eventList[].id").description("이벤트 아이디"),
                                fieldWithPath("_embedded.eventList[].name").description("이벤트명"),
                                // fieldWithPath("_embedded.eventList[].description").description("이벤트 설명"),
                                // fieldWithPath("_embedded.eventList[].beginEnrollmentDateTime").description("이벤트 등록 시작일"),
                                // fieldWithPath("_embedded.eventList[].closeEnrollmentDateTime").description("이벤트 등록 종료일"),
                                // fieldWithPath("_embedded.eventList[].beginEventDateTime").description("이벤트 시작일"),
                                // fieldWithPath("_embedded.eventList[].endEventDateTime").description("이벤트 종료일"),
                                // fieldWithPath("_embedded.eventList[].location").description("이벤트 장소"),
                                fieldWithPath("_embedded.eventList[].basePrice").description("이벤트 가격"),
                                fieldWithPath("_embedded.eventList[].maxPrice").description("이벤트 최대 가격"),
                                fieldWithPath("_embedded.eventList[].limitOfEnrollment").description("이벤트 최대 등록 가능 수"),
                                fieldWithPath("_embedded.eventList[].offline").description("오프라인 이벤트 여부"),
                                fieldWithPath("_embedded.eventList[].free").description("무료 이벤트 여부"),
                                // fieldWithPath("_embedded.eventList[].eventStatus").description("이벤트 상태"),
                                fieldWithPath("_embedded.eventList[]._links.self.href").description("자신을 가르키는 링크"),

                                fieldWithPath("_links.first.href").description("첫 페이지를 가르키는 링크"),
                                fieldWithPath("_links.prev.href").description("이전 페이지를 가르키는 링크"),
                                fieldWithPath("_links.self.href").description("자신을 가르키는 링크"),
                                fieldWithPath("_links.next.href").description("다음 페이지를 가르키는 링크"),
                                fieldWithPath("_links.last.href").description("마지막 페이지를 가르키는 링크"),
                                fieldWithPath("_links.profile.href").description("프로필을 가르키는 링크"),

                                fieldWithPath("page.size").description("페이지 사이즈"),
                                fieldWithPath("page.totalElements").description("총 요소 수"),
                                fieldWithPath("page.totalPages").description("총 페이지 수"),
                                fieldWithPath("page.number").description("페이지 번호")
                        )
                ));
    }

    private void generateEventsCountOf(int expectedCount) {
        List<Event> events = IntStream.range(0, expectedCount)
                .mapToObj(index -> Event.builder().name("Event" + index).build())
                .collect(toList());

        eventRepository.saveAll(events);
    }

    private LocalDateTime december(int date) {
        return LocalDateTime.of(2018, 12, date, 0, 0);
    }
}