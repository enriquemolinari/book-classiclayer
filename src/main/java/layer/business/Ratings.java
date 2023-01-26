package layer.business;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import layer.business.api.RateRecord;
import layer.business.api.RatingRecord;
import layer.data.api.RatingDetail;

public class Ratings {

  private List<UserRate> userRates = new ArrayList<>();
  private float value;
  private long totalVotes;

  public Ratings(float value, Long totalVotes, List<RatingDetail> details) {
    for (RatingDetail rd : details) {
      userRates.add(
          new UserRate(rd.username(), rd.value(), rd.comment(), rd.votedAt()));
    }
    this.value = value;
    this.totalVotes = totalVotes;
  }

  List<RateRecord> detailsRecord() {
    return this.userRates.stream().map(ur -> {
      return ur.toRecord();
    }).collect(Collectors.toUnmodifiableList());
  }

  RatingRecord toRecord() {
    return new RatingRecord(totalVotes, value, this.detailsRecord());
  }

}
