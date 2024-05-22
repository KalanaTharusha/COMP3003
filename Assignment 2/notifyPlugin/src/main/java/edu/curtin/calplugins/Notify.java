package edu.curtin.calplugins;

import org.example.api.CalendarApi;
import org.example.api.CalendarPlugin;
import org.example.models.Event;

public class Notify implements CalendarPlugin {
    private String subscribedEvent;
    public Notify(String subscribedEvent){
        this.subscribedEvent = subscribedEvent;
    }
    @Override
    public void startPlugin(CalendarApi api) {
    }

    @Override
    public void onEvent(Event event) {
        // each event notifications
//        System.out.println(event.getTitle() + "Started now");

        if (event.getTitle().equals(subscribedEvent)) {
            subscribedEventInfo(event);
        }
    }

    // subscribed event info
    public void subscribedEventInfo(Event event) {
        System.out.println("\nEvent Notification");
        System.out.println(event.getTitle());
        System.out.println("Started at " + event.getStartDateTime());
        System.out.println("Happening until " + event.getDuration());
    }
}
