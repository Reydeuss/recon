package io.github.reydeuss.check;

import io.github.reydeuss.model.AccrualLedgerEntry;
import io.github.reydeuss.model.CashLedgerEntry;
import io.github.reydeuss.result.Match;
import io.github.reydeuss.result.MatchType;
import java.math.BigDecimal;
import java.util.Map;

public class AmountUnalignedChecker extends AbstractCheckWorker {

  private static final BigDecimal maxError = new BigDecimal("0.01");

  @Override
  public boolean check(
    Map<String, AccrualLedgerEntry> accrualLedger,
    CashLedgerEntry cashEntry
  ) {
    var accrualEntry = accrualLedger.get(cashEntry.getTransactionId());
    var accrualAmount = accrualEntry.getAmount();
    var cashAmount = cashEntry.getAmount();
    var diff = accrualAmount.subtract(cashAmount).abs();

    if (diff.compareTo(maxError) > 0) {
      String infoString =
        "AMOUNT UNALIGNED: Transaction " +
        cashEntry.getTransactionId() +
        " recorded as " +
        accrualAmount +
        " in accrual, " +
        cashAmount +
        " in cash.";

      this.result = new Match(MatchType.AMOUNT_UNALIGNED, infoString);
      return false;
    }

    return true;
  }
}
