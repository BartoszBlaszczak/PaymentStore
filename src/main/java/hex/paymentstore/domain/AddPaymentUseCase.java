package hex.paymentstore.domain;

import org.apache.commons.lang3.RandomStringUtils;

public class AddPaymentUseCase {
	private final PaymentRepository repository;
	
	public AddPaymentUseCase(PaymentRepository repository) {
		this.repository = repository;
	}
	
	public Payment add(Payment payment) {
		String id = RandomStringUtils.randomAlphanumeric(20);
		return repository.add(payment.copy(id));
	}
}
