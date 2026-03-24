#!/usr/bin/env -S uv run
# /// script
# dependencies = ["faker"]
# ///

import base64
import csv
import hashlib
import random
from datetime import datetime, timedelta
from decimal import Decimal
from typing import List

from faker import Faker

SEED = 42
fake = Faker()
Faker.seed(SEED)
random.seed(SEED)


class Row:
    def __init__(
        self,
        id: str,
        dest: str,
        amt: Decimal,
        t: datetime,
    ):
        self.transaction_id: str = id
        self.destination: str = dest
        self.amount: Decimal = amt
        self.time: datetime = t

    def __repr__(self) -> str:
        return self.__str__()

    def __str__(self) -> str:
        return f"{self.transaction_id},{self.destination},{self.amount},{self.time.isoformat(timespec='seconds')}"


def generate_row() -> Row:
    # Use faker to generate destination company, time, and amount
    dest: str = fake.company()
    time: datetime = fake.date_time_this_year(after_now=True)
    amt: Decimal = fake.pydecimal(
        left_digits=5, right_digits=2, positive=True, min_value=10
    )

    # Use other columns to hash into TransactionID (SHA-256) and encode to base64
    hash_source: str = f"{dest}|{time.isoformat(timespec='seconds')}|{amt}"
    raw_hash = hashlib.sha256(hash_source.encode()).digest()
    id = base64.b64encode(raw_hash).decode("UTF-8")

    return Row(id, dest, amt, time)


def generate_rows(N: int = 100) -> list[Row]:
    return [generate_row() for _ in range(N)]


def create_cash_ledger(accrual_rows: List[Row]) -> List[Row]:
    cash_rows = []
    for row in accrual_rows:
        # 1. Simulate "CASH_ENTRY_MISSING" (Drop the row entirely)
        if random.random() < 0.05:  # 5% chance
            continue

        new_amt = row.amount
        new_time = row.time

        # 2. Simulate "AMOUNT_UNALIGNED" (Shift by a cent or more)
        if random.random() < 0.03:  # 3% chance
            error_amount = round(random.random(), ndigits=2)
            new_amt += Decimal(error_amount)

        # 3. Simulate "DATETIME_UNALIGNED" (Shift by more than 3 days)
        elif random.random() < 0.03:
            new_time += timedelta(days=4)

        # Create the derivative row
        cash_rows.append(Row(row.transaction_id, row.destination, new_amt, new_time))

    # 4. Simulate "ACCRUAL_ENTRY_MISSING" (Add ghost entries to Cash)
    ghost_rows = generate_rows(5)
    cash_rows.extend(ghost_rows)

    random.shuffle(cash_rows)
    return cash_rows


def write_csv(output_filename: str, rows: List[Row]):
    with open(output_filename, "w", newline="") as f:
        writer = csv.writer(f, quoting=csv.QUOTE_MINIMAL)
        writer.writerow(["transaction_id", "destination", "amount", "timestamp"])
        for row in rows:
            writer.writerow(
                [
                    row.transaction_id,
                    row.destination,
                    row.amount,
                    row.time.isoformat(timespec="seconds"),
                ]
            )


accrual = generate_rows(100)
cash = create_cash_ledger(accrual)

write_csv("fake_accrual.csv", accrual)
write_csv("fake_cash.csv", cash)
