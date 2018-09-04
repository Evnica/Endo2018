package com.evnica.main;

import com.evnica.main.interaction.JsonParser;
import com.evnica.main.interaction.UrlConnector;
import com.evnica.main.model.User;
import com.evnica.main.model.Workout;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class: Endomain
 * Version: 0.1
 * Created on 28.08.2018 with the help of IntelliJ IDEA (thanks!)
 * Author: Evnica
 * Description:
 */
public class Endomain
{
    private final static Logger LOGGER =
            org.apache.logging.log4j.LogManager.getLogger(Endomain.class.getName());
    private static int rejectedUserIdCount = 0,
                       rejectedWrktIdCount = 0,
                       invalidUserIdCount = 0,
                       invalidWrktIdCount = 0,
                       rejectedSummaryCount = 0;
    private static List<Integer> rejectedUserIds = new ArrayList<>(  );
    private static List<Integer> rejectedWorkoutIds = new ArrayList<>(  );
    private static int numOfIterations, iterationSize, start, end;
    private enum TargetData{
        SUMMARY, USER, WORKOUT;

        @Override
        public String toString()
        {
            String string;
            switch ( this ){
                case SUMMARY:
                    string = "summary";
                    break;
                case WORKOUT:
                    string = "workout";
                    break;
                case USER:
                    string = "user";
                    break;
                default:
                    string = "n/a";
                    break;
            }
            return string;
        }
    }


    public static void main( String[] args )
    {
        readParameters();
        retrieveData( numOfIterations, iterationSize, start);
        jsonLog( start, end-1 );

    }

    private static void retrieveData( int numOfIterations, int iterationSize, int start )
    {
        Random random = new Random();
        IntStream intStream = random.ints(8, 20);
        List<Integer> randomBetween8And20 = intStream
                .limit(numOfIterations)
                .boxed()
                .collect( Collectors.toList());

        rejectedUserIds = new ArrayList<>(  );
        rejectedWorkoutIds = new ArrayList<>(  );

        for ( int id = start; id < end; id++ )
        {
            String workoutSummaryString;
            try
            {
                workoutSummaryString = UrlConnector.getAllWorkoutsJson( id );
            } catch ( IOException e )
            {
                e.printStackTrace();
                workoutSummaryString = processUrlIOException(e, id, TargetData.SUMMARY );
            }

            if (workoutSummaryString != null){
                //parse workout summary to get an array of cycling workouts
                ArrayList<Workout> workouts = parseJsonWorkoutSummary( workoutSummaryString, id );

                if (workouts.size() > 0){

                    //TODO: LOAD DATA for non-empty cycling summary
                    // 1. load the user demographic data

                    retrieveAndLogUser( id );

                    // 2. write user data to a file
                    // 3. for each workout in the list:
                    // 3.1. load detail from url
                    // 3.2. pars laps, store in the list
                    // 3.3. write laps to a file
                    // 3.4. parse points, store in the list
                    // 3.5. write points to a file
                }
            }
            else{
                //TODO: Log failed attempt and move on
            }


        }

    }

    private static void retrieveAndLogUser(int id){
        String userData;
        try
        {
            userData = UrlConnector.getUserJsonData( id );
        } catch ( IOException e )
        {
            userData = processUrlIOException( e, id, TargetData.USER );
            e.printStackTrace();
        }
        if (userData != null){
            try
            {
                User user = JsonParser.parseUser( userData );
            } catch ( Exception e )
            {
                e.printStackTrace();
            }
        }

    }

    private static ArrayList<Workout> parseJsonWorkoutSummary(String workoutSummaryString, int id){

        ArrayList<Workout> workouts = new ArrayList<>( );
        JSONArray workoutSummary = new JSONArray( workoutSummaryString );
        for (int i = 0; i < workoutSummary.length(); i++){
            JSONObject workoutObject = new JSONObject( workoutSummary.getJSONObject( i ) );
            try
            {
                int sport = workoutObject.getInt( "sport" );
                if (sport == 1 || sport == 2 || sport == 3)
                {
                    Workout w = JsonParser.parseWorkout( workoutObject, id );
                    workouts.add( w );
                }
            } catch ( JSONException e )
            {
                e.printStackTrace();
            }
        }
        return workouts;
    }


    private static void readParameters()
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter number of iterations: ");
        try
        {
            numOfIterations = Integer.parseInt( reader.readLine() );
            System.out.print( "Enter iteration size: " );
            iterationSize = Integer.parseInt( reader.readLine() );
            System.out.println( "Enter start id: " );
            start = Integer.parseInt( reader.readLine() );
            end = numOfIterations * iterationSize + start;
            System.out.println( "First id to process: " + start + ", last id to process " + (end - 1)
                    + ", iteration size: " + iterationSize + ". Random delays apply." );

        }
        catch(NumberFormatException nfe)
        {
            System.err.println("Invalid Format!");
            nfe.printStackTrace();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            System.exit( -1 );
        }
    }

    private static String processUrlIOException( IOException e, int id, TargetData targetData )
    {
        String urlContent = null;
        if (e.getMessage().contains( "429" )){
            switch ( targetData ){
                case SUMMARY:
                    rejectedSummaryCount++;
                    break;
                case WORKOUT:
                    rejectedWrktIdCount++;
                    break;
                case USER:
                    rejectedUserIdCount++;
                    break;
                default:
                    break;
            }
            System.out.println(id + " " + targetData + " rejected (429). Retry in 13\"" );
            try { Thread.sleep( 13000 ); }
            catch ( InterruptedException e1 ) { LOGGER.error( "Can't sleep on id " + id ); }
            try{
                urlContent = UrlConnector.getAllWorkoutsJson( id );
            } catch ( IOException ioe ){
                System.out.println( id + " rejected after sleeping for 13\": " + ioe);
                switch ( targetData ){
                    case WORKOUT:
                        rejectedWorkoutIds.add( id );
                        break;
                    default:
                        rejectedUserIds.add( id );
                        break;
                }
            }
        }
        else if (e.getMessage().contains( "500" ) || e instanceof java.io.FileNotFoundException)
        {
            switch ( targetData ){
                case WORKOUT:
                    invalidWrktIdCount++;
                    break;
                default:
                    invalidUserIdCount++;
                    writeInvalidAthleteToFile(id);
                    break;
            }

        }
        else if (e.getMessage().contains("403"))
        {
            LOGGER.error("FATAL: 403! Exiting. Last processed id: " + id);
            System.out.println("FATAL: 403! Exiting. Last processed id: " + id);
            System.exit(-1);
        }
        else if (e instanceof UnknownHostException || e instanceof ConnectException )
        {
            switch ( targetData ){
                case WORKOUT:
                    rejectedWorkoutIds.add( id );
                    break;
                default:
                    rejectedUserIds.add( id );
                    break;
            }
            LOGGER.error( "AAAAAAAAA!!! Connection lost! Trying to sleep for 30s, id cause: " + id );
            try { Thread.sleep( 30000 ); }
            catch ( InterruptedException e1 )
            {
                LOGGER.error( "AAAAAAAAA!!! Connection lost! Tried to sleep but failed =( " + id );
                System.exit( -1 );
            }
        }
        else
        {
            switch ( targetData ){
                case WORKOUT:
                    rejectedWorkoutIds.add( id );
                    break;
                default:
                    rejectedUserIds.add( id );
                    break;
            }
            LOGGER.error( "X3 what is that: ", e );
            try { Thread.sleep( 30000 ); }
            catch ( InterruptedException e1 )
            { LOGGER.error( "Sleep just in case on " + id ); }
        }
        return urlContent;
    }

    private static void writeInvalidAthleteToFile(int id)
    {
        try
        {
            Path file = Paths.get( "./log/invalidUsers.log" );
            List<String> lines = new ArrayList<>(  );
            lines.add( Integer.toString( id ) );
            Files.write( file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    private static void jsonLog(int startUser, int endUser)
    {
            List<String> lines = new ArrayList<>(  );
            String line = new DateTime().toString("yyyy-MM-dd HH-mm-ss") + ";" + startUser + ";" +
                    endUser + ";" + toJsonArray( rejectedUserIds ) + ";" + toJsonArray( rejectedWorkoutIds );
            lines.add( line );
            appendToFile( "C:/logs/jsonRetrieval.log", lines );
    }

    private static void appendToFile(String path, List<String> lines){
        Path file = Paths.get( path );
        try
        {
            Files.write( file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND );
        } catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    private static String toJsonArray( List<Integer> list)
    {
        StringBuilder result = new StringBuilder(  );
        result.append( "[" );
        if (list.size() > 0)
        {
            result.append( Integer.toString( list.get( 0 ) ) );
            if (list.size() > 1)
            {
                for (int i = 1; i < list.size(); i++)
                {
                    result.append( "," );
                    result.append( Integer.toString( rejectedUserIds.get( i ) ) );
                }
            }
        }
        result.append( "]" );
        return result.toString();

    }




}
