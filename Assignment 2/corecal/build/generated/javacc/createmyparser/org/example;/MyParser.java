/* Generated By:JavaCC: Do not edit this line. MyParser.java */
package org.example;
import org.example.Token;import org.example.models.Event;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.time.format.DateTimeFormatter;

public class MyParser implements MyParserConstants {
    private static ArrayList<Event> events;
    private static ArrayList<String> scripts;
    private static HashMap<String, List<Map<String, String>>> pluginInfoMap;

  static final public void parseFile() throws ParseException {
 events = new ArrayList<Event>();
 scripts = new ArrayList<String>();
 pluginInfoMap = new HashMap<String, List<Map<String, String>>>();
 Token title = null;
 Token date = null;
 Token time = null;
 Token duration = null;
 Token script;
 Token pluginId;
 Token allDay = null;
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EVENT:
      case SCRIPT:
      case PLUGIN:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EVENT:
        jj_consume_token(EVENT);
        date = jj_consume_token(DATE);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case ALLDAY:
          allDay = jj_consume_token(ALLDAY);
          break;
        default:
          jj_la1[1] = jj_gen;
          ;
        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case TIME:
          time = jj_consume_token(TIME);
          break;
        default:
          jj_la1[2] = jj_gen;
          ;
        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case DURATION:
          duration = jj_consume_token(DURATION);
          break;
        default:
          jj_la1[3] = jj_gen;
          ;
        }
        title = jj_consume_token(STRING);
          if(allDay == null)
          {
              LocalDateTime dateTime = LocalDateTime.parse(date.image+ " " +time.image, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
              events.add(new Event(title.image, dateTime, Integer.parseInt(duration.image), Event.EventType.TIME_OF_DAY));
          } else {
              LocalDateTime dateTime = LocalDateTime.parse(date.image+ " " +"00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
              events.add(new Event(title.image, dateTime, 24, Event.EventType.ALL_DAY));
          }
          allDay = null;
        break;
      case SCRIPT:
        jj_consume_token(SCRIPT);
        script = jj_consume_token(STRING);
      scripts.add(script.toString().replaceAll("\u005c"\u005c"", "\u005c""));
        break;
      case PLUGIN:
        jj_consume_token(PLUGIN);
        pluginId = jj_consume_token(ID);
        jj_consume_token(ARGS);
      pluginInfoMap.put(pluginId.image, new ArrayList<Map<String, String>>());
      System.out.println(pluginId.image);
        break;
      default:
        jj_la1[4] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
  }

  static final public ArrayList<Event> getEvents() throws ParseException {
   {if (true) return events;}
    throw new Error("Missing return statement in function");
  }

  static final public ArrayList<String> getScripts() throws ParseException {
   {if (true) return scripts;}
    throw new Error("Missing return statement in function");
  }

  static final public HashMap<String, List<Map<String, String>>> getPlugins() throws ParseException {
   {if (true) return pluginInfoMap;}
    throw new Error("Missing return statement in function");
  }

  static private boolean jj_initialized_once = false;
  /** Generated Token Manager. */
  static public MyParserTokenManager token_source;
  static SimpleCharStream jj_input_stream;
  /** Current token. */
  static public Token token;
  /** Next token. */
  static public Token jj_nt;
  static private int jj_ntk;
  static private int jj_gen;
  static final private int[] jj_la1 = new int[5];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0xe0,0x8000,0x800,0x1000,0xe0,};
   }

  /** Constructor with InputStream. */
  public MyParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public MyParser(java.io.InputStream stream, String encoding) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new MyParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public MyParser(java.io.Reader stream) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new MyParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  static public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public MyParser(MyParserTokenManager tm) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(MyParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  static private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  static final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  static final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  static private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  static private java.util.List jj_expentries = new java.util.ArrayList();
  static private int[] jj_expentry;
  static private int jj_kind = -1;

  /** Generate ParseException. */
  static public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[16];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 5; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 16; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  static final public void enable_tracing() {
  }

  /** Disable tracing. */
  static final public void disable_tracing() {
  }

}