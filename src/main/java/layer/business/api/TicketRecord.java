package layer.business.api;

import java.util.List;

public record TicketRecord(List<Integer> seats, String showDate,
    String movieName, float paidAmount, int userPoints) {

}
