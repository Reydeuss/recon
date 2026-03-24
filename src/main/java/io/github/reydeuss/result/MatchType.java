package io.github.reydeuss.result;

public enum MatchType {
  MATCHED("Exact Match"),
  CASH_ENTRY_MISSING("Cash Entry missing"),
  ACCRUAL_ENTRY_MISSING("Accrual Entry missing"),
  DATETIME_UNALIGNED("Date/Time unaligned"),
  AMOUNT_UNALIGNED("Amount unaligned");

  private final String description;

  MatchType(String description) {
    this.description = description;
  }

  public String getDescription() {
    return this.description;
  }
}
