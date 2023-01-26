package layer.business.api;

import java.util.List;

public record RatingRecord(Long total, float value, List<RateRecord> details) {

}
