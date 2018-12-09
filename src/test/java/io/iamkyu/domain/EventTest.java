package io.iamkyu.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import io.iamkyu.common.TestDescription;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

@RunWith(JUnitParamsRunner.class)
public class EventTest {

    @Test
    public void buildEvent() {
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

    @Test
    @Parameters
    @TestDescription("최소, 최대 가격이 0이면 무료이벤트이여야 한다")
    public void testFree(TestFreeParameter param) {
        //given
        Event event = Event.builder()
                .basePrice(param.getBasePrice())
                .maxPrice(param.getMaxPrice())
                .build();

        //when
        event.adjust();

        //then
        assertThat(event.isFree()).isEqualTo(param.isExpectedIsFree());
    }

    @SuppressWarnings("unused")
    private List<TestFreeParameter> parametersForTestFree() {
        return Arrays.asList(
                new TestFreeParameter(0, 0, true),
                new TestFreeParameter(100, 0, false),
                new TestFreeParameter(0, 100, false),
                new TestFreeParameter(100, 200, false)
        );
    }

    @Test
    @Parameters
    @TestDescription("장소가 명시되면 오프라인이벤트이여야 한다")
    public void testOffline(TestOfflineParameter param) {
        //given
        Event event = Event.builder()
                .location(param.getLocation())
                .build();

        //when
        event.adjust();

        //then
        assertThat(event.isOffline()).isEqualTo(param.isExpectedIsOffline());
    }

    @SuppressWarnings("unused")
    private List<TestOfflineParameter> parametersForTestOffline() {
        return Arrays.asList(
                new TestOfflineParameter("강남", true),
                new TestOfflineParameter(null, false),
                new TestOfflineParameter("      ", false)
        );
    }

    private static class TestFreeParameter {
        private int basePrice;
        private int maxPrice;
        private boolean expectedIsFree;

        public TestFreeParameter(int basePrice, int maxPrice, boolean expectedIsFree) {
            this.basePrice = basePrice;
            this.maxPrice = maxPrice;
            this.expectedIsFree = expectedIsFree;
        }

        public int getBasePrice() {
            return basePrice;
        }

        public int getMaxPrice() {
            return maxPrice;
        }

        public boolean isExpectedIsFree() {
            return expectedIsFree;
        }
    }

    private static class TestOfflineParameter {
        private String location;
        private boolean expectedIsOffline;

        public TestOfflineParameter(String location, boolean expectedIsOffline) {
            this.location = location;
            this.expectedIsOffline = expectedIsOffline;
        }

        public String getLocation() {
            return location;
        }

        public boolean isExpectedIsOffline() {
            return expectedIsOffline;
        }
    }
}