# weathercalculator

    World Temperature
 Given 2 APIs:  
 
public interface WeatherAPI {   
    public Set<City> getAllCitiesByIds(Set<String> cityIds);    
    public List<DailyTemp> getLastYearTemprature(String cityId); 2sec   
}   
    
public class City {     
    private String id; private String name; private int population;     
}   
    
public class DailyTemp {    
    private Date date;  
    private double temperature;     
}   
The program that takes 2 parameters:
1. Set of city id - (TLV, NY...)
2. Aggregation type (avg, max, median,... ) 
    
The output: 
Collection of top 3 cities with the aggregated temperature. 
The program should calculate the Top 3 cities by requested temperature aggregation whose population is over 50K people. 
Notes:  
1. No need to implement the HTTP calls to the REST APIs, you can use the    
supplied interface above.   
2. You can implement only one aggregator type, but note that it could be any    
additional aggregation type (avg, max, median,... ) 
3. Take into consideration efficiency (the number of countries could be very big)   
