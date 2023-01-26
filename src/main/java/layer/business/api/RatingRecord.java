package layer.business.api;

import java.util.List;

public record RatingRecord(Long total, String value, List<RateRecord> details) {

}
