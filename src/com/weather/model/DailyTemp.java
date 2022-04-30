package com.weather.model;

import java.util.Date;

public class DailyTemp {
    private Date date;
    private double temperature;

    public DailyTemp(Date date, double temperature) {
        this.date = date;
        this.temperature = temperature;
    }

    public double getTemperature() {
        return temperature;
    }
}
