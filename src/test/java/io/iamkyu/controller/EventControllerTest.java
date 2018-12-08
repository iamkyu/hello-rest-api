package io.iamkyu.controller;

import static org.mockito.Mockito.when;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_UTF8_VALUE;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.iamkyu.domain.Event;
import io.iamkyu.domain.EventRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@WebMvcTest
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    EventRepository eventRepository;

    @Test
    public void createEvent() throws Exception {
        //given
        Event event = Event.builder()
                .beginEnrollmentDateTime(december(1))
                .closeEnrollmentDateTime(december(10))
                .beginEventDateTime(december(24))
                .endEventDateTime(december(25))
                .basePrice(10000)
                .maxPrice(50000)
                .limitOfEnrollment(100)
                .location("서울특별시")
                .build();
        event.setId(100);
        when(eventRepository.save(event)).thenReturn(event);

        //when then
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(mapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(header().string(CONTENT_TYPE, HAL_JSON_UTF8_VALUE))
                .andExpect(header().exists(LOCATION))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists());
    }

    private LocalDateTime december(int date) {
        return LocalDateTime.of(2018, 12, date, 0, 0);
    }
}