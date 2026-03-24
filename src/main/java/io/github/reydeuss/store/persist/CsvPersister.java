package io.github.reydeuss.store.persist;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import io.github.reydeuss.result.Match;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class CsvPersister extends AbstractPersister {

  private final Path targetPath;
  private final CsvMapper mapper;
  private final CsvSchema schema;

  public CsvPersister(Path targetPath) {
    this.targetPath = targetPath;
    this.mapper = new CsvMapper();
    // Derive schema from the annotated internal wrapper
    this.schema = mapper.schemaFor(CsvMatchWrapper.class).withHeader();
  }

  @Override
  public void saveResults(List<Match> results) throws IOException {
    Path parent = targetPath.getParent() != null ? targetPath.getParent() : Path.of(".");
    Path tempFile = Files.createTempFile(parent, "recon_", ".tmp");

    try (SequenceWriter writer = mapper.writer(schema).writeValues(tempFile.toFile())) {
      for (Match match : results) {
        writer.write(new CsvMatchWrapper(match));
      }

      // Ensure all data is flushed before moving
      writer.flush();

      Files.move(tempFile, targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    } catch (IOException e) {
      Files.deleteIfExists(tempFile);
      throw new IOException("Failed to persist results to " + targetPath, e);
    }
  }

  /**
   * Internal wrapper to keep Jackson annotations out of the result package.
   */
  @JsonPropertyOrder({ "status", "details" })
  private static class CsvMatchWrapper {

    private final Match match;

    public CsvMatchWrapper(Match match) {
      this.match = match;
    }

    @JsonProperty("status")
    public String getStatus() {
      return match.type().getDescription();
    }

    @JsonProperty("details")
    public String getDetails() {
      return match.information();
    }
  }
}
