package layer.business;

import layer.business.api.CreditCardRecord;

public class SomePaymentProvider implements PaymentProvider {

  @Override
  public void charge(CreditCardRecord card, float amount) {
    // call to some payment provider API
  }

}
