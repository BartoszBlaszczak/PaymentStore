package hex.paymentstore.domain;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AddPaymentUseCaseTest {
	private final PaymentRepository repository = Mockito.mock(PaymentRepository.class);
	private final ArgumentCaptor<Payment> argumentCaptor = ArgumentCaptor.forClass(Payment.class);
	private final AddPaymentUseCase sut = new AddPaymentUseCase(repository);
	
	@Test
	void should_add_payment() {
		//given
		when(repository.add(argumentCaptor.capture())).thenAnswer((Answer<Payment>) invocation -> argumentCaptor.getValue());
		Payment payment = new Payment("id", BigDecimal.TEN, "PLN", new UserId("userId"), new BankAccountNumber("123"));
		
		//when
		Payment added = sut.add(payment);
		
		//then
		assertEquals(added.id().length(), 20);
		assertEquals(added.amount(), payment.amount());
		assertEquals(added.currency(), payment.currency());
		assertEquals(added.userId(), payment.userId());
		assertEquals(added.account(), payment.account());
		
		verify(repository).add(added);
	}
}