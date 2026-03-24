package io.github.reydeuss.store.persist;

import io.github.reydeuss.result.Match;
import java.io.IOException;
import java.util.List;

/**
 * Base abstraction for persisting reconciliation outcomes to a permanent data store.
 *
 * Implementations are responsible for transforming {@link Match} results into a
 * structured format, such as CSV, JSON, or database records.
 */
public abstract class AbstractPersister {

  /**
   * Persists the final list of reconciliation results.
   *
   * Implementation Requirements:
   * 1. Must handle filesystem or network connectivity errors by throwing an {@link IOException}.
   * 2. Should prioritize atomic writes (e.g., write-to-temp-then-move) to prevent
   *    file corruption in the event of a system failure during the save process.
   * 3. Should utilize {@code MatchType} descriptions for human-readable output where applicable.
   *
   * @param results a list of {@link Match} objects containing the state of reconciled ledgers
   * @throws IOException if the target medium is unreachable, read-only, or the write fails
   */
  public abstract void saveResults(List<Match> results) throws IOException;
}
