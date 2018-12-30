package io.iamkyu.controller;

import io.iamkyu.app.ErrorsResource;
import io.iamkyu.app.EventCreateRequest;
import io.iamkyu.app.EventCreateRequestValidator;
import io.iamkyu.app.EventResource;
import io.iamkyu.app.EventUpdateRequest;
import io.iamkyu.app.EventUpdateRequestValidator;
import io.iamkyu.domain.Event;
import io.iamkyu.domain.EventRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventCreateRequestValidator createRequestValidator;
    private final EventUpdateRequestValidator updateRequestValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper,
                           EventCreateRequestValidator createRequestValidator,
                           EventUpdateRequestValidator updateRequestValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.createRequestValidator = createRequestValidator;
        this.updateRequestValidator = updateRequestValidator;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventCreateRequest createRequest,
                                      Errors errors) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        createRequestValidator.validate(createRequest, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
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

    @GetMapping
    public ResponseEntity getEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        Page<Event> pages = eventRepository.findAll(pageable);
        PagedResources resource = assembler.toResource(pages, event -> new EventResource(event));
        resource.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(resource);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id) {
        Optional<Event> optional = eventRepository.findById(id);
        if (!optional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        EventResource resource = new EventResource(optional.get());
        resource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventUpdateRequest updateRequest,
                                      Errors errors) {
        Optional<Event> optional = eventRepository.findById(id);
        if (!optional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        updateRequestValidator.validate(updateRequest, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event source = optional.get();
        modelMapper.map(updateRequest, source);
        eventRepository.save(source);

        EventResource resource = new EventResource(source);
        resource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));
        return ResponseEntity.ok(resource);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
