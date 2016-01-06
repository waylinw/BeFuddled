import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;

import javax.json.*;
import java.util.ArrayList;
import java.util.Random;

public class beFuddledGen {
    public static void main(String[] args) {
        String outputFile;
        int numJSONObjectToGenerate;

        //error checking
        if (args.length != 2) {
            System.out.println("Please check README for usage");
            return;
        }

        outputFile = args[0];
        numJSONObjectToGenerate = Integer.parseInt(args[1]);

        if (outputFile.isEmpty() || numJSONObjectToGenerate == 0) {
            System.out.println("Please check README for usage");
            return;
        }

        //generate amount of games to play
        Random gen = new Random();
        int totalGames = numJSONObjectToGenerate / (gen.nextInt(10) + 40);

        //add Game objects to the list of games we need to generate
        ArrayList<Game> gameList = new ArrayList<>();
        for (int i = 1; i <= totalGames; i++) {
            int maxStep = (int) (gen.nextGaussian() * 91 + 9);
            int id = gen.nextInt(9999) + 1;

            Game game = new Game(i, maxStep, "u"+id);
            gameList.add(game);
        }

        //start generating with different threads

        //json object write to file



        JsonObject object = Json.createObjectBuilder().build();
    }
}

class Game {
    static int maxGrid = 20;
    static int minGrid = -20;
    private int gameNumber, maxSteps;
    private String UserID;

    public Game(int gameNumber, int maxSteps, String userID) {
        this.gameNumber = gameNumber;
        this.maxSteps = maxSteps;
        UserID = userID;
    }


    //need to implement the game result generation algorithm

    public int getGameNumber() {
        return gameNumber;
    }

    public void setGameNumber(int gameNumber) {
        this.gameNumber = gameNumber;
    }

    public int getMaxSteps() {
        return maxSteps;
    }

    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
}