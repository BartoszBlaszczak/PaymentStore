package hex.paymentstore.application

import hex.paymentstore.adapter.PaymentController
import hex.paymentstore.adapter.PaymentRepositoryCsvFile
import hex.paymentstore.domain.AddPaymentUseCase
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@Configuration
@ConfigurationProperties(prefix = "paymentstore")
class SpringConfiguration {
	var repositoryCsvFilePath:String = ""
	
	@Bean fun controller(): PaymentController = PaymentController(repository(), addPaymentUseCase())
	@Bean fun repository(): PaymentRepositoryCsvFile = PaymentRepositoryCsvFile(repositoryCsvFilePath)
	@Bean fun addPaymentUseCase(): AddPaymentUseCase = AddPaymentUseCase(repository())
	
	@Bean fun api(): Docket? = Docket(DocumentationType.SWAGGER_2)
			.select()
			.apis(RequestHandlerSelectors.any())
			.paths(PathSelectors.any())
			.build()
}