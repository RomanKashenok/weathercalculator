package com.weather.service;

import com.weather.apimock.WeatherApi;
import com.weather.model.City;
import com.weather.model.DailyTemp;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WeatherServiceImpl implements WeatherService {

    Logger log = Logger.getLogger(WeatherServiceImpl.class.getName());

    private final WeatherApi weatherApi;
    private final int populationThreshold;

    private ExecutorService executorService;

    public WeatherServiceImpl(WeatherApi weatherApi, int executorsNumber, int populationThreshold) {
        this.weatherApi = weatherApi;
        this.populationThreshold = populationThreshold;
        executorService = Executors.newFixedThreadPool(executorsNumber);
    }

    @Override
    public Set<City> getTopAggregated(String agg, Set<String> cityIds, int topCount) {
        if (cityIds.isEmpty()) return Collections.emptySet();

        try {
            Set<City> filteredCities = weatherApi.getAllCitiesByIds(cityIds)
                    .stream()
                    .filter(c -> c.getPopulation() >= populationThreshold)
                    .collect(Collectors.toSet());
            if (filteredCities.isEmpty()) return Collections.emptySet();
            if (filteredCities.size() <= topCount) return filteredCities;

            Map<City, Future<Double>> results = new HashMap<>();
            final DailyTempAggregation aggregateFunc = DailyTempAggregation.fromValue(agg);
            for (City city : filteredCities) {
                Callable<Double> task = new AggregationCalculator(city.getId(), aggregateFunc.getAggregateFunc());
                results.put(city, executorService.submit(task));
            }
            executorService.shutdown();
            return extractResult(results, topCount);
        } catch (Exception e) {
            log.warning(String.format("Execution of getTopAggregated() failed. Error occured: %s", e));
        }
        return Collections.emptySet();
    }

    private Set<City> extractResult(Map<City, Future<Double>> executionResults, int topCount) throws Exception {
        Map<City, Double> results = new HashMap<>();
        for (Map.Entry<City, Future<Double>> entry : executionResults.entrySet()) {
            City city = entry.getKey();
            try {
                Double aggValue = entry.getValue().get(5, TimeUnit.SECONDS);
                results.put(city, aggValue);
            } catch (TimeoutException e) {
                log.warning(String.format("Proceeding get %s aggregation result timeout. Will not be added to results", city));
            }
        }

        return results.entrySet()
                .stream().sorted(Map.Entry.comparingByValue())
                .limit(topCount)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    class AggregationCalculator implements Callable<Double> {
        private final String city;
        private final Function<List<DailyTemp>, Double> aggregationFunc;

        public AggregationCalculator(String city, Function<List<DailyTemp>, Double> aggregationFunc) {
            this.city = city;
            this.aggregationFunc = aggregationFunc;
        }

        @Override
        public Double call() {
            List<DailyTemp> lastYearTemprature = weatherApi.getLastYearTemprature(city);
            return aggregationFunc.apply(lastYearTemprature);
        }
    }

}
