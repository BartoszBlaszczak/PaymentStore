package hex.paymentstore.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import hex.paymentstore.adapter.RestPayment
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.io.File
import java.math.BigDecimal


@SpringBootTest
@AutoConfigureMockMvc
class PaymentStoreApplicationTests(
        @Autowired private val mockMvc: MockMvc,
        @Autowired configuration: SpringConfiguration
)
{
    init { File(configuration.repositoryCsvFilePath).deleteOnExit() }
    private val objectMapper: ObjectMapper = ObjectMapper().registerModule(ParameterNamesModule())
    
    @Test fun contextLoads() {}
    
    @Test fun shouldPerformCrudOperations() {
        //step 1 - try add incorrect new payment
        tryAddPayment(payment().copy(amount = null)).andExpect(status().isBadRequest)
        
        //step 2 - add new payments
        val payment1 = addPayment()
        val payment2 = addPayment()
        
        //step 3 - update payment
        val payment2Updated = updatePayment(payment2.copy(amount = "2".toBigDecimal()))!!
    
        //step 4 - find payment
        getPayment(payment1.id!!) shouldBe payment1
        getPayment(payment2Updated.id!!) shouldBe payment2Updated
        getAllPayments() shouldContainAll listOf(payment1, payment2Updated)
    
        //step 5 - delete payment and verify it
        deletePayment(payment1.id!!)
        getAllPayments() shouldNotContain payment1
        tryGetPayment(payment1.id!!).andExpect(status().isNotFound)
    }
    
    private fun payment() = RestPayment(null, BigDecimal.TEN, "PLN", "userId", "accountNumber")
    
    private fun tryAddPayment(payment: RestPayment = payment()): ResultActions =
            mockMvc.perform(post("/")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payment)))
    
    private fun addPayment(): RestPayment {
        val response = tryAddPayment()
                .andExpect(status().isCreated)
                .andReturn().response.contentAsString
        return objectMapper.readValue(response, RestPayment::class.java)
    }
    
    private fun tryGetPayment(id: String): ResultActions = mockMvc.perform(get("/$id"))
    
    private fun getPayment(id: String): RestPayment {
        val response = tryGetPayment(id)
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
        return objectMapper.readValue(response, RestPayment::class.java)
    }
    
    private fun getAllPayments(): Array<RestPayment> {
        val response = mockMvc.perform(get("/"))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
        return objectMapper.readValue(response, Array<RestPayment>::class.java)
    }
    
    private fun updatePayment(payment: RestPayment): RestPayment? {
        val response = mockMvc.perform(patch("/")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
        return objectMapper.readValue(response, RestPayment::class.java)
    }
    
    private fun deletePayment(id: String) =
        mockMvc.perform(delete("/$id"))
                .andExpect(status().isOk)

}
