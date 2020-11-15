package hex.paymentstore.adapter

import hex.paymentstore.domain.BankAccountNumber
import hex.paymentstore.domain.Payment
import hex.paymentstore.domain.PaymentRepository
import hex.paymentstore.domain.UserId
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.csv.CSVRecord
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class PaymentRepositoryCsvFile(csvFilePath: String) : PaymentRepository {
	private val csvFile: File = File(csvFilePath)
	private val csvFormat: CSVFormat = CSVFormat.DEFAULT
	private val appender: CSVPrinter = CSVPrinter(FileWriter(csvFile, true), csvFormat)
	
	override fun add(payment: Payment): Payment {
		appender.printRecord(payment.id, payment.amount, payment.currency, payment.userId.value, payment.account.value)
		appender.flush()
		return payment
	}
	
	override fun getAll(): List<Payment> {
		return readFile().map(this::toPayment)
	}
	
	private fun readFile() = csvFormat.parse(FileReader(csvFile))
	
	override fun find(id: String): Payment? {
		readFile().iterator().forEach { if (it.get(0) == id) return toPayment(it) }
		return null
	}
	
	override fun remove(id: String) {
		val filteredContent = getAll().filter { it.id != id }.map(this::toRecord)
		val csvPrinter = CSVPrinter(FileWriter(csvFile), csvFormat)
		csvPrinter.printRecords(filteredContent)
		csvPrinter.flush()
	}
	
	private fun toPayment(csvRecord: CSVRecord) = Payment(
			id = csvRecord.get(0),
			amount = csvRecord.get(1).toBigDecimal(),
			currency = csvRecord.get(2),
			userId = UserId(csvRecord.get(3)),
			account = BankAccountNumber(csvRecord.get(4))
	)
	
	private fun toRecord(payment: Payment) =
			arrayOf(payment.id, payment.amount.toString(), payment.currency, payment.userId.value, payment.account.value)
}