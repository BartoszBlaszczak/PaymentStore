package hex.paymentstore.adapter

import hex.paymentstore.domain.AddPaymentUseCase
import hex.paymentstore.domain.PaymentRepository
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.validation.Valid

@RestController
class PaymentController(
	private val repository: PaymentRepository,
	private val addPaymentUseCase: AddPaymentUseCase
){
	
	@PostMapping
	fun add(@Valid @RequestBody payment: RestPayment, errors: Errors): ResponseEntity<Any> {
		if(errors.hasErrors()) {
			return ResponseEntity.badRequest().body(errors.getAllErrors().map { it.defaultMessage }.toString())
		}
		val body = addPaymentUseCase.add(payment.toDomain()).let(::RestPayment)
		return ResponseEntity(body, CREATED)
	}
	
	@GetMapping
	fun getPayments(): List<RestPayment> = repository.getAll().map(::RestPayment)
	
	
	@GetMapping("/{id}")
	fun getPayment(@PathVariable id: String): RestPayment =
			repository.find(id)
					?.let(::RestPayment)
					?: throw ResponseStatusException(NOT_FOUND, "Unable to find payment with id=$id")
	
	
	@DeleteMapping("/{id}")
	fun delete(@PathVariable id: String) = repository.remove(id)
}