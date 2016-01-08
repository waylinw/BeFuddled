import javax.json.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;


public class beFuddledGen {
    public static void main(String[] args) throws IOException {
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

        //Setup file output writer
        PrintWriter writer;

        try {
            writer = new PrintWriter(new FileWriter(outputFile));
        }
        catch (Exception e) {
            System.out.println("Create file failed. Please try again!");
            return;
        }

        //add Game objects to the list of games we need to generate
        ArrayList<Game> gameList = generateGameList(numJSONObjectToGenerate);


        try {
            writer.println("[");
        } catch (Exception e) {
            System.out.println("Write to file failed. Please try running program again!");
            return;
        }

        //generate the json object and write to file
        while (numJSONObjectToGenerate-- > 0 && !gameList.isEmpty()) {
            int x = (int) (Math.random() * gameList.size());
            Game currentGame = gameList.get(x);
            try {
                writer.println(currentGame.step());
            } catch (Exception e) {
                System.out.println("Write to file failed. Please try running program again!");
                return;
            }
            //remove the game from list if it finished
            if(currentGame.getGameCpmplete()) {
                gameList.remove(x);
            }
        }
        //if we didn't generate enough json objects, need to restart the process again.
        if (numJSONObjectToGenerate > 0) {
            gameList = generateGameList(Integer.parseInt(args[1]));
        }

        try {
            writer.println("]");
        } catch (Exception e) {
            System.out.println("Write to file failed. Please try running program again!");
            return;
        }

        writer.close();
    }

    private static ArrayList<Game> generateGameList(int numJSONObjectToGenerate) {
        ArrayList<Game> retVal = new ArrayList<>();
        Random gen = new Random();

        int totalGames = numJSONObjectToGenerate / (Math.abs((int) (gen.nextGaussian() * 5 + 36)) + 9);

        for (int i = 1; i <= totalGames; i++) {
            int maxStep = Math.abs((int) (gen.nextGaussian() * 5 + 36)) + 9;
            int id = gen.nextInt(9999) + 1;
            Game game = new Game(i, maxStep, "u"+id);
            retVal.add(game);
        }
        return retVal;
    }
}

class Game {
    static int maxGrid = 20;
    static int minGrid = -20;
    private int gameNumber, maxSteps;
    private String UserID;
    private boolean gameComplete;
    private boolean gameBegan;
    private int curStep;
    private int points;

    public Game(int gameNumber, int maxSteps, String userID) {
        this.gameNumber = gameNumber;
        this.maxSteps = maxSteps;
        UserID = userID;
        gameComplete = false;
        gameBegan = false;
        curStep = 1;
        points = 0;
    }

    //need to implement the game result generation algorithm
    public JsonObject step() {
        String retVal = "";
        JsonObject obj = null;
        if (!gameBegan) {
            obj = Json.createObjectBuilder()
                    .add("game", curStep)
                    .add("action", Json.createObjectBuilder()
                            .add("actionType", "gameStart")
                            .add("actionNumber", curStep))
                    .add("user", getUserID())
                    .build();
            gameBegan = true;
        }
        else {

        }

        curStep++;

        return obj;
    }

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

    public boolean getGameCpmplete() { return gameComplete; }
}