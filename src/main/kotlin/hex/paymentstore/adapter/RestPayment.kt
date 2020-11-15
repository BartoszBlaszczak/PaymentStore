package hex.paymentstore.adapter

import hex.paymentstore.domain.BankAccountNumber
import hex.paymentstore.domain.Payment
import hex.paymentstore.domain.UserId
import java.math.BigDecimal

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class RestPayment(
	val id: String?,
	@field:NotNull(message = "amount is required")
	val amount: BigDecimal?,
	@field:NotNull(message = "currency is required")
	@field:Size(min=3, max=3, message = "currency should be 3 characters length")
	val currency: String?,
	@field:NotNull(message = "userId is required")
	val userId: String?,
	@field:NotNull(message = "account is required")
	val account: String?
) {
	constructor(payment: Payment) : this(payment.id, payment.amount, payment.currency, payment.userId.value, payment.account.value)
	
	fun toDomain(): Payment = Payment(null, amount!!, currency!!, UserId(userId!!), BankAccountNumber(account!!))
}