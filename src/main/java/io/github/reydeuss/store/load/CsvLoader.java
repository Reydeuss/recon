package io.github.reydeuss.store.load;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.reydeuss.model.AccrualLedgerEntry;
import io.github.reydeuss.model.CashLedgerEntry;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CsvLoader extends AbstractLoader {

  private final CsvMapper mapper;
  private final Path accrualPath;
  private final Path cashPath;
  static final CsvSchema schema = CsvSchema.builder()
    .addColumn("transaction_id")
    .addColumn("destination")
    .addColumn("amount")
    .addColumn("timestamp")
    .setUseHeader(true)
    .setReorderColumns(true)
    .build();

  /**
   * The constructor takes the filepath to the ledger CSV files and runs several checks
   * such as determining the path's existence, read permissions, and if the path is a file.
   * @param accrualFilepath String of the filepath to the accrual ledger CSV file
   * @param cashFilepath String of the filepath to the cash ledger CSV file
   * @throws NoSuchFileException If the path does not exist
   * @throws AccessDeniedException If the file cannot be read
   * @throws FileSystemException If the file is a directory or a non-regular file
   */
  public CsvLoader(String accrualFilepath, String cashFilepath)
    throws NoSuchFileException, AccessDeniedException, FileSystemException {
    Path accrualPath = Path.of(accrualFilepath);
    Path cashPath = Path.of(cashFilepath);

    // If any of the files are invalid, the errors will be propagated immediately
    this.verifyFile(accrualPath);
    this.verifyFile(cashPath);

    this.accrualPath = accrualPath;
    this.cashPath = cashPath;

    mapper = new CsvMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, AccrualLedgerEntry> getAccrualLedger() throws IOException {
    Map<String, AccrualLedgerEntry> accrualLedger = new HashMap<>();
    ObjectReader reader = mapper.readerFor(AccrualLedgerEntry.class).with(schema.withHeader());

    try (
      BufferedReader file = Files.newBufferedReader(accrualPath);
      MappingIterator<AccrualLedgerEntry> it = reader.readValues(file);
    ) {
      while (it.hasNext()) {
        AccrualLedgerEntry entry = it.next();
        accrualLedger.put(entry.getTransactionId(), entry);
      }
    }

    return accrualLedger;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterable<CashLedgerEntry> getCashStream() {
    return () -> {
      try {
        ObjectReader reader = mapper.readerFor(CashLedgerEntry.class).with(schema.withHeader());
        return reader.readValues(Files.newBufferedReader(cashPath));
      } catch (IOException e) {
        throw new java.io.UncheckedIOException(e);
      }
    };
  }

  private void verifyFile(Path path) throws NoSuchFileException, AccessDeniedException, FileSystemException {
    if (Files.exists(path) == false) {
      throw new NoSuchFileException(path.toString());
    }

    if (Files.isReadable(path) == false) {
      throw new AccessDeniedException(path.toString());
    }

    if (Files.isRegularFile(path) == false) {
      throw new FileSystemException(path.toString(), null, "Is a directory or special file");
    }
  }
}
