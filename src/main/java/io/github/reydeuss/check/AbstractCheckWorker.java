package io.github.reydeuss.check;

import io.github.reydeuss.model.AccrualLedgerEntry;
import io.github.reydeuss.model.CashLedgerEntry;
import io.github.reydeuss.result.Match;
import io.github.reydeuss.result.MatchType;
import java.util.Map;

@FunctionalInterface
interface CheckerFunction {
  boolean check(
    Map<String, AccrualLedgerEntry> accrualLedger,
    CashLedgerEntry cashEntry
  );
}

public abstract class AbstractCheckWorker implements CheckerFunction {

  @Override
  public abstract boolean check(
    Map<String, AccrualLedgerEntry> accrualLedger,
    CashLedgerEntry cashEntry
  );

  static Match OkResult = new Match(MatchType.MATCHED, null);
  protected Match result = OkResult;

  public Match getMatchResult() {
    return result;
  }
}
