package com.evnica.main.model;

/**
 * Class: HeartRateZone
 * Version: 0.1
 * Created on 17.08.2018 with the help of IntelliJ IDEA (thanks!)
 * Author: Evnica
 * Description:
 */
public class HeartRateZone
{
    public int userId;
    public int  max,
                rest,
                zone1Start,
                zone2Start,
                zone3Start,
                zone4Start,
                zone5Start;

    public HeartRateZone( int userId )
    {
        this.userId = userId;
    }

    @Override
    public String toString()
    {
        return "HeartRateZone{" +
                "userId=" + userId +
                ", max=" + max +
                ", rest=" + rest +
                ", zone1Start=" + zone1Start +
                ", zone2Start=" + zone2Start +
                ", zone3Start=" + zone3Start +
                ", zone4Start=" + zone4Start +
                ", zone5Start=" + zone5Start +
                '}';
    }
}
