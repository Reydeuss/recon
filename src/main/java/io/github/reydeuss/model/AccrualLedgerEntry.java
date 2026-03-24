package io.github.reydeuss.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents accrual ledger entries. Extends {@link AbstractLedgerEntry}.
 * Contains metadata for match status during reconciliation.
 */
public class AccrualLedgerEntry extends AbstractLedgerEntry {

  private boolean matched = false;

  @JsonCreator
  public AccrualLedgerEntry(
    @JsonProperty("transaction_id") String transId,
    @JsonProperty("destination") String dst,
    @JsonProperty("amount") BigDecimal amt,
    @JsonProperty("timestamp") LocalDateTime dtime
  ) {
    super(transId, dst, amt, dtime);
  }

  public boolean isMatched() {
    return matched;
  }

  public void markMatched() {
    this.matched = true;
  }
}
