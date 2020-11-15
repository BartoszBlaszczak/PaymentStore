package hex.paymentstore.domain

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.math.BigDecimal

class AddPaymentUseCaseTest : ShouldSpec({
  val repository = mockk<PaymentRepository>()
  val slot = slot<Payment>()
  every { repository.add(capture(slot)) } answers {slot.captured}
  val sut = AddPaymentUseCase(repository)
 
 should("add payment") {
  //given
  val payment = Payment("id", BigDecimal.TEN, "PLN", UserId("userId"), BankAccountNumber("123"))
  
  //when
  val added = sut.add(payment)
 
  //then
  added.id!!.length shouldBe 20
  added.amount shouldBe payment.amount
  added.currency shouldBe payment.currency
  added.userId shouldBe payment.userId
  added.account shouldBe payment.account
  
  verify { repository.add(added) }
 }
})
