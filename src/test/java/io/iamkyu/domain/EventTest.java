package io.iamkyu.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.Test;

public class EventTest {
    @Test
    public void build() {
        //given when
        Event event = Event.builder().build();

        //then
        assertThat(event).isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void 이벤트의_아이디는_변경할_수_없다() {
        //given
        Event event = new Event();
        event.setId(1);

        //when
        event.setId(2);

        //then
        fail("이벤트 아이디를 변경 시도하면 예외가 발생해야 한다.");
    }
}