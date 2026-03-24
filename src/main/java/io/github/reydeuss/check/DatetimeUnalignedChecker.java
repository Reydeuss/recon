package io.github.reydeuss.check;

import io.github.reydeuss.model.AccrualLedgerEntry;
import io.github.reydeuss.model.CashLedgerEntry;
import io.github.reydeuss.result.Match;
import io.github.reydeuss.result.MatchType;
import java.time.Duration;
import java.util.Map;

public class DatetimeUnalignedChecker extends AbstractCheckWorker {

  private static final Integer maxDaysTolerance = 3; // Maximum tolerance of 3 days

  @Override
  public boolean check(
    Map<String, AccrualLedgerEntry> accrualLedger,
    CashLedgerEntry cashEntry
  ) {
    var accrualEntry = accrualLedger.get(cashEntry.getTransactionId());
    var accrualTime = accrualEntry.getDateTime();
    var cashTime = cashEntry.getDateTime();

    long daysDiff = Math.abs(Duration.between(accrualTime, cashTime).toDays());

    if (daysDiff > maxDaysTolerance) {
      this.result = new Match(
        MatchType.DATETIME_UNALIGNED,
        String.format(
          "DATETIME UNALIGNED: ID %s. Accrual: %s, Cash: %s (Diff: %d days)",
          cashEntry.getTransactionId(),
          accrualTime,
          cashTime,
          daysDiff
        )
      );
      return false;
    }

    return true;
  }
}
