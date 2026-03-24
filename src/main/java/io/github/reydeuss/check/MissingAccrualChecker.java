package io.github.reydeuss.check;

import io.github.reydeuss.model.AccrualLedgerEntry;
import io.github.reydeuss.model.CashLedgerEntry;
import io.github.reydeuss.result.Match;
import io.github.reydeuss.result.MatchType;
import java.util.Map;

public class MissingAccrualChecker extends AbstractCheckWorker {

  @Override
  public boolean check(
    Map<String, AccrualLedgerEntry> accrualLedger,
    CashLedgerEntry cashEntry
  ) {
    if (accrualLedger.containsKey(cashEntry.getTransactionId()) == false) {
      var infoString = String.format(
        "MISSING ACCRUAL: Transaction ID %s found in cash ledger, not found in accrual.",
        cashEntry.getTransactionId()
      );

      result = new Match(MatchType.ACCRUAL_ENTRY_MISSING, infoString);
      return false;
    }

    return true;
  }
}
