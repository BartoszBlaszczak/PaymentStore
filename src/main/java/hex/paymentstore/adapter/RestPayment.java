package hex.paymentstore.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import hex.paymentstore.domain.BankAccountNumber;
import hex.paymentstore.domain.Payment;
import hex.paymentstore.domain.UserId;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public record RestPayment(
		@JsonProperty("id")
		String id,
		
		@JsonProperty("amount")
		@NotNull(message = "amount is required")
		BigDecimal amount,
		
		@JsonProperty("currency")
		@NotNull(message = "currency is required")
		@Size(min=3, max=3, message = "currency should be 3 characters length")
		String currency,
		
		@JsonProperty("userId")
		@NotNull(message = "userId is required")
		String userId,
		
		@JsonProperty("account")
		@NotNull(message = "account is required")
		String account
) {
	public RestPayment(Payment payment) {
		this(payment.id(), payment.amount(), payment.currency(), payment.userId().value(), payment.account().value());
	}
	
	public Payment toDomain() {
		return new Payment(id, amount, currency, new UserId(userId), new BankAccountNumber(account));
	}
}
