package layer.business;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class DayTimeFormatted {

  private static String format = "MM/dd HH:mm";
  private LocalDateTime dateTime;

  public DayTimeFormatted(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  public String toString() {
    return this.dateTime.getDayOfWeek().name().substring(0, 1).toUpperCase()
        + this.dateTime.getDayOfWeek().name().substring(1).toLowerCase() + " "
        + this.dateTime.format(DateTimeFormatter.ofPattern(format));
  }
}
