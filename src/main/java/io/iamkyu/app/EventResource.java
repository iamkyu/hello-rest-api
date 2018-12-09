package io.iamkyu.app;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import io.iamkyu.controller.EventController;
import io.iamkyu.domain.Event;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

public class EventResource extends Resource<Event> {
    public EventResource(Event content, Link... links) {
        super(content, links);
        add(linkTo(EventController.class).slash(content.getId()).withSelfRel());
    }
}
