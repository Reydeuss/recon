package io.github.reydeuss.engine;

import io.github.reydeuss.check.AbstractCheckWorker;
import io.github.reydeuss.check.AmountUnalignedChecker;
import io.github.reydeuss.check.DatetimeUnalignedChecker;
import io.github.reydeuss.check.MissingAccrualChecker;
import io.github.reydeuss.model.AccrualLedgerEntry;
import io.github.reydeuss.model.CashLedgerEntry;
import io.github.reydeuss.result.Match;
import io.github.reydeuss.result.MatchType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Reconciler {

  private final Map<String, AccrualLedgerEntry> accrualLedger;
  private final List<Match> results;
  private final Iterable<CashLedgerEntry> cashStream;
  private final List<AbstractCheckWorker> pipeline = List.of(
    new MissingAccrualChecker(),
    new AmountUnalignedChecker(),
    new DatetimeUnalignedChecker()
  );

  public Reconciler(Map<String, AccrualLedgerEntry> accrualLedger, Iterable<CashLedgerEntry> cs) {
    this.accrualLedger = accrualLedger;
    this.results = new ArrayList<>();
    this.cashStream = cs;
  }

  public void run() {
    for (CashLedgerEntry cashEntry : this.cashStream) {
      this.processCashEntry(cashEntry);
    }

    this.finalizeReconciliation();
  }

  /**
   * Compares a cash entry to accrual map by running it through the defined pipeline.
   * @param cashEntry A row or cash entry
   */
  private void processCashEntry(CashLedgerEntry cashEntry) {
    for (AbstractCheckWorker worker : this.pipeline) {
      if (worker.check(this.accrualLedger, cashEntry) == false) {
        // Only mark as matched if the entry actually exists in our map
        AccrualLedgerEntry entry = accrualLedger.get(cashEntry.getTransactionId());
        if (entry != null) {
          entry.markMatched();
        }

        results.add(worker.getMatchResult());
        return;
      }
    }

    AccrualLedgerEntry entry = accrualLedger.get(cashEntry.getTransactionId());
    if (entry != null) {
      entry.markMatched();
    }
    results.add(new Match(MatchType.MATCHED, "Transaction " + cashEntry.getTransactionId() + " matched successfully."));
  }

  public void finalizeReconciliation() {
    for (AccrualLedgerEntry entry : accrualLedger.values()) {
      if (entry.isMatched() == false) {
        results.add(
          new Match(
            MatchType.CASH_ENTRY_MISSING,
            "MISSING CASH: Transaction " + entry.getTransactionId() + " found in accrual, not found in cash."
          )
        );
      }
    }
  }

  public List<Match> getResults() {
    return Collections.unmodifiableList(this.results);
  }
}
