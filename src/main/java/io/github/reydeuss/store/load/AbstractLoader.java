package io.github.reydeuss.store.load;

import io.github.reydeuss.model.AccrualLedgerEntry;
import io.github.reydeuss.model.CashLedgerEntry;
import java.io.IOException;
import java.util.Map;

public abstract class AbstractLoader {

  /**
   * Loads and maps accrual ledger entries into a Map for random access by transaction ID.
   * Implementation should ensure the entire dataset is loaded into memory.
   * * @return A Map where the key is the transaction ID and the value is the AccrualLedgerEntry
   * @throws IOException If a filesystem or restricted access error occurs during loading
   */
  public abstract Map<String, AccrualLedgerEntry> getAccrualLedger() throws IOException;

  /**
   * Provides a memory-efficient stream of cash ledger entries.
   * Implementation must return a one-shot {@link Iterable} that processes data
   * row-by-row to maintain $O(1)$ memory complexity relative to the file size.
   * * @return A one-shot Iterable of CashLedgerEntry
   * @throws java.io.UncheckedIOException If an underlying I/O error occurs during stream initialization
   */
  public abstract Iterable<CashLedgerEntry> getCashStream();
}
