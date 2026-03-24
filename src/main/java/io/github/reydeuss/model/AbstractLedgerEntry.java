package io.github.reydeuss.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

abstract class AbstractLedgerEntry {

  final String transactionId;
  final String destination; // Company codes
  final BigDecimal amount;
  final LocalDateTime dateTime; // Expected in ISO 8601

  @JsonCreator
  AbstractLedgerEntry(
    @JsonProperty("transaction_id") String transId,
    @JsonProperty("destination") String dst,
    @JsonProperty("amount") BigDecimal amt,
    @JsonProperty("timestamp") LocalDateTime dtime
  ) {
    this.transactionId = transId;
    this.destination = dst;
    this.amount = amt;
    this.dateTime = dtime;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public String getDestination() {
    return destination;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }
}
