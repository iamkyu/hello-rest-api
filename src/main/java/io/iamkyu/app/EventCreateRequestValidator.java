package io.iamkyu.app;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventCreateRequestValidator {
    public void validate(EventCreateRequest request, Errors errors) {
        validatePrice(request, errors);
        validateEndDate(request, errors);
    }

    private void validatePrice(EventCreateRequest request, Errors errors) {
        if (request.getBasePrice() > request.getMaxPrice() && request.getMaxPrice() > 0) {
            errors.rejectValue("basePrice", "wrongValue", "BasePrice is Wrong.");
            errors.rejectValue("maxPrice", "wrongValue", "MaxPrice is Wrong.");
            errors.reject("Wrong Prices", "Values for Prices are wrong");
        }
    }

    private void validateEndDate(EventCreateRequest request, Errors errors) {
        LocalDateTime endEventDateTime = request.getEndEventDateTime();
        if (endEventDateTime.isBefore(request.getBeginEventDateTime())
                || endEventDateTime.isBefore(request.getBeginEnrollmentDateTime())
                || endEventDateTime.isBefore(request.getCloseEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", "wrongValue", "EndEventTime is Wrong.");
        }
    }

    // TODO beginEventDateTime 검증
    // TODO closeEnrollementDateTime 검증
}
