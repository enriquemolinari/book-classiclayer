package layer.business;

import layer.business.api.CreditCardRecord;

public interface PaymentProvider {

  void charge(CreditCardRecord card, float amount);
}
