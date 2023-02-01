package layer.web;

import java.util.List;
import layer.business.api.CreditCardRecord;

public record PaymentRequest(Long ids, List<Long> seats, String number,
    String name, String code) {

  CreditCardRecord toCreditCardRecord() {
    return new CreditCardRecord(number, name, code);
  }
}
