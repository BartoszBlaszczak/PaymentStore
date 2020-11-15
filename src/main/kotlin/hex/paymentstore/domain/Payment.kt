package hex.paymentstore.domain

import java.math.BigDecimal

data class Payment(val id: String?, val amount: BigDecimal, val currency: String, val userId: UserId, val account: BankAccountNumber)