package com.weather.apimock;

import com.weather.model.City;
import com.weather.model.DailyTemp;

import java.util.List;
import java.util.Set;

public interface WeatherApi {
    public Set<City> getAllCitiesByIds(Set<String> cityIds);
    public List<DailyTemp> getLastYearTemprature(String cityId);
}
