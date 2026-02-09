package com.justlife.cleaning.utils;

import com.justlife.cleaning.domain.dto.Interval;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class IntervalUtils {

  private IntervalUtils() {}

  public static List<Interval> merge(List<Interval> intervals) {
    if (intervals == null || intervals.isEmpty()) {
      return List.of();
    }
    List<Interval> sortedIntervals = new ArrayList<>(intervals);
    sortedIntervals.sort(Comparator.comparing(Interval::start));
    List<Interval> mergedIntervals = new ArrayList<>();
    Interval currentInterval = sortedIntervals.getFirst();
    for (int i = 1; i < sortedIntervals.size(); i++) {
      Interval nextInterval = sortedIntervals.get(i);
      if (!nextInterval.start().isAfter(currentInterval.end())) {
        LocalDateTime mergedEnd = currentInterval.end().isAfter(nextInterval.end())
            ? currentInterval.end()
            : nextInterval.end();
        currentInterval = new Interval(currentInterval.start(), mergedEnd);
        continue;
      }
      mergedIntervals.add(currentInterval);
      currentInterval = nextInterval;
    }
    mergedIntervals.add(currentInterval);
    return mergedIntervals;
  }


  public static List<Interval> subtract(Interval window, List<Interval> mergedBusyIntervals) {
    List<Interval> freeIntervals = new ArrayList<>();
    LocalDateTime freeStartCursor = window.start();

    for (Interval busyInterval : mergedBusyIntervals) {
      if (!busyInterval.end().isAfter(freeStartCursor)) {
        continue;
      }
      if (busyInterval.start().isAfter(freeStartCursor)) {
        LocalDateTime freeEnd = busyInterval.start().isBefore(window.end())
            ? busyInterval.start()
            : window.end();

        if (freeStartCursor.isBefore(freeEnd)) {
          freeIntervals.add(new Interval(freeStartCursor, freeEnd));
        }
      }
      freeStartCursor = busyInterval.end();
      if (!freeStartCursor.isBefore(window.end())) {
        break;
      }
    }
    if (freeStartCursor.isBefore(window.end())) {
      freeIntervals.add(new Interval(freeStartCursor, window.end()));
    }
    return freeIntervals;
  }
}
