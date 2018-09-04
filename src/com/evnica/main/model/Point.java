package com.evnica.main.model;

import org.joda.time.DateTime;

/**
 * Class: Point
 * Version: 0.1
 * Created on 17.08.2018 with the help of IntelliJ IDEA (thanks!)
 * Author: Evnica
 * Description:
 */
public class Point
{
    public int id;
    public int workoutId;
    public DateTime pointTime;
    public double lat,
                  lon;
    public double distance;
    public int duration;
    public double altitude;
    public double speed;
}
