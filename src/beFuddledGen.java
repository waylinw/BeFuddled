import javax.json.*;
import java.io.*;
import java.lang.reflect.Array;
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
        ArrayList<User> users = generateGameList(numJSONObjectToGenerate);

        try {
            writer.println("[");
        } catch (Exception e) {
            System.out.println("Write to file failed. Please try running program again!");
            return;
        }

        //generate the json object and write to file
        int curGameNumber = 1;
        while (numJSONObjectToGenerate-- > 0 && !users.isEmpty()) {
            User curUser;
            do {
                curUser = users.get((int) (Math.random() * users.size()));
            } while (!curUser.hasGameInQue() && users.remove(curUser));

            if (!curUser.isGameStarted()) {
                curUser.setGameNumber(curGameNumber++);
            }

            try {
                writer.println(curUser.step());
            } catch (Exception e) {
                System.out.println("Write to file failed. Please try running program again!");
                return;
            }
            // clean up for the current user
            if (!curUser.hasGameInQue()) users.remove(curUser);

            if (users.isEmpty() && numJSONObjectToGenerate > 0){
                users = generateGameList(Integer.parseInt(args[1]));
            }
        }

        try {
            writer.println("]");
        } catch (Exception e) {
            System.out.println("Write to file failed. Please try running program again!");
            return;
        }

        writer.close();
    }

    private static ArrayList<User> generateGameList(int numJSONObjectToGenerate) {
        ArrayList<User> retVal = new ArrayList<>();

        Random gen = new Random();

        int totalGames = numJSONObjectToGenerate / (Math.abs((int) (gen.nextGaussian() * 5 + 36)) + 9);

        for (int i = 1; i <= totalGames; i++) {
            int maxStep = Math.abs((int) (gen.nextGaussian() * 5 + 36)) + 9;
            int id = gen.nextInt(9999) + 1;
            Game game = new Game(maxStep, "u"+id);
            User temp = new User("u" + id);

            if (retVal.contains(temp)) {
                temp = retVal.get(retVal.indexOf(temp));
                temp.addGameToQue(game);
            }
            else {
                temp.addGameToQue(game);
                retVal.add(temp);
            }
        }
        return retVal;
    }
}

class User {
    private String UserID;
    private ArrayList<Game> gamesQue;

    public User(String usrnm) {
        UserID = usrnm;
        gamesQue = new ArrayList<>();
    }

    public void addGameToQue(Game g) {
        gamesQue.add(g);
    }

    public boolean isGameStarted() {
        return gamesQue.get(0).getGameNumber() != -1;
    }

    public void setGameNumber(int gameNum) {
        gamesQue.get(0).setGameNumber(gameNum);
    }

    public boolean hasGameInQue() {
        return !gamesQue.isEmpty();
    }

    public JsonObject step() {
        if (hasGameInQue()) {
            JsonObject retVal = gamesQue.get(0).step();

            if (gamesQue.get(0).isGameOver()) {
                gamesQue.remove(0);
            }

            return retVal;
        }

        return null;
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
    private int[] specialMove;

    public Game(int mxStps, String userID) {
        maxSteps = mxStps;
        gameNumber = -1;
        UserID = userID;
        gameComplete = false;
        gameBegan = false;
        curStep = 1;
        points = 0;
        specialMove = new int[4];
    }

    //need to implement the game result generation algorithm
    public JsonObject step() {
        JsonObject obj = null;
        if (!gameBegan) {
            obj = Json.createObjectBuilder()
                    .add("game", gameNumber)
                    .add("action", Json.createObjectBuilder()
                            .add("actionType", "gameStart")
                            .add("actionNumber", curStep))
                    .add("user", getUserID())
                    .build();
            gameBegan = true;
        }
        else if (!isGameOver()){
            String action = getAction();
            obj = Json.createObjectBuilder()
                    .add("actionNumber", curStep)
                    .build();
        }
//        else if (!isGameOver())
//        else {
//
//        }

        curStep++;

        return obj;
    }

    private String getAction() {

        return "";
    }

    public boolean isGameOver() { return gameComplete || curStep == maxSteps; }

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