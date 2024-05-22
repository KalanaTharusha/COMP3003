package org.example;

import org.example.api.CalendarApi;
import org.example.models.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class CalendarApiImpl implements CalendarApi {
    private CalendarApp app;

    public CalendarApiImpl(CalendarApp app) {
        this.app = app;
    }

    @Override
    public void addEvent(Event event) {
        app.addEvent(event);
    }

    @Override
    public void addEvents(ArrayList<Event> eventList) {
        app.addEvents(eventList);
    }

    @Override
    public void createEvent(String title, LocalDateTime startingDate, int duration, Event.EventType eventType) {
    }

    @Override
    public void notifyEvent() {
        app.notifyEvent();
    }
}
