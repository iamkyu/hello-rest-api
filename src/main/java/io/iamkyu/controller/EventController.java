package io.iamkyu.controller;

import io.iamkyu.app.EventCreateRequest;
import io.iamkyu.app.EventCreateRequestValidator;
import io.iamkyu.app.EventResource;
import io.iamkyu.domain.Event;
import io.iamkyu.domain.EventRepository;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

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

        Event event = modelMapper.map(createRequest, Event.class);
        event.adjust();
        Event savedEvent = eventRepository.save(event);

        ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(savedEvent.getId());
        URI uri = selfLinkBuilder.toUri();

        EventResource resource = new EventResource(event);
        resource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        resource.add(linkTo(EventController.class).withRel("query-events"));
        resource.add(selfLinkBuilder.withRel("update-event"));

        return ResponseEntity.created(uri).body(resource);
    }
}
