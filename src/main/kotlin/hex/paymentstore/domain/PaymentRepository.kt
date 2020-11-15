package hex.paymentstore.domain

interface PaymentRepository {
	fun add(payment: Payment): Payment
	fun getAll(): List<Payment>
	fun find(id: String): Payment?
	fun remove(id: String)
}