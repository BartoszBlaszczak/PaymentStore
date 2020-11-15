package hex.paymentstore.adapter;

import hex.paymentstore.domain.BankAccountNumber;
import hex.paymentstore.domain.Payment;
import hex.paymentstore.domain.PaymentRepository;
import hex.paymentstore.domain.UserId;
import io.vavr.control.Try;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.StreamSupport;

import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toList;

public class PaymentRepositoryCsvFile implements PaymentRepository {
	private final CSVFormat csvFormat = CSVFormat.DEFAULT;
	private final File csvFile;
	private final CSVPrinter appender;
	
	public PaymentRepositoryCsvFile(String csvFilePath) throws IOException {
		csvFile = new File(csvFilePath);
		appender = new CSVPrinter(new FileWriter(csvFile, true), csvFormat);
	}
	
	@Override
	public Payment add(Payment payment) {
		Try.run(() -> {
			appender.printRecord(payment.id(), payment.amount(), payment.currency(), payment.userId().value(), payment.account().value());
			appender.flush();
		});
		return payment;
	}
	
	@Override
	public List<Payment> getAll() {
		return Try.of(() -> readFile().getRecords().stream().map(this::toPayment).collect(toList())).get();
	}
	
	private CSVParser readFile() {
		return Try.of(() -> csvFormat.parse(new FileReader(csvFile))).get();
	}
	
	@Override
	public Optional<Payment> find(String id) {
		return StreamSupport.stream(spliteratorUnknownSize(readFile().iterator(), Spliterator.ORDERED), false)
				.filter(record -> record.get(0).equals(id))
				.findFirst()
				.map(this::toPayment);
	}
	
	@Override
	public Optional<Payment> update(Payment payment) {
		List<Payment> all = getAll();
		return all.stream().filter(p -> p.id().equals(payment.id()))
				.findFirst()
				.map(found -> Try.of(() -> {
					Payment updated = found.update(payment);
					List<List<String>> toUpdate = all.stream().map(record -> record.id().equals(payment.id()) ? updated : record).map(this::toRecord).collect(toList());
					CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(csvFile), csvFormat);
					csvPrinter.printRecords(toUpdate);
					csvPrinter.flush();
					return updated;
				}).get());
	}
	
	@Override
	public void remove(String id) {
		List<List<String>> filteredContent = getAll().stream()
				.filter(p -> !p.id().equals(id))
				.map(this::toRecord)
				.collect(toList());
		Try.run(() -> {
			CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(csvFile), csvFormat);
			csvPrinter.printRecords(filteredContent);
			csvPrinter.flush();
		});
	}
	
	private Payment toPayment(CSVRecord csvRecord) {
		return new Payment(
				csvRecord.get(0),
				new BigDecimal(csvRecord.get(1)),
				csvRecord.get(2),
				new UserId(csvRecord.get(3)),
				new BankAccountNumber(csvRecord.get(4))
		);
	}
	
	private List<String> toRecord(Payment payment) {
		return List.of(payment.id(), payment.amount().toString(), payment.currency(), payment.userId().value(), payment.account().value());
	}
}
