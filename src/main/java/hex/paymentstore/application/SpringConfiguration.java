package hex.paymentstore.application;

import hex.paymentstore.adapter.PaymentStoreController;
import hex.paymentstore.adapter.PaymentRepositoryCsvFile;
import hex.paymentstore.domain.AddPaymentUseCase;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@ConfigurationProperties(prefix = "paymentstore")
public class SpringConfiguration {
	private String repositoryCsvFilePath;
	
	public String getRepositoryCsvFilePath() {
		return repositoryCsvFilePath;
	}
	
	public void setRepositoryCsvFilePath(String repositoryCsvFilePath) {
		this.repositoryCsvFilePath = repositoryCsvFilePath;
	}
	
	
	@Bean
	public PaymentStoreController controller() throws IOException {
		return new PaymentStoreController(repository(), addPaymentUseCase());
	}
	
	@Bean
	public PaymentRepositoryCsvFile repository() throws IOException {
		return new PaymentRepositoryCsvFile(repositoryCsvFilePath);
	}
	
	@Bean
	public AddPaymentUseCase addPaymentUseCase() throws IOException {
		return new AddPaymentUseCase(repository());
	}
}
