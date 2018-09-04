package com.evnica.main.model;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Class: Lap
 * Version: 0.1
 * Created on 17.08.2018 with the help of IntelliJ IDEA (thanks!)
 * Author: Evnica
 * Description:
 */
public class Lap
{
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
    public int orderedNum;
    public int workoutId;
    public DateTime offset;
    public long duration; // in millis
    public double beginLatitude,
                beginLongitude,
                endLatitude,
                endLongitude;

    @Override
    public String toString()
    {
        return "Lap{" +
                "orderedNum=" + orderedNum +
                ", workoutId=" + workoutId +
                ", offset=" + offset.toString( formatter ) +
                ", duration=" + duration +
                ", beginLatitude=" + beginLatitude +
                ", beginLongitude=" + beginLongitude +
                ", endLatitude=" + endLatitude +
                ", endLongitude=" + endLongitude +
                '}';
    }
}
