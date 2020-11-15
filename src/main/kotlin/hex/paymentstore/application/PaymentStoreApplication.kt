package hex.paymentstore.application

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PaymentStoreApplication

fun main(args: Array<String>) {
    runApplication<PaymentStoreApplication>(*args)
}
