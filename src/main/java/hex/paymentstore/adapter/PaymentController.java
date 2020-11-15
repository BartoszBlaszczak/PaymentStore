package hex.paymentstore.adapter;

import hex.paymentstore.domain.AddPaymentUseCase;
import hex.paymentstore.domain.PaymentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class PaymentController {
	private final PaymentRepository repository;
	private final AddPaymentUseCase addPaymentUseCase;
	
	public PaymentController(PaymentRepository repository, AddPaymentUseCase addPaymentUseCase) {
		this.repository = repository;
		this.addPaymentUseCase = addPaymentUseCase;
	}
	
	@PostMapping
	public ResponseEntity<?> add(@Valid @RequestBody RestPayment payment, Errors errors) {
		if(errors.hasErrors()) {
			return ResponseEntity.badRequest().body(errors.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining()));
		}
		RestPayment body = new RestPayment(addPaymentUseCase.add(payment.toDomain()));
		return new ResponseEntity<>(body, CREATED);
	}
	
	@GetMapping
	public List<RestPayment> getPayments() {
		return repository.getAll().stream().map(RestPayment::new).collect(toList());
	}
	
	@GetMapping("/{id}")
	public RestPayment getPayment(@PathVariable String id) {
		return repository.find(id)
				.map(RestPayment::new)
				.orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unable to find payment with id=$id"));
	}
	
	@DeleteMapping("/{id}")
	public void delete(@PathVariable String id) {
		repository.remove(id);
	}
}
