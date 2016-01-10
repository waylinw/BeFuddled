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
    private Random generator;

    public Game(int mxStps, String userID) {
        maxSteps = mxStps;
        gameNumber = -1;
        UserID = userID;
        gameComplete = false;
        gameBegan = false;
        curStep = 0;
        points = 0;
        specialMove = new int[4];
        generator = new Random();
    }

    //need to implement the game result generation algorithm
    public JsonObject step() {
        JsonObject obj = null;
        curStep++;
        if (isGameOver()) gameComplete = true;

        if (!gameBegan) {
            obj = Json.createObjectBuilder()
                    .add("game", gameNumber)
                    .add("action", Json.createObjectBuilder()
                            .add("actionType", "gameStart")
                            .add("actionNumber", curStep))
                    .add("user", UserID)
                    .build();
            gameBegan = true;
        }
        else if (!isGameOver()){
            String action = getAction();
            if (action.equals("Move")) {
                int x = getLocation();
                int y = getLocation();
                int point = getPoints(-1.5, 8.5);
                points+= point;
                obj = Json.createObjectBuilder()
                        .add("game", gameNumber)
                        .add("action", Json.createObjectBuilder()
                            .add("actionType", action)
                            .add("pointsAdded", point)
                            .add("actionNumber", curStep)
                            .add("location", Json.createObjectBuilder()
                                .add("x", x)
                                .add("y", y))
                            .add("points", points))
                        .add("user", UserID)
                        .build();
            }
            else {
                String specialMove = getSpecialMoveType();
                int point = getPoints(10, 3);
                points+=point;
                obj = Json.createObjectBuilder()
                        .add("game", gameNumber)
                        .add("action", Json.createObjectBuilder()
                                .add("actionType", action)
                                .add("move", specialMove)
                                .add("pointsAdded", point)
                                .add("actionNumber", curStep)
                                .add("points", points))
                        .add("user", UserID)
                        .build();
            }
        }
        else if (isGameOver()) {
            obj = Json.createObjectBuilder()
                    .add("game", gameNumber)
                    .add("action", Json.createObjectBuilder()
                            .add("actionType", "GameEnd")
                            .add("actionNumber", curStep)
                            .add("points", points)
                            .add("gameStatus", points > 1 ? "Win" : "Loss"))
                    .add("user", UserID)
                    .build();
        }

        return obj;
    }

    /**
     * gets the action type with less than 5% chance of getting a special move
     * @return Move or SpecialMove
     */
    private String getAction() {
        boolean specialMove = generator.nextDouble() < .05;
        if (!specialMove || !isSpecialMoveAvailable()) {
            return "Move";
        }
        else {
            return "SpecialMove";
        }
    }

    /**
     * Checks and see if any special moves are still available
     * @return true/false
     */
    private boolean isSpecialMoveAvailable() {
        int sum = 0;
        for (int i : specialMove)
            sum +=i;
        return sum < 4;
    }

    /**
     * Return random int from normal distribution with mean = 10 and sigma = 4.5
     * @return int location from 1 to 20
     */
    private int getLocation() {
        int loc = Math.abs((int) (generator.nextGaussian() * 4.5 + 10));

        if (loc < 1)
            loc = 1;
        if (loc > 20)
            loc = 20;

        return loc;
    }

    /**
     * returns a random int generated from normal distribution with given mean and sigma
     * This is used for the score
     * @param mean average score
     * @param sigma standard deviation
     * @return int points from -20 to 20
     */
    private int getPoints(double mean, double sigma) {
        int pt = (int) (generator.nextGaussian() * sigma + mean);
        if (pt < -20)
            pt = -20;
        if (pt > 20)
            pt = 20;

        return pt;
    }


    private String getSpecialMoveType() {
        int moveType = 3;

        double move = generator.nextDouble();
        if (move < .3) {
            moveType = 0;
        }
        else if (move < .55) {
            moveType = 1;
        }
        else if (move < .78) {
            moveType = 2;
        }

        while (specialMove[moveType] != 0) {
            moveType++;
            if (moveType == 4)
                moveType = 0;
        }

        specialMove[moveType] = 1;

        return specialMovesList[moveType];
    }

    public boolean isGameOver() { return gameComplete || curStep == maxSteps; }

    public int getGameNumber() {
        return gameNumber;
    }

    public void setGameNumber(int gameNumber) {
        this.gameNumber = gameNumber;
    }

    private static final String[] specialMovesList = new String[] {"Shuffle", "Clear", "Invert", "Rotate"};
}