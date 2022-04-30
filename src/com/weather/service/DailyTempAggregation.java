package com.weather.service;

import com.weather.model.DailyTemp;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum DailyTempAggregation {
    AVERAGE("avg", dailyTemps -> dailyTemps.stream().mapToDouble(DailyTemp::getTemperature).average().orElseThrow()),
    MAX("max", dailyTemps -> dailyTemps.stream().mapToDouble(DailyTemp::getTemperature).max().orElseThrow()),
    MEDIAN("med", dailyTemps -> {
        List<Double> orderedTemp = dailyTemps.stream().map(DailyTemp::getTemperature)
                .sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        return orderedTemp.get(orderedTemp.size() / 2);
    });

    private String value;
    private Function<List<DailyTemp>, Double> aggregateFunc;

    DailyTempAggregation(String value, Function<List<DailyTemp>, Double> aggregateFunc) {
        this.value = value;
        this.aggregateFunc = aggregateFunc;
    }

    public Function<List<DailyTemp>, Double> getAggregateFunc() {
        return aggregateFunc;
    }

    public static DailyTempAggregation fromValue(String val) {
        for(DailyTempAggregation agg : DailyTempAggregation.values()) {
            if (agg.value.equals(val)) {
                return agg;
            }
        }
        throw new IllegalArgumentException(val);
    }
}
