package layer.business;

import layer.business.api.CinemaException;

public class RateComment {

  private String rateComment;

  public RateComment(String rateComment) {
    if (rateComment.length() > 500) {
      throw new CinemaException("rate comments must be less than 500 chars");
    }
    this.rateComment = rateComment;
  }

  public String val() {
    return this.rateComment;
  }

}
