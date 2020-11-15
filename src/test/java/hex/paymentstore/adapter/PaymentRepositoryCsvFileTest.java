package hex.paymentstore.adapter;

import hex.paymentstore.domain.BankAccountNumber;
import hex.paymentstore.domain.Payment;
import hex.paymentstore.domain.UserId;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PaymentRepositoryCsvFileTest {
	
	@Test
	void should_add_payment() throws IOException {
		//given
		Payment payment = new Payment("id", BigDecimal.TEN, "PLN", new UserId("userId"), new BankAccountNumber("123"));
		
		// when
		Payment added = sut().add(payment);
		
		// then
		assertEquals(added, payment);
	}
	
	@Test
	void should_get_all_payments() throws IOException {
		//given
		Payment payment1 = new Payment("1", BigDecimal.TEN, "PLN", new UserId("userId"), new BankAccountNumber("123"));
		Payment payment2 = new Payment("2", BigDecimal.TEN, "PLN", new UserId("userId"), new BankAccountNumber("123"));
		
		PaymentRepositoryCsvFile sut = sut();
		sut.add(payment1);
		sut.add(payment2);
		
		// when
		List<Payment> payments = sut.getAll();
		// then
		assertTrue(payments.containsAll(List.of(payment1, payment2)));
	}
	
	@Test
	void should_find_payment() throws IOException {
		//given
		PaymentRepositoryCsvFile sut = sut();
		Payment added = sut.add(new Payment("id", BigDecimal.TEN, "PLN", new UserId("userId"), new BankAccountNumber("123")));
		
		// when
		Optional<Payment> found = sut.find(added.id());
		// then
		assertTrue(found.isPresent());
		assertEquals(found.get(), added);
	}
	
	@Test
	void should_not_find_payment() throws IOException {
		// when
		Optional<Payment> found = sut().find("notExistingPayment");
		// then
		assertTrue(found.isEmpty());
	}
	
	@Test
	void should_remove_payment() throws IOException {
		//given
		PaymentRepositoryCsvFile sut = sut();
		Payment added = sut.add(new Payment("id", BigDecimal.TEN, "PLN", new UserId("userId"), new BankAccountNumber("123")));
		
		// when
		sut.remove(added.id());
		// then
		assertTrue(sut.find(added.id()).isEmpty());
	}
	
	private PaymentRepositoryCsvFile sut() throws IOException {
		File tempFile = File.createTempFile("tmp", ".csv");
		tempFile.deleteOnExit();
		return new PaymentRepositoryCsvFile(tempFile.getPath());
	}
}