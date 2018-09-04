package com.evnica.main.model;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Class: Workout
 * Version: 0.1
 * Created on 17.08.2018 with the help of IntelliJ IDEA (thanks!)
 * Author: Evnica
 * Description:
 */
public class Workout
{
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

    public int id;
    public int userId;
    public int sport;
    public DateTime startTimeUtc;
    public DateTime startTimeLocal;
    public String timeZone;
    public double distance; // in km
    public double duration; // in seconds
    public double speedAvg; // in kmh
    public double speedMax;

    @Override
    public String toString()
    {
        return "Workout{" +
                "id=" + id +
                ", userId=" + userId +
                ", sport=" + sport +
                ", startTimeUtc=" + startTimeUtc.toString( formatter ) +
                ", startTimeLocal=" + startTimeLocal +
                ", timeZone='" + timeZone + '\'' +
                ", distance=" + distance +
                ", duration=" + duration +
                ", speedAvg=" + speedAvg +
                ", speedMax=" + speedMax +
                '}';
    }
}
