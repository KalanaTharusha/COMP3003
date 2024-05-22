package org.example.api;

import org.example.models.Event;

public interface CalendarPlugin {
    void startPlugin(CalendarApi api);
    void onEvent(Event event);
}
