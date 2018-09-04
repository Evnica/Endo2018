package com.evnica.main.interaction;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

/**
 * Class: UrlConnector
 * Version: 0.1
 * Created on 17.08.2018 with the help of IntelliJ IDEA (thanks!)
 * Author: Evnica
 * Description:
 */
public class UrlConnector
{

    private static final String USER_REST_URL = "https://www.endomondo.com/rest/v1/users/";
    private static final String START_DATE = "2016-12-31T23%3A59%3A59.000Z";
    private static final String END_DATE = "2018-01-01T00%3A00%3A00.000";
    private static String userWorkoutsInIntervalUrl =
            "https://www.endomondo.com/rest/v1/users/%s/workouts?before=%s&after=%s";
    private static String workoutUrl = "https://www.endomondo.com/rest/v1/users/%s/workouts/%s";


    public static String getUserJsonData(int id) throws IOException{
        String url = USER_REST_URL + id;
        return getUrlContent( url );
    }

    public static String getAllWorkoutsJson(int userId) throws IOException{
        String url = String.format(userWorkoutsInIntervalUrl, userId, END_DATE, START_DATE);
        return getUrlContent( url ); //ISO8859-1
    }

    public static String getWorkoutData(int userId, int workoutId) throws IOException{
        String url = String.format( workoutUrl, userId, workoutId );
        return getUrlContent( url );
    }

    private static String getUrlContent(String url) throws IOException
    {
        InputStream inputStream = new URL(url).openStream();
        return new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
    }




}
