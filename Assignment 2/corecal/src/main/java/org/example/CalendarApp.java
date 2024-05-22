package org.example;

import edu.curtin.terminalgrid.TerminalGrid;
import org.example.api.CalendarPlugin;
import org.example.models.Event;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CalendarApp {

    private static List<Event> eventList = new ArrayList<>();
    private static List<String> scriptsList = new ArrayList<>();
    private static Locale locale = Locale.getDefault();
    private static ResourceBundle bundle = ResourceBundle.getBundle("bundle", locale);
    private static List<CalendarPlugin> plugins = new ArrayList<>();
    private static ScheduledExecutorService eventNotificationService = new ScheduledThreadPoolExecutor(1);

    public CalendarApp() {
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No calendar file provided.");
            System.out.println("Usage: ./gradlew run --args=\"../calendarfile.cal\"");
        } else {
            eventList.add(new Event("notification event", LocalDateTime.of(2023, Month.OCTOBER, 25, 19, 36), 0, Event.EventType.TIME_OF_DAY));
            CalendarApp calendarApp = new CalendarApp();
            calendarApp.parseFile(args[0]);
            calendarApp.addPlugins();
            calendarApp.readScript();
            calendarApp.notifyEvent();
            calendarApp.start();
            eventNotificationService.shutdownNow();
        }
    }

    public void start() {
        LocalDate currentDate = LocalDate.now();
        displayMenu(currentDate);
    }

    public static void displayMenu(LocalDate currentDate) {
        Scanner scanner = new Scanner(System.in);

        boolean exit = false;

        while (!exit) {

            displayCalendar(currentDate, eventList);

            System.out.println("\n" + bundle.getString("menu") + ":");
            System.out.println("+d -> " + bundle.getString("go_forward_1_day"));
            System.out.println("+w -> " + bundle.getString("go_forward_1_week"));
            System.out.println("+m -> " + bundle.getString("go_forward_1_month"));
            System.out.println("+y -> " + bundle.getString("go_forward_1_year"));
            System.out.println("-d -> " + bundle.getString("go_backward_1_day"));
            System.out.println("-w -> " + bundle.getString("go_backward_1_week"));
            System.out.println("-m -> " + bundle.getString("go_backward_1_month"));
            System.out.println("-y -> " + bundle.getString("go_backward_1_year"));
            System.out.println(" t -> " + bundle.getString("go_today"));
            System.out.println(" s -> " + bundle.getString("search"));
            System.out.println(" i -> " + bundle.getString("language"));
            System.out.println(" q -> " + bundle.getString("exit"));
            System.out.print(bundle.getString("enter_your_choice"));

            String choice = scanner.next();
            switch (choice) {
                case "+d":
                    currentDate = currentDate.plusDays(1);
                    break;
                case "+w":
                    currentDate = currentDate.plusWeeks(1);
                    break;
                case "+m":
                    currentDate = currentDate.plusMonths(1);
                    break;
                case "+y":
                    currentDate = currentDate.plusYears(1);
                    break;
                case "-d":
                    currentDate = currentDate.minusDays(1);
                    break;
                case "-w":
                    currentDate = currentDate.minusWeeks(1);
                    break;
                case "-m":
                    currentDate = currentDate.minusMonths(1);
                    break;
                case "-y":
                    currentDate = currentDate.minusYears(1);
                    break;
                case "t":
                    currentDate = LocalDate.now();
                    break;
                case "s":
                    System.out.println("\nEnter the event name: ");
                    String term = scanner.next();
                    LocalDate eventDate = search(term);
                    if (eventDate != null) {
                        currentDate = eventDate;
                    }
                    break;
                case "i":
                    changeLocal();
                    break;
                case "q":
                    exit = true;
                    System.out.println("\nGoodbye!");
                    break;
                default:
                    System.out.println("\nInvalid choice.");
                    break;
            }
        }
        scanner.close();
    }

    public static void displayCalendar(LocalDate startingDate, List<Event> events) {

        List<Event> filteredEvents = new ArrayList<Event>(); // events in the current week
        List<Event> allDayEvents = new ArrayList<>();
        List<LocalDate> filteredDates = new ArrayList<>(); // time slots for the current week
        List<LocalTime> filteredTimes = new ArrayList<>();
        var terminalGrid = TerminalGrid.create(); // terminal grid

        // Date Formats
        DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("yyyy-MM-dd", locale);
        DateTimeFormatter dtfDay = DateTimeFormatter.ofPattern("d", locale);
        DateTimeFormatter dtfWeek = DateTimeFormatter.ofPattern("E", locale);
        DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("HH:00", locale);


        System.out.println("\n" + bundle.getString("week_title") + " " + startingDate.format(dtfDate) + ": \n");

        // filter events for current week
        for (int i = 0; i < 7; i++) {
            filteredDates.add(startingDate);

            for (Event event : events) {
                String eDate = event.getStartDateTime().toLocalDate().toString();
                String cDate = startingDate.format(dtfDate);
                if (cDate.equals(eDate)) {
                    filteredEvents.add(event);
                }
            }
            // next date
            startingDate = startingDate.plusDays(1);
        }

        // reset date
        startingDate = startingDate.minusDays(7);

        // filter time slots
        for (Event e : filteredEvents) {
            if (e.getEventType().equals(Event.EventType.TIME_OF_DAY)) {
                LocalTime timeToAdd = e.getStartDateTime().toLocalTime();
                if(!filteredTimes.contains(timeToAdd)){
                    filteredTimes.add(timeToAdd);
                }
            } else {
                allDayEvents.add(e);
            }
        }


        // sort time slots
        Collections.sort(filteredTimes, Comparator.naturalOrder());

        // column headings
        List<String> colHeadings = new ArrayList();
        for (LocalDate ld : filteredDates) {
            colHeadings.add(ld.format(dtfWeek));
        }

        // row headings
        List<String> rowHeadings = new ArrayList();
        rowHeadings.add("");
        rowHeadings.add(bundle.getString("all_day"));
        for (LocalTime lt : filteredTimes) {
            rowHeadings.add(lt.format(dtfTime));
        }

        // month day row
        List<String> monthDayRow = new ArrayList();
        for (LocalDate ld : filteredDates) {
            monthDayRow.add(ld.format(dtfDay));
        }

        // all day event row
        List<String> allDayRow = new ArrayList<>();
        for (LocalDate ld : filteredDates) {
            String title = "-";
            for (Event e : allDayEvents) {
                if (e.getStartDateTime().format(dtfDate).equals(ld.format(dtfDate))) {
                    title = e.getTitle();
                    break;
                }
            }
            allDayRow.add(title);
        }

        // table cells
        List<List<String>> data = new ArrayList();
        data.add(monthDayRow);
        data.add(allDayRow);

        // time of day events
        for (LocalTime lt : filteredTimes) {
            List<String> itemRow = new ArrayList<>();
            for (LocalDate ld : filteredDates) {
                String title = "-";
                for (Event e : filteredEvents) {
                    if (e.getStartDateTime().format(dtfDate).equals(ld.format(dtfDate)) && e.getStartDateTime().toLocalTime().format(dtfTime).equals(lt.format(dtfTime))) {
                        title = e.getTitle();
                        break;
                    }
                }
                itemRow.add(title);
            }
            data.add(itemRow);
        }

        // set boarders
        terminalGrid.setCharset(java.nio.charset.Charset.forName("UTF-8"));
        terminalGrid.setBoxChars(new TerminalGrid.BoxChars(
                "\u2502 ", " \u250a ", " \u2502",
                "\u2500", "\u254c", "\u2500",
                "\u256d\u2500", "\u2500\u256e", "\u2570\u2500", "\u2500\u256f",
                "\u2500\u252c\u2500", "\u2500\u2534\u2500", "\u251c\u254c", "\u254c\u2524", "\u254c\u253c\u254c"));

        // print calendar
        terminalGrid.print(data, rowHeadings, colHeadings);
    }

    private static LocalDate search(String term) {
        Optional<Event> event = eventList.stream()
                .filter(e -> e.getTitle().toLowerCase().contains(term.toLowerCase()))
                .findFirst();
        if (event.isPresent()) {
            return event.get().getStartDateTime().toLocalDate();
        } else {
            System.out.println("\nNot found!");
            return null;
        }
    }

    private static void changeLocal() {
        Scanner scanner = new Scanner(System.in);
        boolean changed = false;
        while (!changed) {
            System.out.println("\n");
            System.out.println("en -> English");
            System.out.println("fr -> French");
            System.out.println("si -> Sinhalese");
            System.out.print(bundle.getString("enter_your_choice"));

            String choice = scanner.next();

            switch (choice) {
                case "en":
                    locale = new Locale("en");
                    bundle = ResourceBundle.getBundle("bundle", locale);
                    changed = true;
                    break;
                case "fr":
                    locale = new Locale("fr");
                    bundle = ResourceBundle.getBundle("bundle", locale);
                    changed = true;
                    break;
                case "si":
                    locale = new Locale("si", "LK");
                    bundle = ResourceBundle.getBundle("bundle", locale);
                    changed = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
        scanner.close();
    }

    public void addEvent(Event event) {
        this.eventList.add(event);
    }

    public void addEvents(List<Event> eventList) {
        eventList.addAll(eventList);
    }

    public void createEvent(String title, LocalDateTime startingDate, int duration, Event.EventType eventType) {
        Event event = new Event(title, startingDate, duration, eventType);
        eventList.add(event);
    }

    // subscribe plugins to get notifications from each event
    public void notifyEvent() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime formattedNow = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute());

        for (Event event : eventList) {
            LocalDateTime notifyingTime = event.getStartDateTime();
            Duration delay = Duration.between(formattedNow, notifyingTime);
            int delaySeconds = (int)delay.toSeconds();

            if (delaySeconds > 0) {
                for (CalendarPlugin plugin : plugins) {
                    ScheduledFuture<?> future = eventNotificationService.schedule(() -> {
                        plugin.onEvent(event);
                    }, delaySeconds, TimeUnit.SECONDS);
                }
            }
        }
    }

    public void addPlugins() {

        try {

            Class<?> pluginClass = Class.forName("edu.curtin.calplugins.Repeat");
            plugins.add((CalendarPlugin) pluginClass.getConstructor(String.class, LocalDateTime.class, int.class, Event.EventType.class, int.class).newInstance("repeat event", LocalDateTime.of(2023, Month.NOVEMBER, 21, 19, 36), 0, Event.EventType.TIME_OF_DAY, 20));

            pluginClass = Class.forName("edu.curtin.calplugins.Notify");
            plugins.add((CalendarPlugin) pluginClass.getConstructor(String.class).newInstance("notification event"));

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            System.out.println(e.getMessage());
        }

        CalendarApiImpl apiImpl = new CalendarApiImpl(this);

        for (CalendarPlugin plugin : plugins) {
            plugin.startPlugin(apiImpl);
        }
    }

    private void readScript() {

        PythonInterpreter interpreter = new PythonInterpreter(null, new PySystemState());
        interpreter.exec("from org.example import CalendarApiImpl");
        interpreter.exec("from org.example.models import Event");
        interpreter.exec("from java.time import LocalDateTime, Month");

        interpreter.set("api", this);
        for (String script : scriptsList) {
            interpreter.exec(script.substring(1, script.length() - 1));
        }

        interpreter.cleanup();
    }

    private void parseFile(String filename) {
        try {
            InputStream fs = new FileInputStream(filename);
            MyParser parser = new MyParser(fs);
            parser.parseFile();
            eventList.addAll(parser.getEvents());
            scriptsList = parser.getScripts();
            parser.getPlugins();

            System.out.println("Input valid");

            fs.close();

        } catch (ParseException e) {
            System.out.println("Parsing error!");
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
