package layer.business;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeFormatted {

  private static String format = "HH:mm";
  private LocalDateTime dateTime;

  public TimeFormatted(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  public String toString() {
    return this.dateTime.format(DateTimeFormatter.ofPattern(format));
  }

}
