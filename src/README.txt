This is a readme file for both beFuddledGen.java and thghtShre.java
Both java files use the javax.json-1.0.2.jar external json library.

beFuddledGen.java
—————————————————
To compile:
javac -cp javax.json-1.0.2.jar beFuddledGen.java

To run:
java -cp .:./javax.json-1.0.2.jar beFuddledGen[output file path] [number of objects to generate]

Command Line parameters:
arg[0] - the output file name

arg[1] - the number of JSON objects to generate for the game

The output file does not have beauty-print, it prints the output line by line.

The number of total games to play is generated by dividing the given JsonObject by a random number selected from a normal distribution mean of 36 and std dev of 5, then right shifted by 9 to guarantee that we have the minimum number of steps required.

The number of steps per game is then decided by picking from the same normal distribution, but it’s not the same as the number used above.

Probability of each event is documented in the code. All events are generated from a normal distribution with given mean and standard deviation if not specified below. 

Special Move: 5% chance for every move.
              30% chance for Shuffle
              25% chance for Clear
              23% chance for Invert
              22% chance for Rotate



thghtShre.java
______________
To compile:
javac -cp *.jar thghtShre.java

To run:
java -cp .:./javax.json-1.0.2.jar thghtShre [output file name] [number of
objects to generate] [optional text file]

Command line parameters:
arg[0] - the output file name, the program will generate the JSON objects and
         write them to a file with this name

arg[1] - number of JSON objects to generate, this is the number of JSON
         objects desired

arg[2] - OPTIONAL text file, this text file is used to randomly generate
         message text. This file MUST have one word per line. The program treats         
         each line as a single word. This file must also have at least one word in it.

General Assumptions:
80% of messages are public, 10% are private, and 10% are protected.

40% of public messages are to all, 40% are to subscribers, 15% are to a
specific user, and 5% are to self.

90% of private messages are to a specific user and 10% are to self.

85% of protected messages are to subscribers, 10% are to specific users, and
5% are to self.

70% of messages are in-response to an earlier message and 30% are not
in-response.
