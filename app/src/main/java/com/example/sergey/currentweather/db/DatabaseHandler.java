package com.example.sergey.currentweather.db;


import com.example.sergey.currentweather.model.Weather;

import java.util.List;

public interface DatabaseHandler<T> {

    void addCity(Weather weather);

    void addListCity(String [] name);

    List<Weather> getAllCity();

    int updateCity(Weather weather);

    void deleteCity(Weather weather);

    Weather getCityWeather(long id);
}
