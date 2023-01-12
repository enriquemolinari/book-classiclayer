package layer.data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

class ToLocalDate {

  private Timestamp time;

  public ToLocalDate(Object time) {
    this.time = (Timestamp) time;
  }

  public LocalDateTime val() {
    return LocalDateTime.ofInstant((this.time).toInstant(),
        ZoneOffset.systemDefault());
  }
}
