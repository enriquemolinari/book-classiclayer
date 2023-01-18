package layer.web;

import java.util.List;

public record ReservationRequest(Long ids, Long idu, List<Long> seats) {

}
