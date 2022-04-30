package com.weather.service;

import com.weather.model.City;

import java.util.Set;

public interface WeatherService {

    Set<City> getTopAggregated(String agg, Set<String> cityIds, int topCount);
}
