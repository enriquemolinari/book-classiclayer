package layer.data.api;

import java.util.List;

public record RatingData(Long totalVotes, Float value,
    List<RatingDetail> ratingDetail) {

}
