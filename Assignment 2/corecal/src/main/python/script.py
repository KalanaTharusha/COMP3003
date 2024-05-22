from org.example import CalendarApiImpl
from org.example.models import Event
from java.time import LocalDateTime, Month
e = Event("Independence Day - LK", LocalDateTime.of(2023, Month.FEBRUARY, 4, 0, 0), 0, Event.EventType.ALL_DAY)
api.addEvent(e)

e = Event("New Year - LK", LocalDateTime.of(2023, Month.APRIL, 14, 0, 0), 0, Event.EventType.ALL_DAY)
api.addEvent(e)

e = Event("New Year - AU", LocalDateTime.of(2023, Month.JANUARY, 1, 0, 0), 0, Event.EventType.ALL_DAY)
api.addEvent(e)

e = Event("Vesak", LocalDateTime.of(2024, Month.MAY, 23, 0, 0), 0, Event.EventType.ALL_DAY)
api.addEvent(e)

e = Event("Christmas Day", LocalDateTime.of(2023, Month.DECEMBER, 25, 0, 0), 0, Event.EventType.ALL_DAY)
api.addEvent(e)

e = Event("Thanksgiving Day", LocalDateTime.of(2023, Month.NOVEMBER, 23, 0, 0), 0, Event.EventType.ALL_DAY)
api.addEvent(e)