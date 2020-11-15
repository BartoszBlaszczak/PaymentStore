package hex.paymentstore.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import hex.paymentstore.adapter.RestPayment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentStoreApplicationTests {
	private final MockMvc mockMvc;
	private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new ParameterNamesModule());
	
	@Autowired public PaymentStoreApplicationTests(MockMvc mockMvc, SpringConfiguration configuration) {
		this.mockMvc = mockMvc;
		new File(configuration.getRepositoryCsvFilePath()).deleteOnExit();
	}
	
	@Test
	void contextLoads() {}
	
	@Test
	void shouldPerformCrudOperations() throws Exception {
		//step 1 - try add incorrect new payment
		RestPayment incorrectPayment = new RestPayment(null, null, "PLN", "userId", "accountNumber");
		tryAddPayment(incorrectPayment).andExpect(status().isBadRequest());
		
		//step 2 - add new payments
		RestPayment payment1 = addPayment();
		RestPayment payment2 = addPayment();
		
		//step 3 - update payment
		RestPayment payment2Updated = updatePayment(new RestPayment(payment2.id(), BigDecimal.TEN, "PLN", "userId", "accountNumber"));
		
		//step 3 - find payment
		assertEquals(getPayment(payment1.id()), payment1);
		assertEquals(getPayment(payment2.id()), payment2Updated);
		assertTrue(getAllPayments().containsAll(List.of(payment1, payment2Updated)));
		
		//step 4 - delete payment and verify it
		deletePayment(payment1.id());
		assertFalse(getAllPayments().contains(payment1));
		tryGetPayment(payment1.id()).andExpect(status().isNotFound());
	}
	
	private RestPayment payment() {
		return new RestPayment(null, BigDecimal.ONE, "PLN", "userId", "accountNumber");
	}
	
	private ResultActions tryAddPayment(RestPayment payment) throws Exception {
		return mockMvc.perform(post("/")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(payment)));
	}
	
	private RestPayment addPayment() throws Exception {
		String response = tryAddPayment(payment())
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
		return objectMapper.readValue(response, RestPayment.class);
	}
	
	private ResultActions tryGetPayment(String id) throws Exception {
		return mockMvc.perform(get("/" + id));
	}
	
	private RestPayment getPayment(String id) throws Exception {
		String response = tryGetPayment(id)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		return objectMapper.readValue(response, RestPayment.class);
	}
	
	private List<RestPayment> getAllPayments() throws Exception {
		String response = mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		return objectMapper.readValue(response, new TypeReference<>() {});
	}
	
	private RestPayment updatePayment(RestPayment payment) throws Exception {
		String response = mockMvc.perform(patch("/")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(payment)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		return objectMapper.readValue(response, RestPayment.class);
	}
	
	private ResultActions deletePayment(String id) throws Exception {
		return mockMvc.perform(delete("/" + id))
				.andExpect(status().isOk());
	}
}
