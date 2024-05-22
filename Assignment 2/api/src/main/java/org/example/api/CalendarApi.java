package org.example.api;

import org.example.models.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;

public interface CalendarApi {
    void addEvent(Event event);
    void addEvents(ArrayList<Event> eventList);
    void createEvent(String title, LocalDateTime startingDate, int duration, Event.EventType eventType);
    void notifyEvent();
}
