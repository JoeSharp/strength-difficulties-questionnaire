package uk.ratracejoe.sdq.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public interface PeriodSupplier {
  static Supplier<Instant> periodicDays(int period) {
    AtomicInteger days = new AtomicInteger(0);
    return () -> Instant.now().plus(days.getAndAdd(period), ChronoUnit.DAYS);
  }
}
