package com.evnica.main.interaction;

import com.evnica.main.model.HeartRateZone;
import com.evnica.main.model.Lap;
import com.evnica.main.model.User;
import com.evnica.main.model.Workout;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class: JsonParser
 * Version: 0.1
 * Created on 17.08.2018 with the help of IntelliJ IDEA (thanks!)
 * Author: Evnica
 * Description:
 */
public class JsonParser
{
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final static Logger LOGGER =
            org.apache.logging.log4j.LogManager.getLogger(JsonParser.class.getName());


    /*
    * Operates on a user JSON data like here:
    * https://www.endomondo.com/rest/v1/users/1311
    * */
    public static User parseUser(String jsonText){

        /*
        *   public int id; "id": 1311,
            public int gender; "gender": 0,
            public int weight; "weight": 83,
            public int height; "height": 192,
            public DateTime born; "date_of_birth": "1963-06-02T07:21:02.000Z",
            public int workoutCount; "workoutCount": 3884,
            public DateTime created; "created_date": "2008-11-13T17:28:18.000Z",
            public String country; "country": "DK",
        * */

        User user = new User();
        JSONObject userObject = new JSONObject( jsonText );

        int id,
                gender,
                weight,
                height,
                workoutCount;
        String country;
        DateTime dateOfBirthDT, dateCreatedDT;

        id = userObject.getInt( "id" );

        try{
            gender = userObject.getInt( "gender" );
        } catch ( JSONException e ){
            gender = 2;
        }

        try{
            weight = userObject.getInt( "weight" );
        }catch ( JSONException e ){
            weight = -1;
        }

        try{
            height = userObject.getInt( "height" );
        }catch ( JSONException e ){
            height = -1;
        }

        try{
            dateOfBirthDT = getDateTimeFromJSON( userObject, "date_of_birth" );
        }catch ( JSONException e ){
            dateOfBirthDT = null;
        }

        try{
            workoutCount = userObject.getInt( "workoutCount" );
        }catch ( JSONException e ){
            workoutCount = -1;
        }

        try{
            dateCreatedDT = getDateTimeFromJSON( userObject, "created_date" );
        } catch ( JSONException e ){
            dateCreatedDT = null;
        }

        try{
            country = userObject.getString( "country" );
        } catch ( JSONException e ){
            country = null;
        }

        user.id = id;
        user.gender = gender;
        user.weight = weight;
        user.height = height;
        user.born = dateOfBirthDT;
        user.workoutCount = workoutCount;
        user.created = dateCreatedDT;
        user.country = country;

        return user;
    }


    /*
    * Operates on a user JSON data like here:
    * https://www.endomondo.com/rest/v1/users/1311
    * */
    public static HeartRateZone parseHeartRateZone(String jsonText){

        HeartRateZone heartRateZone;

        /*
        public int userId;
        public int  max,
                rest,
                zone1Start,
                zone2Start,
                zone3Start,
                zone4Start,
                zone5Start;

        * "hr_zones": {
                        "max": 185,
                        "rest": 60,
                        "zone1_start": 123,
                        "zone2_start": 135,
                        "zone3_start": 148,
                        "zone4_start": 160,
                        "zone5_start": 173
                        },
        * */
        JSONObject heartRateObject = new JSONObject( jsonText );
        int max, rest, zone1Start, zone2Start, zone3Start, zone4Start, zone5Start;

        try
        {
            JSONObject zones = heartRateObject.getJSONObject( "hr_zones" );
            heartRateZone = new HeartRateZone(heartRateObject.getInt( "id" ));

            try{
                max = zones.getInt( "max" );
            } catch ( JSONException e ){
                max = -1;
            }
            try{
                rest = zones.getInt( "rest" );
            } catch ( JSONException e ){
                rest = -1;
            }
            try{
                zone1Start = zones.getInt( "zone1_start" );
            } catch ( JSONException e ){
                zone1Start = -1;
            }
            try{
                zone2Start = zones.getInt( "zone2_start" );
            } catch ( JSONException e ){
                zone2Start = -1;
            }
            try{
                zone3Start = zones.getInt( "zone3_start" );
            } catch ( JSONException e ){
                zone3Start = -1;
            }
            try{
                zone4Start = zones.getInt( "zone4_start" );
            } catch ( JSONException e ){
                zone4Start = -1;
            }
            try{
                zone5Start = zones.getInt( "zone5_start" );
            } catch ( JSONException e ){
                zone5Start = -1;
            }

            heartRateZone.max = max;
            heartRateZone.rest = rest;
            heartRateZone.zone1Start = zone1Start;
            heartRateZone.zone2Start = zone2Start;
            heartRateZone.zone3Start = zone3Start;
            heartRateZone.zone4Start = zone4Start;
            heartRateZone.zone5Start = zone5Start;


        }catch ( JSONException e ){
            heartRateZone = null;
        }

        return heartRateZone;
}


    /*
    * Only workouts wih the distance of no less than 500 m  and a duration of no less than than 1.5 minutes (90 seconds)
     * wil be stored in the DB
     * Operates on one of the workout summaries like in a JSON array here:
     * https://www.endomondo.com/rest/v1/users/1311/workouts?before=2017-01-01T00%3A00%3A00.000Z&after=2016-01-01T00%3A00%3A00.000Z
    * */
    public static Workout parseWorkout( JSONObject workoutJsonSummary, int userId ){

        /*
        public int id;                      "id": 660566852,
        public int sport;                   "sport": 2,
        public DateTime startTimeUtc;       "start_time": "2016-01-21T17:07:30.000Z",
        public DateTime startTimeLocal;     "local_start_time": "2016-01-21T18:07:30.000+01:00",
        public String timeZone;                                                        "+01:00",
        public double distance;             "distance": 2.75792,
        public double duration;             "duration": 1009.957, in seconds
        public double speedAvg;             "speed_avg": 9.830628432695649,
        public double speedMax;             "speed_max": 18.2736,
        * */

        Workout workout;

        int id, sport;
        String localTime, timeZone;
        DateTime startTimeUtcDT, startTimeLocalDT;
        double distance, duration, speedAvg, speedMax;

        try
        {
            id = workoutJsonSummary.getInt( "id" );
            duration = workoutJsonSummary.getInt( "duration" );
            distance = workoutJsonSummary.getDouble( "distance" );
            if (distance >= 0.5){

                if (duration >= 90){

                    sport = workoutJsonSummary.getInt( "sport" );

                    startTimeUtcDT = getDateTimeFromJSON(workoutJsonSummary, "start_time" );
                    localTime = workoutJsonSummary.getString( "local_start_time" );
                    timeZone = localTime.substring( 23 );
                    startTimeLocalDT = getDateTimeFromJSON( workoutJsonSummary, "local_start_time" );

                    try
                    {
                        speedAvg = workoutJsonSummary.getDouble( "speed_avg" );
                        speedMax = workoutJsonSummary.getDouble( "speed_max" );
                    } catch ( JSONException e )
                    {
                        speedAvg = -1;
                        speedMax = -1;
                    }


                    workout = new Workout();
                    workout.id = id;
                    workout.userId = userId;
                    workout.sport = sport;
                    workout.startTimeUtc = startTimeUtcDT;
                    workout.startTimeLocal = startTimeLocalDT;
                    workout.timeZone = timeZone;
                    workout.distance = distance;
                    workout.duration = duration;
                    workout.speedAvg = speedAvg;
                    workout.speedMax = speedMax;
                }
                else {
                    workout = null;
                }
            } else {
                workout = null;
            }
        } catch ( JSONException e )
        {
            workout = null;
        }


        return workout;
    }

    /*
    * Operates on workout metadata like here:
    * https://www.endomondo.com/rest/v1/users/1311/workouts/660566852
    * */
    public static ArrayList<Lap> parseLaps(String jsonText){
        /*
        "laps": {
                "metric": [
                {
                "average_pace": 297.524,
                "distance": 1,
                "duration": 297524,
                "begin_latitude": 55.93198338523507,
                "begin_longitude": 12.287206454202533,
                "end_latitude": 55.92924621038089,
                "end_longitude": 12.300732111560142
                },
                {
                "average_pace": 334.993,
                "distance": 1,
                "duration": 334993,
                "begin_latitude": 55.92924621038089,
                "begin_longitude": 12.300732111560142,
                "end_latitude": 55.93454016193798,
                "end_longitude": 12.31211335646295
                },
                {
                "average_pace": 498.0512279166655,
                "distance": 0.7579200267791748,
                "duration": 377483,
                "begin_latitude": 55.93454016193798,
                "begin_longitude": 12.31211335646295,
                "end_latitude": 55.93448395840824,
                "end_longitude": 12.320886282250285
                }
                ],
        * */
        ArrayList<Lap> laps = new ArrayList<>();
        try
        {
            JSONObject workoutObject = new JSONObject( jsonText );
            JSONArray jsonLaps;
            try
            {
                jsonLaps = workoutObject.getJSONObject( "laps" ).getJSONArray( "metric" );
            } catch ( JSONException e )
            {
                jsonLaps = workoutObject.getJSONObject( "laps" ).getJSONArray( "imperial" );
            }
            DateTime offset = getDateTimeFromJSON( workoutObject, "local_start_time" );
            int workoutId = workoutObject.getInt( "id" );
            if (jsonLaps.length() > 0){
                double beginLatitude, beginLongitude, endLatitude, endLongitude;
                int duration;
                for (int i = 0; i < jsonLaps.length(); i++)
                {
                    beginLatitude = ((JSONObject) jsonLaps.get(i)).getDouble("begin_latitude");
                    beginLongitude = ((JSONObject) jsonLaps.get(i)).getDouble("begin_longitude");
                    endLatitude = ((JSONObject) jsonLaps.get(i)).getDouble("end_latitude");
                    endLongitude = ((JSONObject) jsonLaps.get(i)).getDouble("end_longitude");
                    duration = ((JSONObject) jsonLaps.get(i)).getInt("duration");

                    Lap lap = new Lap();
                    lap.orderedNum = i;
                    lap.workoutId = workoutId;
                    lap.beginLatitude = beginLatitude;
                    lap.beginLongitude = beginLongitude;
                    lap.endLatitude = endLatitude;
                    lap.endLongitude = endLongitude;
                    lap.duration = duration;
                    lap.offset = offset;
                    offset = offset.plusMillis( duration );
                    laps.add( lap );
                }
            }
        } catch ( JSONException e )
        {
            e.printStackTrace();
        }

        return laps;
    }

    private static DateTime getDateTimeFromJSON(JSONObject jsonObject, String stringKeyInJSON){
        String ts = jsonObject.getString( stringKeyInJSON );
        ts = ts.substring( 0, 19 );
        return formatter.parseDateTime( ts );
    }
}
