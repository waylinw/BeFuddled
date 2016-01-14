Lab 1 Part 2, CPE 369 Section 3
Group Members: Waylin Wang, Myron Zhao

beFuddled Parser (beFuddledStats.java)
________________
To Compile:
javac -cp javax.json-1.0.2.jar beFuddledStats.java

To Run:
java -cp .:./javax.json-1.0.2.jar beFuddledStats [beFuddled game data path] [optional output file name]

Command Line parameters:
arg[0] - the name of the input file that contains the beFuddled game data

arg[1] - optional, will generate a json object and write to the provided file name

This program reads in a json array of beFuddled game json object data, and provides some statistical analysis on the given input.
It can optionally output to a specified file as well.

ThghtShre Parser (thghtShreStats.java)
______________
To compile:
javac -cp *.jar thghtShreStats.java

To run:
java -cp .:./javax.json-1.0.2.jar thghtShreStats [thghtShre data path] [optional output file name]

Command Line parameters:
arg[0] - the name of the input file that contains the thghtShre message data

arg[1] - optional, will generate a json object and write to the provided file name

This program reads in a json array of thghtShre json object message data, and provides some statistical analysis on the given input.
It can optionally output to a specified file as well.