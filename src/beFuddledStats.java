import javax.json.*;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

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

        ArrayList<UserStat> userList = new ArrayList<>();
        ArrayList<GameStat> gameList = new ArrayList<>();
        ArrayList<LocCount> locList = new ArrayList<>();
        int [] specialMoves = new int[4];
        int [] numMoves = new int[200];
        int [] numMovesWon = new int[200];
        int [] numMovesLoss = new int[200];

        while (parser.hasNext()) {
            jsonEvent = parser.next();

            if (jsonEvent == JsonParser.Event.START_OBJECT) {
                jsonEvent = parser.next();
                GameStat gstat = null;
                UserStat ustat = null;

                while (jsonEvent != JsonParser.Event.END_OBJECT) {
                    if (jsonEvent == JsonParser.Event.KEY_NAME) {
                        switch(parser.getString()) {
                            case "game":
                                jsonEvent = parser.next(); //reads in game number
                                gstat = new GameStat(parser.getInt());
                                if (gameList.contains(gstat)) {
                                    gstat = gameList.get(gameList.indexOf(gstat));
                                }
                                else {
                                    gameList.add(gstat);
                                }
                                break;
                            case "action":
                                jsonEvent = parser.next();
                                jsonEvent = parser.next();//skip the value and object opening
                                while (jsonEvent != JsonParser.Event.END_OBJECT) { //reads in the action object
                                    switch (parser.getString()) {
                                        case "actionType":
                                            jsonEvent = parser.next();
                                            switch(parser.getString()) {
                                                case "gameStart" :
                                                    gstat.addMoves();
                                                    break;
                                                case "Move":
                                                    gstat.addMoves();
                                                    break;
                                                case "SpecialMove":
                                                    gstat.addMoves();
                                                    break;
                                                case "GameEnd":
                                                    gstat.addMoves();
                                                    gstat.completedGame();
                                                    numMoves[gstat.getMoves()]++;
                                                    break;
                                                default:
                                                    System.out.println("Input file is not for beFuddled, please check the file.");
                                                    return;
                                            }
                                            break;
                                        case "actionNumber"://do nothing, already incrementing moves above
                                            jsonEvent = parser.next();
                                            break;
                                        case "move":
                                            jsonEvent = parser.next();
                                            String sm = parser.getString();
                                            for (int i = 0; i < specialMovesList.length; i++)
                                                if (specialMovesList[i].equals(sm))
                                                    specialMoves[i]++;
                                            break;
                                        case "pointsAdded":
                                            jsonEvent = parser.next(); //dont need to read the individual point delta
                                            break;
                                        case "points":
                                            jsonEvent = parser.next();
                                            gstat.setPoint(parser.getInt());
                                            break;
                                        case "location"://to be implemented with x and y coordinate
                                            jsonEvent = parser.next();
                                            jsonEvent = parser.next();
                                            jsonEvent = parser.next();// skip to the value
                                            int x = parser.getInt();
                                            jsonEvent = parser.next();
                                            jsonEvent = parser.next();
                                            int y = parser.getInt();
                                            Point2D temp = new Point2D.Float(x,y);
                                            LocCount tempLoc = new LocCount(temp);
                                            if (locList.contains(tempLoc)) {
                                                tempLoc = locList.get(locList.indexOf(tempLoc));
                                                tempLoc.addCount();
                                            }
                                            else {
                                                locList.add(tempLoc);
                                            }
                                            jsonEvent = parser.next();
                                            break;
                                        case "gameStatus":
                                            jsonEvent = parser.next();
                                            boolean won = parser.getString().equals("Win");
                                            gstat.setVictory(won);
                                            if (won) {
                                                numMovesWon[gstat.getMoves()]++;
                                            }
                                            else {
                                                numMovesLoss[gstat.getMoves()]++;
                                            }
                                            break;
                                        default:
                                            System.out.println("Input file is not for beFuddled, please check the file.");
                                            return;
                                    }
                                    jsonEvent = parser.next();
                                }
                                break;
                            case "user":
                                jsonEvent = parser.next(); // reads in username
                                ustat = new UserStat(parser.getString());
                                if (userList.contains(ustat)) {
                                    ustat = userList.get(userList.indexOf(ustat));
                                    if (gstat.getMoves() == 1) {
                                        ustat.gameStarted();
                                    }
                                }
                                else {
                                    if (gstat.getMoves() == 1) {
                                        ustat.gameStarted();
                                    }
                                    userList.add(ustat);
                                }
                                break;
                            default:
                                System.out.println("Input file is not for beFuddled, please check the file.");
                                return;
                        }
                    }
                    jsonEvent = parser.next();
                }

                AggregrateData(ustat, gstat);

                if (!userList.contains(ustat))
                    userList.add(ustat);
                if (!gameList.contains(gstat))
                    gameList.add(gstat);
            }
        }

        //Compute result
        BR5 BR5Data = getBR5Data(gameList);
        gameList = null; //zero out gameList so we have memory for iterating other items below

        System.out.println("Total Games In Input File: " + (int)BR5Data.totalGames);
        System.out.println();
        System.out.println("Total Completed Games: " + (int)BR5Data.totalCompGames);
        System.out.printf("Completed Games Avg Score: %.2f Std Dev: %.2f\n" ,BR5Data.totalPtsAvg, BR5Data.totalPtsStdDev );
        System.out.println("Total Games Won: " + (int)BR5Data.totalWin);
        System.out.printf("Won Games Avg Score: %.2f Std Dev: %.2f\n" ,BR5Data.wonPtsAvg, BR5Data.wonPtsStdDev );
        System.out.println("Total Games Loss: " + (int)BR5Data.totalLoss);
        System.out.printf("Loss Games Avg Score: %.2f Std Dev: %.2f\n" ,BR5Data.lossPtsAvg, BR5Data.lossPtsStdDev );
        System.out.println();

        //BR6 results
        BR6 BR6Data = getBR6Data(numMoves, numMovesWon, numMovesLoss);

        System.out.printf("Completed Games Avg Move: %.2f Std Dev: %.2f\n", BR6Data.totalMovesAvg, BR6Data.totalMovesStdDev);
        System.out.printf("Won Games Avg Move: %.2f Std Dev: %.2f\n", BR6Data.winMovesAvg, BR6Data.winMovesStdDev);
        System.out.printf("Loss Games Avg Move: %.2f Std Dev: %.2f\n", BR6Data.lossMovesAvg, BR6Data.lossMovesStdDev);
        System.out.printf("Histogram of Moves in Completed Games:\n");
        System.out.printf("[0, 15): %d\n", BR6Data.histogram[0]);
        System.out.printf("[15, 30): %d\n", BR6Data.histogram[1]);
        System.out.printf("[30, 40): %d\n", BR6Data.histogram[2]);
        System.out.printf("[40, 50): %d\n", BR6Data.histogram[3]);
        System.out.printf("[50, 60): %d\n", BR6Data.histogram[4]);
        System.out.printf("[60, 80): %d\n", BR6Data.histogram[5]);
        System.out.printf("[80, inf): %d\n", BR6Data.histogram[6]);
        System.out.println();

        BR7 BR7Data = getBR7Data(userList);

        System.out.println(BR7Data.numUserStartedGame + " users started at least one game.");
        System.out.println(BR7Data.numUserCompletedGame + " users completed at least one game.");
        System.out.println();
        System.out.println("Largest number of games started is: " + BR7Data.largestNumGameStarted);
        if (BR7Data.largestNumGameStartedUser.size() > 10) {
            System.out.println(BR7Data.largestNumGameStartedUser.size() + " users started the largest number of games.");
        }
        else {
            System.out.println("These users started the largest number of games: ");
            for (String temp : BR7Data.largestNumGameStartedUser)
                System.out.print(temp + " ");
            System.out.println();
        }
        System.out.println();
        System.out.println("Largest number of games completed is: " + BR7Data.largestNumGamesCompleted);
        if (BR7Data.largestNumGameCompletedUser.size() > 10) {
            System.out.println(BR7Data.largestNumGameCompletedUser.size() + " users completed the largest number of games.");
        }
        else {
            System.out.println("The following users completed the largest number of games: ");
            for (String temp : BR7Data.largestNumGameCompletedUser)
                System.out.print(temp + " ");
            System.out.println();
        }
        System.out.println();
        System.out.println("Largest number of wins a single user had was: " + BR7Data.largestNumWin);
        if (BR7Data.largestNumWinsUser.size() > 10) {
            System.out.println(BR7Data.largestNumWinsUser.size() + " users won the largest number of games.");
        }
        else {
            System.out.println("The following users won the largest number of games: ");
            for (String temp : BR7Data.largestNumWinsUser)
                System.out.print(temp + " ");
            System.out.println();
        }
        System.out.println();
        System.out.println("Largest number of losses a single user had was: " + BR7Data.largestSingleLoss);
        if (BR7Data.largestLossUser.size() > 10) {
            System.out.println(BR7Data.largestLossUser.size() + " users lost the largest number of games.");
        }
        else {
            System.out.println("The following users lost the largest number of games: ");
            for (String temp : BR7Data.largestLossUser)
                System.out.print(temp + " ");
            System.out.println();
        }
        System.out.println();
        System.out.println("The longest game contained " + BR7Data.longestGameMove + " moves");
        if (BR7Data.longestGamesUser.size() > 10) {
            System.out.println(BR7Data.longestGamesUser.size() + " users played the longest games recorded.");
        }
        else {
            System.out.println("The following users played the longest games: ");
            for (String temp : BR7Data.longestGamesUser)
                System.out.print(temp + " ");
            System.out.println();
        }

        //For most popular locations
        Collections.sort(locList, new favoriteLocations() );
        System.out.println("These are the top 10 most frequent move locations listed from highest to lowest.");
        for (int i = 0; i < 10; i++) {
            System.out.println(locList.get(i).toString());
        }

        //For Most Popular moves
        System.out.println();
        System.out.println("Special Move Usages: ");
        System.out.println(specialMovesList[0] + ": " + specialMoves[0]);
        System.out.println(specialMovesList[1] + ": " + specialMoves[1]);
        System.out.println(specialMovesList[2] + ": " + specialMoves[2]);
        System.out.println(specialMovesList[3] + ": " + specialMoves[3]);


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

    /**
     * Saves data to the user to be stored.
     * @param ustat
     * @param gstat
     */
    private static void AggregrateData(UserStat ustat, GameStat gstat) {
        if (gstat.isComplete()) {
            ustat.gameCompleted(); //set game to completed in user

            if (gstat.isWon())
                ustat.gameWon();
            else
                ustat.gameLoss();

            ustat.setHighestMove(gstat.getMoves()); //sets the highest move
            ustat.setTopScore(gstat.getPoints()); //sets the highest point

        }
    }

    private static BR5 getBR5Data(ArrayList<GameStat> gList) {
        double totalSum = 0, totalCompGameCount = 0, winSum = 0, winGameCount = 0, lossSum = 0, lossGameCount = 0;
        double totalSumSqrd = 0, winSumSqrd = 0, lossSumSqrd = 0;
        double totalStdDev = 0, winStdDev = 0, lossStdDev = 0;

        for (GameStat stat: gList) {
            if (!stat.isComplete())
                continue;
            totalCompGameCount++;
            totalSum += stat.getPoints();
            totalSumSqrd += stat.getPoints() * stat.getPoints();
            if (stat.isWon()) {
                winSum += stat.getPoints();
                winSumSqrd += stat.getPoints() * stat.getPoints();
                winGameCount++;
            } else {
                lossSum += stat.getPoints();
                lossSumSqrd += stat.getPoints() * stat.getPoints();
                lossGameCount++;
            }
        }
        //formula from http://mathforum.org/library/drmath/view/61381.html
        totalStdDev = Math.sqrt((totalSumSqrd - totalSum*totalSum/totalCompGameCount)/totalCompGameCount);
        winStdDev = Math.sqrt((winSumSqrd - winSum*winSum/winGameCount)/winGameCount);
        lossStdDev = Math.sqrt((lossSumSqrd - lossSum*lossSum/lossGameCount)/lossGameCount);

        double totalAvg = totalSum / totalCompGameCount;
        double winAvg = winSum / winGameCount;
        double lossAvg = lossSum / lossGameCount;


        return new BR5(totalAvg, totalStdDev, winAvg, winStdDev,lossAvg, lossStdDev,
                gList.size(), totalCompGameCount, winGameCount, lossGameCount);
    }

    private static BR6 getBR6Data(int[] totalMoves, int[] movesWon, int[] movesLoss) {
        double totalMovesAvg = 0, totalMovesStdDev = 0, totalMove = 0, totalMovesSqrd = 0, totalMovesCount = 0;
        double winMovesAvg = 0l, winMovesStdDev = 0, totalWinMove = 0, totalWinMovesSqrd = 0, totalWinCount = 0;
        double lossMovesAvg = 0l, lossMovesStdDev = 0, totalLossMove = 0, totalLossMovesSqrd = 0, totalLossCount = 0;
        int[] hist = new int[7];

        for (int i = 0; i < totalMoves.length; i++) {
            totalMovesCount += totalMoves[i];
            double val = totalMoves[i] * i;
            totalMove += val;
            totalMovesSqrd += (i * i) * totalMoves[i];
            if (i < 15) {
                hist[0]+=totalMoves[i];
            }
            else if (i < 30) {
                hist[1]+=totalMoves[i];
            }
            else if (i < 40) {
                hist[2]+=totalMoves[i];
            }
            else if (i < 50) {
                hist[3]+=totalMoves[i];
            }
            else if (i < 60) {
                hist[4]+=totalMoves[i];
            }
            else if (i < 80) {
                hist[5]+=totalMoves[i];
            }
            else {
                hist[6]+=totalMoves[i];
            }

            if (movesWon[i] > 0) {
                double wonVal = movesWon[i] * i;
                totalWinCount += movesWon[i];
                totalWinMove += wonVal;
                totalWinMovesSqrd += (i * i) * movesWon[i];
            }

            if (movesLoss[i] > 0) {
                double lossVal = movesLoss[i] * i;
                totalLossCount += movesLoss[i];
                totalLossMove += lossVal;
                totalLossMovesSqrd += (i * i) *movesLoss[i];
            }
        }

        totalMovesAvg = totalMove / totalMovesCount;
        totalMovesStdDev = Math.sqrt((totalMovesSqrd - totalMove * totalMove / totalMovesCount) / totalMovesCount);

        winMovesAvg = totalWinMove / totalWinCount;
        winMovesStdDev = Math.sqrt((totalWinMovesSqrd - totalWinMove * totalWinMove / totalWinCount) / totalWinCount);

        lossMovesAvg = totalLossMove / totalLossCount;
        lossMovesStdDev = Math.sqrt((totalLossMovesSqrd - totalLossMove * totalLossMove / totalLossCount) / totalLossCount);


        return new BR6(hist, totalMovesAvg, totalMovesStdDev, winMovesAvg, winMovesStdDev, lossMovesAvg, lossMovesStdDev);
    }

    private static BR7 getBR7Data(ArrayList<UserStat> uList) {
        ArrayList<String> largestNumGameStartedUser = new ArrayList<>();
        ArrayList<String> largestNumGameCompletedUser = new ArrayList<>();
        ArrayList<String> largestNumWinsUser = new ArrayList<>();
        ArrayList<String> largestNumLossUser = new ArrayList<>();
        ArrayList<String> longestGamesUser = new ArrayList<>();
        int largestNumGameStarted = 0;
        int largestNumGameCompleted = 0;
        int largestNumWin = 0;
        int largestNumLoss = 0;
        int numUserStarted = 0;
        int numUserCompleted = 0;

        Collections.sort(uList, new largestGameStarted());
        largestNumGameStarted = uList.get(0).getGameStarted();
        for (UserStat temp : uList) {
            if (temp.getGameStarted() == largestNumGameStarted)
                largestNumGameStartedUser.add(temp.getUserID());
            if (temp.getGameStarted() == 0)
                break;
            numUserStarted++;
        }

        Collections.sort(uList, new largestGameCompleted());
        largestNumGameCompleted = uList.get(0).getGamesCompleted();
        for (UserStat temp : uList) {
            if (temp.getGamesCompleted() == largestNumGameCompleted)
                largestNumGameCompletedUser.add(temp.getUserID());
            if (temp.getGamesCompleted() == 0)
                break;
            numUserCompleted++;
        }

        Collections.sort(uList, new largestNumWins());
        largestNumWin = uList.get(0).getGameWon();
        for (UserStat temp : uList) {
            if (temp.getGameWon() < largestNumWin)
                break;
            largestNumWinsUser.add(temp.getUserID());
        }

        Collections.sort(uList, new largestNumLoss());
        largestNumLoss = uList.get(0).getGameLoss();
        for (UserStat temp : uList) {
            if (temp.getGameLoss() < largestNumLoss)
                break;
            largestNumLossUser.add(temp.getUserID());
        }
        Collections.sort(uList, new mostMoves());
        int longestGame = uList.get(0).getHighestMove();
        for (UserStat temp : uList) {
            if (temp.getHighestMove() < longestGame)
                break;
            longestGamesUser.add(temp.getUserID());
        }



        return new BR7(largestNumGameStartedUser,largestNumGameCompletedUser,largestNumWinsUser,largestNumLossUser,
                longestGamesUser,numUserStarted, numUserCompleted,
                largestNumGameStarted, largestNumWin, largestNumLoss, longestGame, largestNumGameCompleted);
    }

    private static final String[] specialMovesList = new String[] {"Shuffle", "Clear", "Invert", "Rotate"};
}

//Comparator methods fo BR7
class largestGameStarted implements Comparator<UserStat> {
    @Override
    public int compare(UserStat o1, UserStat o2) {
        return o1.getGameStarted() < o2.getGameStarted() ? 1 : o1.getGameStarted() == o2.getGameStarted() ? 0 : -1;
    }
}
class largestGameCompleted implements Comparator<UserStat> {
    @Override
    public int compare(UserStat o1, UserStat o2) {
        return o1.getGamesCompleted() < o2.getGamesCompleted() ? 1 :
                o1.getGamesCompleted() == o2.getGamesCompleted() ? 0 : -1;
    }
}
class largestNumWins implements Comparator<UserStat> {
    @Override
    public int compare(UserStat o1, UserStat o2) {
        return o1.getGameWon() < o2.getGameWon() ? 1 :
                o1.getGameWon() == o2.getGameWon() ? 0 : -1;
    }
}
class largestNumLoss implements Comparator<UserStat> {
    @Override
    public int compare(UserStat o1, UserStat o2) {
        return o1.getGameLoss() < o2.getGameLoss() ? 1 :
                o1.getGameLoss() == o2.getGameLoss() ? 0 : -1;
    }
}
class mostMoves implements Comparator<UserStat> {
    @Override
    public int compare(UserStat o1, UserStat o2) {
        return o1.getHighestMove() < o2.getHighestMove() ? 1 :
                o1.getHighestMove() == o2.getHighestMove() ? 0 : -1;
    }
}

//Comparator for locations
class favoriteLocations implements Comparator<LocCount> {
    @Override
    public int compare(LocCount o1, LocCount o2) {
        return o1.getCount() < o2.getCount() ? 1 :
                o1.getCount() == o2.getCount() ? 0 : -1;
    }
}

class GameStat {
    private boolean isCompleted;
    private boolean isWon;
    private String userID;
    private int moveCount;
    private int points;
    private int gameNumber;

    public GameStat(int gN) {
        isCompleted = false;
        isWon = false;
        gameNumber = gN;
        moveCount = 0;
        points = 0;
    }

    public int getGameNumber() {
        return gameNumber;
    }

    public void addMoves() {
        moveCount++;
    }

    public void completedGame() {
        isCompleted = true;
    }

    public void setVictory(boolean stat) {
        isWon = stat;
    }

    public void setPoint(int point) {
        points = point;
    }

    public boolean isWon() {
        return isWon;
    }

    public boolean isComplete() {
        return isCompleted;
    }

    public int getPoints() {
        return points;
    }

    public int getMoves() {
        return moveCount;
    }

    public boolean equals(Object obj) {
        return ((GameStat)obj).getGameNumber() == gameNumber;
    }
}

class UserStat {
    private String userID;
    private int topScore;
    private int highestMove;
    private int gamesCompleted;
    private int gamesLoss;
    private int gamesWon;
    private int gamesStarted;

    public UserStat(String uid) {
        userID = uid;
        topScore = highestMove = gamesCompleted = gamesLoss = gamesWon = gamesStarted = 0;
    }

    public String getUserID() {
        return userID;
    }

    public void setTopScore(int s) {
        if (s > topScore)
            topScore = s;
    }

    public void setHighestMove(int m) {
        if (m > highestMove)
            highestMove = m;
    }

    public int getHighestMove() {
        return highestMove;
    }

    public int getGameStarted() {
        return gamesStarted;
    }

    public int getGameWon() {
        return gamesWon;
    }

    public int getGameLoss() {
        return gamesLoss;
    }

    public int getGamesCompleted() {
        return gamesCompleted;
    }

    public void gameCompleted() {
        gamesCompleted++;
    }

    public void gameLoss() {
        gamesLoss++;
    }

    public void gameWon() {
        gamesWon++;
    }

    public void gameStarted() {
        gamesStarted++;
    }


    public boolean equals(Object obj) {
        return ((UserStat)obj).getUserID().equals(this.userID);
    }
}

class LocCount {
    public Point2D loc;
    public int count;

    public LocCount(Point2D pt) {
        this.loc = pt;
        count = 1;
    }

    public int getCount() {
        return count;
    }
    public Point2D getLoc() {
        return loc;
    }
    public void addCount() {
        count++;
    }

    public boolean equals(Object obj) {
        return ((LocCount)obj).getLoc().equals(this.loc);
    }

    public String toString() {
        return "(" + (int)loc.getX() + ", " + (int)loc.getY() + ") has " + count + "hits.";
    }
}

class BR5 {
    public double totalPtsAvg, totalPtsStdDev;
    public double wonPtsAvg, wonPtsStdDev;
    public double lossPtsAvg, lossPtsStdDev;
    public double totalGames, totalCompGames, totalWin, totalLoss;

    public BR5(double totalPtsAvg, double totalPtsStdDev, double wonPtsAvg,
               double wonPtsStdDev, double lossPtsAvg, double lossPtsStdDev,
               double totalGames, double totalCompGames, double totalWin, double totalLoss) {
        this.totalPtsAvg = totalPtsAvg;
        this.totalPtsStdDev = totalPtsStdDev;
        this.wonPtsAvg = wonPtsAvg;
        this.wonPtsStdDev = wonPtsStdDev;
        this.lossPtsAvg = lossPtsAvg;
        this.lossPtsStdDev = lossPtsStdDev;
        this.totalGames = totalGames;
        this.totalCompGames = totalCompGames;
        this.totalWin = totalWin;
        this.totalLoss = totalLoss;
    }
}

class BR6 {
    public double totalMovesAvg, totalMovesStdDev;
    public double winMovesAvg, winMovesStdDev;
    public double lossMovesAvg, lossMovesStdDev;
    public int[] histogram;

    public BR6(int[] hist, double totalMovesAvg, double totalMovesStdDev, double winMovesAvg, double winMovesStdDev, double lossMovesAvg, double lossMovesStdDev) {
        histogram = hist;
        this.totalMovesAvg = totalMovesAvg;
        this.totalMovesStdDev = totalMovesStdDev;
        this.winMovesAvg = winMovesAvg;
        this.winMovesStdDev = winMovesStdDev;
        this.lossMovesAvg = lossMovesAvg;
        this.lossMovesStdDev = lossMovesStdDev;
    }
}

class BR7 {
    public ArrayList<String> largestNumGameStartedUser;
    public ArrayList<String> largestNumGameCompletedUser;
    public ArrayList<String> largestNumWinsUser;
    public ArrayList<String> largestLossUser;
    public ArrayList<String> longestGamesUser;
    public int numUserStartedGame;
    public int numUserCompletedGame;
    public int largestNumGameStarted;
    public int largestNumGamesCompleted;
    public int largestNumWin;
    public int largestSingleLoss;
    public int longestGameMove;

    public BR7(ArrayList<String> largestNumGameStartedUser, ArrayList<String> largestNumGameCompletedUser,
               ArrayList<String> largestNumWinsUser, ArrayList<String> largestLossUser,
               ArrayList<String> longestGamesUser, int numUserStartedGame, int numUserCompletedGame,
               int largestNumGameStarted, int largestNumWin, int largestSingleLoss, int longestGameMove,
               int largestNumGamesCompleted) {
        this.largestNumGameStartedUser = largestNumGameStartedUser;
        this.largestNumGameCompletedUser = largestNumGameCompletedUser;
        this.largestNumWinsUser = largestNumWinsUser;
        this.largestLossUser = largestLossUser;
        this.longestGamesUser = longestGamesUser;
        this.numUserStartedGame = numUserStartedGame;
        this.numUserCompletedGame = numUserCompletedGame;
        this.largestNumGameStarted = largestNumGameStarted;
        this.largestNumWin = largestNumWin;
        this.largestSingleLoss = largestSingleLoss;
        this.longestGameMove = longestGameMove;
        this.largestNumGamesCompleted = largestNumGamesCompleted;
    }
}