package io.iamkyu.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import io.iamkyu.app.EventCreateRequest;
import io.iamkyu.app.EventCreateRequestValidator;
import io.iamkyu.domain.Event;
import io.iamkyu.domain.EventRepository;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventCreateRequestValidator validator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper,
                           EventCreateRequestValidator validator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.validator = validator;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventCreateRequest createRequest,
                                             Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        validator.validate(createRequest, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Event savedEvent = eventRepository.save(modelMapper.map(createRequest, Event.class));
        URI uri = linkTo(EventController.class)
                .slash(savedEvent.getId())
                .toUri();

        return ResponseEntity.created(uri).body(savedEvent);
    }
}
