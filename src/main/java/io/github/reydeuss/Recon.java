package io.github.reydeuss;

import io.github.reydeuss.engine.Reconciler;
import io.github.reydeuss.model.AccrualLedgerEntry;
import io.github.reydeuss.store.load.AbstractLoader;
import io.github.reydeuss.store.load.CsvLoader;
import io.github.reydeuss.store.persist.AbstractPersister;
import io.github.reydeuss.store.persist.CsvPersister;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
  name = "recon",
  version = "rolling",
  mixinStandardHelpOptions = true,
  description = "Stream-based financial reconciliation engine.",
  header = "Reconciliation Utility"
)
public class Recon implements Runnable {

  @Option(names = { "--accrual" }, description = "Accrual Ledger CSV path", required = true, paramLabel = "<file>")
  private String accrualLedgerFilename;

  @Option(names = { "--cash" }, description = "Cash Ledger CSV path", required = true, paramLabel = "<file>")
  private String cashLedgerFilename;

  @Option(names = { "--output" }, description = "Results output path", required = true, paramLabel = "<file>")
  private String outputFilename;

  @Override
  public void run() {
    try {
      AbstractLoader loader = new CsvLoader(accrualLedgerFilename, cashLedgerFilename);

      Map<String, AccrualLedgerEntry> accrualLedger = loader.getAccrualLedger();
      Reconciler engine = new Reconciler(accrualLedger, loader.getCashStream());

      engine.run();

      AbstractPersister persister = new CsvPersister(Path.of(outputFilename));
      persister.saveResults(engine.getResults());

      System.out.printf("Reconciliation successful: %s%n", outputFilename);
    } catch (IOException e) {
      System.err.printf("IO Error: %s%n", e.getMessage());
      System.exit(1);
    } catch (Exception e) {
      System.err.printf("Unexpected Error: %s%n", e.getMessage());
      System.exit(1);
    }
  }

  public static void main(String[] args) {
    int exitCode = new CommandLine(new Recon()).execute(args);
    System.exit(exitCode);
  }
}
