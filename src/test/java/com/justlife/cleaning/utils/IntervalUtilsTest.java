package com.justlife.cleaning.utils;

import com.justlife.cleaning.domain.dto.Interval;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntervalUtilsTest {

  @Test
  void merge_overlappingAndTouchingIntervals() {
    LocalDateTime t0 = LocalDateTime.of(2026, 2, 10, 10, 0);

    var intervals = List.of(
        new Interval(t0.plusMinutes(60), t0.plusMinutes(120)),
        new Interval(t0, t0.plusMinutes(60)),
        new Interval(t0.plusMinutes(180), t0.plusMinutes(240)),
        new Interval(t0.plusMinutes(230), t0.plusMinutes(300))
    );

    var merged = IntervalUtils.merge(intervals);

    assertEquals(2, merged.size());
    assertEquals(new Interval(t0, t0.plusMinutes(120)), merged.get(0));
    assertEquals(new Interval(t0.plusMinutes(180), t0.plusMinutes(300)), merged.get(1));
  }

  @Test
  void subtract_producesFreeIntervals() {
    LocalDateTime t0 = LocalDateTime.of(2026, 2, 10, 8, 0);

    Interval window = new Interval(t0, t0.plusHours(8));
    List<Interval> busy = List.of(
        new Interval(t0.plusHours(1), t0.plusHours(2)),
        new Interval(t0.plusHours(3), t0.plusHours(5))
    );

    var free = IntervalUtils.subtract(window, IntervalUtils.merge(busy));

    assertEquals(List.of(
        new Interval(t0, t0.plusHours(1)),
        new Interval(t0.plusHours(2), t0.plusHours(3)),
        new Interval(t0.plusHours(5), t0.plusHours(8))
    ), free);
  }
}

