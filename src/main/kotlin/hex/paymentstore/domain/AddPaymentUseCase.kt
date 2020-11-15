package hex.paymentstore.domain

import org.apache.commons.lang3.RandomStringUtils

class AddPaymentUseCase(private val repository: PaymentRepository) {
	fun add(payment: Payment): Payment {
		val id: String = RandomStringUtils.randomAlphanumeric(20)
		return repository.add(payment.copy(id = id))
	}
}