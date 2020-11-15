package hex.paymentstore.domain;

import java.math.BigDecimal;

public record Payment(String id, BigDecimal amount, String currency, UserId userId, BankAccountNumber account){
	public Payment copy(String id) {
		return new Payment(id, amount, currency, userId, account);
	}
}
