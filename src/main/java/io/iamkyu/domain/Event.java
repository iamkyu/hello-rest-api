package io.iamkyu.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
@Entity
public class Event {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location;
    private int basePrice;
    private int maxPrice;
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;

    public void setId(Integer id) {
        if (this.id != null) {
            throw new IllegalArgumentException("The id may not be changed.");
        }

        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBeginEnrollmentDateTime(LocalDateTime beginEnrollmentDateTime) {
        this.beginEnrollmentDateTime = beginEnrollmentDateTime;
    }

    public void setCloseEnrollmentDateTime(LocalDateTime closeEnrollmentDateTime) {
        this.closeEnrollmentDateTime = closeEnrollmentDateTime;
    }

    public void setBeginEventDateTime(LocalDateTime beginEventDateTime) {
        this.beginEventDateTime = beginEventDateTime;
    }

    public void setEndEventDateTime(LocalDateTime endEventDateTime) {
        this.endEventDateTime = endEventDateTime;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setBasePrice(int basePrice) {
        this.basePrice = basePrice;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    public void setLimitOfEnrollment(int limitOfEnrollment) {
        this.limitOfEnrollment = limitOfEnrollment;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }
}
