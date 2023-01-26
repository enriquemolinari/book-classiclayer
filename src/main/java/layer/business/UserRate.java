package layer.business;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import layer.business.api.RateRecord;

public class UserRate {

  private String userName;
  private int vote;
  private String comment;
  private LocalDateTime commentAt;

  public UserRate(String userName, int vote, String comment) {
    this.userName = userName;
    this.vote = new Vote(vote).val();
    this.comment = new RateComment(comment).val();
    this.commentAt = LocalDateTime.now();
  }

  public UserRate(String userName, int value, String comment,
      LocalDateTime votedAt) {
    this(userName, value, comment);
    this.commentAt = votedAt;
  }

  RateRecord toRecord() {
    return new RateRecord(userName, vote, commentAt(), comment);
  }

  String commentAt() {
    return this.commentAt.format(DateTimeFormatter
        .ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.ENGLISH));
  }

}
