import com.oracle.javafx.jmx.json.JSONReader;

import javax.json.*;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WaylinWang on 1/11/16.
 */

/**
 * Gamestats:
 * total number of games
 * total number of completed games
 * number of win
 * number of loss
 *
 * completed games: average and std dev
 * total number of points in the game
 * number of points in won games
 * number of points in loss games
 * number of moves in a game
 * number of moves in games won
 * number of moves in games lost
 * moves histogram:, 0-15) [15-30) [30-40) [40-50) [50-60) [60-80) [80-inf)
 *
 * users who played 1 complete game:
 * total who started at least 1 game
 * total who completed at least 1 game
 * largest number of games a user started
 * largest number of games a user completed
 * largest number of wins a user have
 * largest number of loss a single user had
 * largest number of moves in a single game, report userid only
 * report the userid as well, unless too many
 *
 * identify the 10 most popular locations, all moves from all games, in histogram
 *
 * histogram of frequency of special moves
 *
 *
 */
public class beFuddledStats {
    public static void main(String[] args) {
        boolean saveToFile = false;
        String inputFile, saveFile = "";

        if (args.length == 0) {
            System.out.println("Usage [input file name] [optional output file name]");
            return;
        }

        inputFile = args[0];
        if (args.length > 1){
            saveFile = args[1];
            saveToFile = true;
        }

        BufferedReader bufReader;

        try {
            bufReader = new BufferedReader(new FileReader(inputFile));
        } catch (Exception e) {
            System.out.println("Error reading from " + inputFile + ". Please try again");
            return;
        }

        Map<String,Boolean> config = new HashMap<>();
        JsonParserFactory parserFactory = Json.createParserFactory(config);
        JsonParser parser = parserFactory.createParser(bufReader);
        JsonParser.Event jsonEvent;

        while (parser.hasNext()) {
            jsonEvent = parser.next();

            if (jsonEvent == JsonParser.Event.START_OBJECT) {
                jsonEvent = parser.next();
                while (jsonEvent != JsonParser.Event.END_OBJECT) {
                    if (jsonEvent == JsonParser.Event.KEY_NAME) {
                        switch(parser.getString()) {
                            case "game":
                                jsonEvent = parser.next(); //reads in game number
                                break;
                            case "action":
                                while (jsonEvent != JsonParser.Event.END_OBJECT) { //reads in the action object
                                    jsonEvent = parser.next();
                                }
                                break;
                            case "user":
                                jsonEvent = parser.next(); // reads in username
                                break;
                            default:
                                System.out.println("Input file is not for beFuddled, please check the file.");
                                return;
                        }
                    }
                    jsonEvent = parser.next();
                }
            }
        }


        PrintWriter writer;

        if (saveToFile) {
            try {
                writer = new PrintWriter(new FileWriter(saveFile));
            }
            catch (Exception e) {
                System.out.println("Create file failed. Please try again!");
                return;
            }

            //prints the json array opening
            try {
                writer.println("[");
            } catch (Exception e) {
                System.out.println("Write to file failed. Please try running program again!");
                return;
            }
        }


    }
}


