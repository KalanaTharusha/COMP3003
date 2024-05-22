package org.example.models;

import java.time.LocalDateTime;

public class Event {
    private String title;
    private LocalDateTime startDateTime;
    private int duration;
    private EventType eventType;

    public Event(String title, LocalDateTime date, int duration, EventType eventType){
        this.title = title;
        this.startDateTime = date;
        this.duration = duration;
        this.eventType = eventType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }
    public int getDuration() {return duration;}
    public void setDuration(int duration) {this.duration = duration;}
    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public enum EventType {
        ALL_DAY,
        TIME_OF_DAY
    }
}
