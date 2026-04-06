package uk.ratracejoe.sdq.utils;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public interface PeriodSupplier {
  static Supplier<LocalDate> periodicDays(int period) {
    AtomicInteger days = new AtomicInteger(0);
    return () -> LocalDate.now().plusDays(days.getAndAdd(period));
  }
}
