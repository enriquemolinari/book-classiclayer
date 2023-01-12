package layer.business;

import java.time.Duration;

public class MovieDurationFormat {

  private int duration;

  public MovieDurationFormat(int duration) {
    this.duration = duration;
  }

  public String val() {
    var duration = Duration.ofMinutes(Long.valueOf(this.duration));
    var description = "%dhr";

    if (duration.toHours() > 1) {
      description += "s";
    }

    description += " %02dmin";

    if (duration.toMinutesPart() > 1) {
      description += "s";
    }

    return String.format(description, duration.toHours(),
        duration.toMinutesPart());
  }
}
