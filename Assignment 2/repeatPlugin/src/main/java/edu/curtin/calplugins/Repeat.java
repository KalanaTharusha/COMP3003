package edu.curtin.calplugins;

import org.example.api.CalendarApi;
import org.example.api.CalendarPlugin;
import org.example.models.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

public class Repeat implements CalendarPlugin {
    private String title;
    private LocalDateTime startDateTime;
    private int duration;
    private Event.EventType eventType;
    private int repeat;

    public Repeat(String title, LocalDateTime date, int duration, Event.EventType eventType, int repeat){
        this.title = title;
        this.startDateTime = date;
        this.duration = duration;
        this.eventType = eventType;
        this.repeat = repeat;
    }
    @Override
    public void startPlugin(CalendarApi api) {
        LocalDateTime curr = startDateTime;
        LocalDateTime next = LocalDateTime.now().plusYears(1);

        while (curr.getYear() < next.getYear()) {
            api.addEvent(new Event(this.title, curr, this.duration, this.eventType));
            curr = curr.plusDays(repeat);
        }
    }

    @Override
    public void onEvent(Event event) {
        System.out.println("\n" + event.getTitle() + " Started now");
    }
}
