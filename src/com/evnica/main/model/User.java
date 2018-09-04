package com.evnica.main.model;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Class: User
 * Version: 0.1
 * Created on 17.08.2018 with the help of IntelliJ IDEA (thanks!)
 * Author: Evnica
 * Description:
 */
public class User
{
    public int id;
    public int gender;
    public int weight;
    public int height;
    public DateTime born;
    public int workoutCount;
    public DateTime created;
    public String country;
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Override
    public String toString()
    {
        String gen = gender == 0 ? "male" : gender == 1 ? "female" : "n/a";
        return "User{" +
                "id: " + id +
                ", gender: '" + gen +
                "', weight: " + weight +
                ", height: " + height +
                ", born: " + born +
                ", workoutCount: " + workoutCount +
                ", created: " + created +
                ", country: '" + country + '\'' +
                '}';
    }

    public String toCsvString(){
        return id + ";" + gender + ";" + weight + ";" + height + ";" + born.toString( formatter ) + ";" + workoutCount
                + ";" + created.toString( formatter ) + ";" + country;
    }
}
