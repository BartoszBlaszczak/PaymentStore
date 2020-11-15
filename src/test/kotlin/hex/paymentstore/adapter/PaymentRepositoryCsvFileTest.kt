package hex.paymentstore.adapter

import hex.paymentstore.domain.BankAccountNumber
import hex.paymentstore.domain.Payment
import hex.paymentstore.domain.UserId
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.containAll
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class PaymentRepositoryCsvFileTest : ShouldSpec({
	
	fun sut(): PaymentRepositoryCsvFile {
		val tempFile = createTempFile()
		tempFile.deleteOnExit()
		return PaymentRepositoryCsvFile(tempFile.path)
	}
	
	should("add payment") {
		val payment = Payment("id", BigDecimal.TEN, "PLN", UserId("userId"), BankAccountNumber("123"))
		
		// when
		val added = sut().add(payment)
		
		// then
		added shouldBe payment
	}
	
	should("get all payments") {
		val payment1 = Payment("1", BigDecimal.ONE, "PLN", UserId("userId"), BankAccountNumber("123"))
		val payment2 = Payment("2", BigDecimal.TEN, "PLN", UserId("userId"), BankAccountNumber("456"))
		val sut = sut()
		sut.add(payment1)
		sut.add(payment2)
		
		// when
		val payments = sut.getAll()
		// then
		payments should containAll(payment1, payment2)
	}
	
	should("get payment") {
		val payment = Payment("id", BigDecimal.TEN, "PLN", UserId("userId"), BankAccountNumber("123"))
		val sut = sut()
		sut.add(payment)
		
		// when
		val found = sut.find(payment.id!!)
		// then
		found shouldBe payment
	}
	
	should("not find payment") {
		// when
		val found = sut().find("notExistingPayment")
		// then
		found shouldBe null
	}
	
	should("remove payment") {
		val payment = Payment("id", BigDecimal.TEN, "PLN", UserId("userId"), BankAccountNumber("123"))
		val sut = sut()
		sut.add(payment)
		
		// when
		sut.remove(payment.id!!)
		// then
		sut.find(payment.id!!) shouldBe null
	}
})