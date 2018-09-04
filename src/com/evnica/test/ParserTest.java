package com.evnica.test;

import com.evnica.main.interaction.JsonParser;
import com.evnica.main.interaction.UrlConnector;
import com.evnica.main.model.Lap;
import com.evnica.main.model.User;
import com.evnica.main.model.Workout;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class: ParserTest
 * Version: 0.1
 * Created on 21.08.2018 with the help of IntelliJ IDEA (thanks!)
 * Author: Evnica
 * Description:
 */
public class ParserTest
{

    public static void main( String[] args ) throws Exception
    {

        String workoutUserPairs = UrlConnector.getAllWorkoutsJson( 1311 );

        JSONArray workouts = new JSONArray( workoutUserPairs );
        boolean cyclist = false;
        if (workouts.length() > 0){
            System.out.println(workouts.length() + " workouts");
            JSONObject workoutObject;
            int sport;
            for (int i = 0 ; i < 1; i++){
                workoutObject = workouts.getJSONObject( i );
                sport = workoutObject.getInt( "sport" );
                if (sport == 0 || sport == 1 || sport == 2 || sport == 3){
                    cyclist = true;
                    Workout workout = JsonParser.parseWorkout( workoutObject, 1311 );
                    String workoutDetail = UrlConnector.getWorkoutData( 1311, workout.id );
                    ArrayList<Lap> laps = JsonParser.parseLaps( workoutDetail );
                    System.out.println(workout);
                    for (Lap lap: laps){
                        System.out.println(lap);
                    }
                }
            }
        }
        if (cyclist){
            String userContent = UrlConnector.getUserJsonData( 1311 );
            User user = JsonParser.parseUser( userContent );
            System.out.println(user);
        }

    }

}
