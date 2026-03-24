package io.github.reydeuss.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CashLedgerEntry extends AbstractLedgerEntry {

  @JsonCreator
  public CashLedgerEntry(
    @JsonProperty("transaction_id") String transId,
    @JsonProperty("destination") String dst,
    @JsonProperty("amount") BigDecimal amt,
    @JsonProperty("timestamp") LocalDateTime dtime
  ) {
    super(transId, dst, amt, dtime);
  }
}
