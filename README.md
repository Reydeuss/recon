# Recon

A high-performance, stream-based financial reconciliation engine designed with a "no-magic" philosophy. This utility reconciles accrual and cash ledgers with an **O(1)** memory footprint for the cash stream, ensuring scalability for massive datasets.

## Features

* **Stream-Based Processing**: Processes cash ledger entries as a stream to maintain constant memory usage regardless of file size.
* **Minimalist Architecture**: No heavy enterprise frameworks. Built with Maven, Picocli, and Jackson CSV for transparency.
* **Atomic Persistence**: Results are written to a temporary file and moved atomically to the destination to prevent data corruption.
* **Extensible Checks**: Pluggable checker system for validating amounts, timestamps, and missing entries.

## Project Structure

The project follows a clean boundary between domain logic and infrastructure:

* `engine`: Core reconciliation logic.
* `check`: Modular workers for specific validation rules.
* `store`: Infrastructure layer for data loading (`load`) and persistence (`persist`).
* `model`: Pure data carriers (Java Records and Abstracts).
* `result`: Domain-specific outcomes and status types.

## Prerequisites

* **Java 17+**
* **Maven** (or use the included `./mvnw`)
* **Python 3.x** / **uv** (for the test data generator)

## Getting Started

### 1. Generate Test Data
It is recommended to use `uv` for the generator as it handles dependencies automatically:
```bash
uv run generator/generator.py
```

### 2. Build the Project
Compile and package the utility into an executable JAR:
```bash
./mvnw clean package
```

### 3. Run Reconciliation
Execute the JAR using the CLI arguments provided by Picocli:
```bash
java -jar target/recon-rolling.jar --accrual=accrual.csv --cash=cash.csv --output=results.csv
```

## CLI Usage

```text
Usage: recon [-hV] --accrual=<file> --cash=<file> --output=<file>
Stream-based financial reconciliation engine.

      --accrual=<file>   Accrual Ledger CSV path
      --cash=<file>      Cash Ledger CSV path
  -h, --help             Show this help message and exit.
      --output=<file>    Results output path
  -V, --version          Print version information and exit.
```

## License

This project is licensed under the **BSD 3-Clause License**. See `src/main/java/io/github/reydeuss/LICENSE.md` for the full license text.

---

Developed by **Grey (reydeuss)**.
