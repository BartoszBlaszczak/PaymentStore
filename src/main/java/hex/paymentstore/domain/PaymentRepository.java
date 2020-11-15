package hex.paymentstore.domain;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
	Payment add(Payment payment);
	List<Payment> getAll();
	Optional<Payment> find(String id);
	Optional<Payment> update(Payment payment);
	void remove(String id);
}
