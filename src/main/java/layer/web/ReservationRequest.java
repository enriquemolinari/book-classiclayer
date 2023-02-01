package layer.web;

import java.util.List;

public record ReservationRequest(Long ids, List<Long> seats) {

}
