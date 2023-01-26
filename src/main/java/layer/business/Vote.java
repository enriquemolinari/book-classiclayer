package layer.business;

import layer.business.api.CinemaException;

public class Vote {

  private int vote;

  public Vote(int vote) {
    if (vote < 1 || vote > 5) {
      throw new CinemaException("vote must be a value between 1 and 5");
    }
    this.vote = vote;
  }

  public int val() {
    return this.vote;
  }
}
