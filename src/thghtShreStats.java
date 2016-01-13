/**
 * CPE 369, Lab 1 Part 2, 1/11/2016
 * Waylin Wang, Myron Zhao
 */

import javax.json.*;
import javax.json.stream.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class thghtShreStats {

    public static final int numUsers = 10000;
    public static final int maxWords = 20;

    public static void main(String[] args) {
        String inputFile;
        String outputFile;
        String line;
        BufferedReader bufReader;
        PrintWriter writer = null;
        JsonParser.Event jsonEvent;
        StringTokenizer tokenizer;
        Message curMsg = new Message();
        FileWriter fileWriter = null;

        //A LOT of things to tally
        Boolean[] uniqueUsers = new Boolean[numUsers];
        int[] numWords = new int[maxWords + 1];
        int numUniqueUsers = 0;
        int numMessages = 0;
        int numPublic = 0;
        int numProtected = 0;
        int numPrivate = 0;
        int numToAll = 0;
        int numToSelf = 0;
        int numToSubscribers = 0;
        int numToUser = 0;
        int numInResponse = 0;
        int numNotResponse = 0;
        int numPublicResponse = 0;
        int numProtectedResponse = 0;
        int numPrivateResponse = 0;
        int numAllResponse = 0;
        int numSelfResponse = 0;
        int numSubscribersResponse = 0;
        int numUserResponse = 0;
        int totalNumWords = 0;
        int totalNumChars = 0;
        int numWordsResponse = 0;
        int numCharsResponse = 0;
        int numWordsNotResponse = 0;
        int numCharsNotResponse = 0;
        int numWordsPublic = 0;
        int numWordsProtected = 0;
        int numWordsPrivate = 0;
        int numWordsAll = 0;
        int numWordsSelf = 0;
        int numWordsSubscribers = 0;
        int numWordsUser = 0;
        int numCharsPublic = 0;
        int numCharsProtected = 0;
        int numCharsPrivate = 0;
        int numCharsAll = 0;
        int numCharsSelf = 0;
        int numCharsSubscribers = 0;
        int numCharsUser = 0;
        int squaredTotalWords = 0;
        int squaredTotalChars = 0;
        int squaredPublicWords = 0;
        int squaredPublicChars = 0;
        int squaredProtectedWords = 0;
        int squaredProtectedChars = 0;
        int squaredPrivateWords = 0;
        int squaredPrivateChars = 0;
        int squaredToAllWords = 0;
        int squaredToAllChars = 0;
        int squaredToSelfWords = 0;
        int squaredToSelfChars = 0;
        int squaredToSubscribersWords = 0;
        int squaredToSubscribersChars = 0;
        int squaredToUserWords = 0;
        int squaredToUserChars = 0;
        int squaredInResponseWords = 0;
        int squaredInResponseChars = 0;
        int squaredNotResponseWords = 0;
        int squaredNotResponseChars = 0;
        int numPublicAll = 0;
        int numPublicSelf = 0;
        int numPublicSubscribers = 0;
        int numPublicUser = 0;
        int numProtectedAll = 0;
        int numProtectedSelf = 0;
        int numProtectedSubscribers = 0;
        int numProtectedUser = 0;
        int numPrivateAll = 0;
        int numPrivateSelf = 0;
        int numPrivateSubscribers = 0;
        int numPrivateUser = 0;

        //Initialize unique users
        for (int i = 0; i < numUsers; i++) {
            uniqueUsers[i] = false;
        }

        //Error checking for command line arguments
        if (args.length < 1) {
            System.out.println("Please check README for usage");
            return;
        }

        //Choose file to read from
        if (args.length > 0) {
            inputFile = args[0];
        }
        else {
            System.out.println("Please check README for usage");
            return;
        }

        //Setup file output writer
        if (args.length > 1) {
            outputFile = args[1];
            try {
                fileWriter = new FileWriter(outputFile);
                writer = new PrintWriter(fileWriter);
            } catch (Exception e) {
                System.out.println("Create file failed. Please try again!");
                return;
            }
        }

        //Read lines from input file
        try {
            bufReader = new BufferedReader(new FileReader(inputFile));
        }
        catch (Exception e){
            System.out.println("Error reading from " + inputFile + ". Please try again!");
            return;
        }

        //Set configuration for JSON factory
        Map<String, Boolean> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);

        //Create a JSON parser
        JsonParserFactory parserFactory = Json.createParserFactory(config);
        JsonParser jsonParser = parserFactory.createParser(bufReader);

        //Create a JSON builder factory
        JsonBuilderFactory builderFactory = Json.createBuilderFactory(config);
        JsonWriterFactory writerFactory = Json.createWriterFactory(config);


        //Parse JSON objects
        while (jsonParser.hasNext()) {
            jsonEvent = jsonParser.next();

            //Count dependencies in end
            if (jsonEvent == JsonParser.Event.END_OBJECT) {
                //Counts number for recepients
                if (curMsg.getRecepient().equals("all")) {
                    numWordsAll += curMsg.getNumWords();
                    numCharsAll += curMsg.getNumChars();
                    squaredToAllWords += curMsg.getNumWords()*curMsg.getNumWords();
                    squaredToAllChars += curMsg.getNumChars()*curMsg.getNumChars();

                    if (curMsg.getStatus().equals("public")) {
                        numPublicAll++;
                    } else if (curMsg.getStatus().equals("protected")) {
                        numProtectedAll++;
                    } else {
                        numPrivateAll++;
                    }
                }
                else if (curMsg.getRecepient().equals("self")) {
                    numWordsSelf += curMsg.getNumWords();
                    numCharsSelf += curMsg.getNumChars();
                    squaredToSelfWords += curMsg.getNumWords()*curMsg.getNumWords();
                    squaredToSelfChars += curMsg.getNumChars()*curMsg.getNumChars();

                    if (curMsg.getStatus().equals("public")) {
                        numPublicSelf++;
                    } else if (curMsg.getStatus().equals("protected")) {
                        numProtectedSelf++;
                    } else {
                        numPrivateSelf++;
                    }
                }
                else if (curMsg.getRecepient().equals("subscribers")) {
                    numWordsSubscribers += curMsg.getNumWords();
                    numCharsSubscribers += curMsg.getNumChars();
                    squaredToSubscribersWords += curMsg.getNumWords()*curMsg.getNumWords();
                    squaredToSubscribersChars += curMsg.getNumChars()*curMsg.getNumChars();

                    if (curMsg.getStatus().equals("public")) {
                        numPublicSubscribers++;
                    } else if (curMsg.getStatus().equals("protected")) {
                        numProtectedSubscribers++;
                    } else {
                        numPrivateSubscribers++;
                    }
                }
                else {
                    numWordsUser += curMsg.getNumWords();
                    numCharsUser += curMsg.getNumChars();
                    squaredToUserWords += curMsg.getNumWords()*curMsg.getNumWords();
                    squaredToUserChars += curMsg.getNumChars()*curMsg.getNumChars();

                    if (curMsg.getStatus().equals("public")) {
                        numPublicUser++;
                    } else if (curMsg.getStatus().equals("protected")) {
                        numProtectedUser++;
                    } else {
                        numPrivateUser++;
                    }
                }

                //Counts numbers for in-response
                if (curMsg.getInResponse()) {
                    //Counts number of words and characters in-response
                    numWordsResponse += curMsg.getNumWords();
                    numCharsResponse += curMsg.getNumChars();
                    squaredInResponseWords += curMsg.getNumWords()*curMsg.getNumWords();
                    squaredInResponseChars += curMsg.getNumChars()*curMsg.getNumChars();

                    //Counts numbers for statuses
                    if (curMsg.getStatus().equals("public")) {
                        numPublicResponse++;
                    }
                    else if (curMsg.getStatus().equals("protected")) {
                        numProtectedResponse++;
                    }
                    else if (curMsg.getStatus().equals("private")) {
                        numPrivateResponse++;
                    }

                    //Counts number for recepients
                    if (curMsg.getRecepient().equals("all")) {
                        numAllResponse++;
                    }
                    else if (curMsg.getRecepient().equals("self")) {
                        numSelfResponse++;
                    }
                    else if (curMsg.getRecepient().equals("subscribers")) {
                        numSubscribersResponse++;
                    }
                    else {
                        numUserResponse++;
                    }
                } else {
                    //Counts number of words and characters not in-response
                    numNotResponse++;
                    numWordsNotResponse += curMsg.getNumWords();
                    numCharsNotResponse += curMsg.getNumChars();
                    squaredNotResponseWords += curMsg.getNumWords()*curMsg.getNumWords();
                    squaredNotResponseChars += curMsg.getNumChars()*curMsg.getNumChars();
                }

                //Counts numbers for statuses
                if (curMsg.getStatus().equals("public")) {
                    numWordsPublic += curMsg.getNumWords();
                    numCharsPublic += curMsg.getNumChars();
                    squaredPublicWords += curMsg.getNumWords()*curMsg.getNumWords();
                    squaredPublicChars += curMsg.getNumChars()*curMsg.getNumChars();
                }
                else if (curMsg.getStatus().equals("protected")) {
                    numWordsProtected += curMsg.getNumWords();
                    numCharsProtected += curMsg.getNumChars();
                    squaredProtectedWords += curMsg.getNumWords()*curMsg.getNumWords();
                    squaredProtectedChars += curMsg.getNumChars()*curMsg.getNumChars();
                }
                else if (curMsg.getStatus().equals("private")) {
                    numWordsPrivate += curMsg.getNumWords();
                    numCharsPrivate += curMsg.getNumChars();
                    squaredPrivateWords += curMsg.getNumWords()*curMsg.getNumWords();
                    squaredPrivateChars += curMsg.getNumChars()*curMsg.getNumChars();
                }

                numWords[curMsg.getNumWords()]++;


                //Counts number of words and chars overall
                totalNumWords += curMsg.getNumWords();
                totalNumChars += curMsg.getNumChars();
                squaredTotalWords += curMsg.getNumWords()*curMsg.getNumWords();
                squaredTotalChars += curMsg.getNumChars()*curMsg.getNumChars();

                numMessages++;
                curMsg = new Message();
            }

            if (jsonEvent == JsonParser.Event.KEY_NAME) {
                line = jsonParser.getString();

                //Error checking for format
                if (!line.equals("messageID") && !line.equals("user") && !line.equals("status") &&
                        !line.equals("recepient") && !line.equals("text") && !line.equals("in-response")) {
                    System.out.println("Formatting error. Please try again with valid JSON objects!");
                    return;
                }

                //Counts number of unique users
                if (line.equals("user")) {
                    if (jsonParser.hasNext()) {
                        jsonEvent = jsonParser.next();
                        if (jsonEvent == JsonParser.Event.VALUE_STRING) {
                            line = jsonParser.getString();
                            uniqueUsers[Integer.parseInt(line.substring(1)) - 1] = true;
                        }
                    }
                }

                //Count by status
                if (line.equals("status")) {
                    if (jsonParser.hasNext()) {
                        jsonEvent = jsonParser.next();
                        if (jsonEvent == JsonParser.Event.VALUE_STRING) {
                            line = jsonParser.getString();
                            curMsg.setStatus(line);
                            if (line.equals("public")) {
                                numPublic++;
                            }
                            else if (line.equals("protected")) {
                                numProtected++;
                            }
                            else if (line.equals("private")) {
                                numPrivate++;
                            }
                        }
                    }
                }

                //Count by recepient
                if (line.equals("recepient")) {
                    if (jsonParser.hasNext()) {
                        jsonEvent = jsonParser.next();
                        if (jsonEvent == JsonParser.Event.VALUE_STRING) {
                            line = jsonParser.getString();
                            curMsg.setRecepient(line);
                            if (line.equals("all")) {
                                numToAll++;
                            }
                            else if (line.equals("self")) {
                                numToSelf++;
                            }
                            else if (line.equals("subscribers")) {
                                numToSubscribers++;
                            }
                            else {
                                numToUser++;
                            }
                        }
                    }
                }

                //Counts number of in-response
                if (line.equals("in-response")) {
                    curMsg.setInResponse(true);
                    numInResponse++;
                    if (jsonParser.hasNext()) {
                        jsonEvent = jsonParser.next();
                        if (jsonEvent == JsonParser.Event.VALUE_STRING) {
                            line = jsonParser.getString();
                        }
                    }
                }

                //Counts number of words and characters in a message
                if (line.equals("text")) {
                    if (jsonParser.hasNext()) {
                        jsonEvent = jsonParser.next();
                        if (jsonEvent == JsonParser.Event.VALUE_STRING) {
                            line = jsonParser.getString();
                            curMsg.setNumChars(line.toCharArray().length);
                            tokenizer = new StringTokenizer(line);
                            curMsg.setNumWords(tokenizer.countTokens());
                        }
                    }
                }

            }
        }

        //Calculate number of unqiue users
        for (int i = 0; i < numUsers; i++) {
            if (uniqueUsers[i]) {
                numUniqueUsers++;
            }
        }

        //Printing to terminal
        System.out.println("BASIC STATS:");
        System.out.println("Total number of messages: " + numMessages);
        System.out.println("Number of unique users: " + numUniqueUsers);
        System.out.println("Average length in words: " + (double)totalNumWords/numMessages);
        System.out.println("Average length in chars: " + (double)totalNumChars/numMessages);
        System.out.println("Standard deviation of words: " + Math.sqrt((squaredTotalWords - (totalNumWords*totalNumWords/numMessages))/numMessages));
        System.out.println("Standard deviation of chars: " + Math.sqrt((squaredTotalChars - (totalNumChars*totalNumChars/numMessages))/numMessages));
        System.out.println();

        System.out.println("MESSAGE TYPE HISTOGRAMS");
        System.out.println("Number of public messages: " + numPublic);
        System.out.println("Number of protected messages: " + numProtected);
        System.out.println("Number of private messages: " + numPrivate);
        System.out.println();

        System.out.println("Number of messages to all: " + numToAll);
        System.out.println("Number of messages to self: " + numToSelf);
        System.out.println("Number of messages to subscribers: " + numToSubscribers);
        System.out.println("Number of messages to other users: " + numToUser);
        System.out.println();

        System.out.println("Number of messages in-response: " + numInResponse);
        System.out.println("Number of messages not in-response: " + (numNotResponse));
        System.out.println();

        for (int i = 2; i <= maxWords; i++) {
            System.out.println(i + " words: " + numWords[i]);
        }
        System.out.println();

        System.out.println("STATS FOR SUBSETS OF MESSAGES");
        System.out.println("Average length of words for public: " + (double)numWordsPublic/numPublic);
        System.out.println("Average length of chars for public: " + (double)numCharsPublic/numPublic);
        System.out.println("Average length of words for protected: " + (double)numWordsProtected/numProtected);
        System.out.println("Average length of chars for protected: " + (double)numCharsProtected/numProtected);
        System.out.println("Average length of words for private: " + (double)numWordsPrivate/numPrivate);
        System.out.println("Average length of chars for private: " + (double)numCharsPrivate/numPrivate);
        System.out.println("Standard deviation of public words: " + Math.sqrt((squaredPublicWords - (numWordsPublic*numWordsPublic/numPublic))/numPublic));
        System.out.println("Standard deviation of public chars: " + Math.sqrt((squaredPublicChars - (numCharsPublic*numCharsPublic/numPublic))/numPublic));
        System.out.println("Standard deviation of protected words: " + Math.sqrt((squaredProtectedWords - (numWordsProtected*numWordsProtected/numProtected))/numProtected));
        System.out.println("Standard deviation of protected chars: " + Math.sqrt((squaredProtectedChars - (numCharsProtected*numCharsProtected/numProtected))/numProtected));
        System.out.println("Standard deviation of private words: " + Math.sqrt((squaredPrivateWords - (numWordsPrivate*numWordsPrivate/numPrivate))/numPrivate));
        System.out.println("Standard deviation of private chars: " + Math.sqrt((squaredPrivateChars - (numCharsPrivate*numCharsPrivate/numPrivate))/numPrivate));
        System.out.println();

        System.out.println("Average length of words for all: " + (double)numWordsAll/numToAll);
        System.out.println("Average length of chars for all: " + (double)numCharsAll/numToAll);
        System.out.println("Average length of words for self: " + (double)numWordsSelf/numToSelf);
        System.out.println("Average length of chars for self: " + (double)numCharsSelf/numToSelf);
        System.out.println("Average length of words for subscribers: " + (double)numWordsSubscribers/numToSubscribers);
        System.out.println("Average length of chars for subscribers: " + (double)numCharsSubscribers/numToSubscribers);
        System.out.println("Average length of words for users: " + (double)numWordsUser/numToUser);
        System.out.println("Average length of chars for users: " + (double)numCharsUser/numToUser);
        System.out.println("Standard deviation of words to all: " + Math.sqrt((squaredToAllWords - (numWordsAll*numWordsAll/numToAll))/numToAll));
        System.out.println("Standard deviation of chars to all: " + Math.sqrt((squaredToAllChars - (numCharsAll*numCharsAll/numToAll))/numToAll));
        System.out.println("Standard deviation of words to self: " + Math.sqrt((squaredToSelfWords - (numWordsSelf*numWordsSelf/numToSelf))/numToSelf));
        System.out.println("Standard deviation of chars to self: " + Math.sqrt((squaredToSelfChars - (numCharsSelf*numCharsSelf/numToSelf))/numToSelf));
        System.out.println("Standard deviation of words to subscribers: " + Math.sqrt((squaredToSubscribersWords - (numWordsSubscribers*numWordsSubscribers/numToSubscribers))/numToSubscribers));
        System.out.println("Standard deviation of chars to subscribers: " + Math.sqrt((squaredToSubscribersChars - (numCharsSubscribers*numCharsSubscribers/numToSubscribers))/numToSubscribers));
        System.out.println("Standard deviation of words to users: " + Math.sqrt((squaredToUserWords - (numWordsUser*numWordsUser/numToUser))/numToUser));
        System.out.println("Standard deviation of chars to users: " + Math.sqrt((squaredToUserChars - (numCharsUser*numCharsUser/numToUser))/numToUser));
        System.out.println();

        System.out.println("Average length of words for in-response: " + (double)numWordsResponse/numInResponse);
        System.out.println("Average length of chars for in-response: " + (double)numCharsResponse/numInResponse);
        System.out.println("Average length of words for not in-response: " + (double)numWordsNotResponse/(numNotResponse));
        System.out.println("Average length of chars for not in-response: " + (double)numCharsNotResponse/(numNotResponse));
        System.out.println("Standard deviation of words in-response: " + Math.sqrt((squaredInResponseWords - (numWordsResponse*numWordsResponse/numInResponse))/numInResponse));
        System.out.println("Standard deviation of chars in-response: " + Math.sqrt((squaredInResponseChars - (numCharsResponse*numCharsResponse/numInResponse))/numInResponse));
        System.out.println("Standard deviation of words not in-response: " + Math.sqrt((squaredNotResponseWords - (numWordsNotResponse*numWordsNotResponse/numNotResponse))/numNotResponse));
        System.out.println("Standard deviation of chars not in-response: " + Math.sqrt((squaredNotResponseChars - (numCharsNotResponse*numCharsNotResponse/numNotResponse))/numNotResponse));
        System.out.println();

        System.out.println("CONDITIONAL HISTOGRAMS");
        System.out.println("Number of public to all: " + numPublicAll);
        System.out.println("Number of public to self: " + numPublicSelf);
        System.out.println("Number of public to subscribers: " + numPublicSubscribers);
        System.out.println("Number of public to users: " + numPublicUser);
        System.out.println("Number of protected to all: " + numProtectedAll);
        System.out.println("Number of protected to self: " + numProtectedSelf);
        System.out.println("Number of protected to subscribers: " + numProtectedSubscribers);
        System.out.println("Number of protected to user: " + numProtectedUser);
        System.out.println("Number of private to all: " + numPrivateAll);
        System.out.println("Number of private to self: " + numPrivateSelf);
        System.out.println("Number of private to subscribers: " + numPrivateSubscribers);
        System.out.println("Number of private to user: " + numPrivateUser);
        System.out.println();

        System.out.println("Number of public in-response: " + numPublicResponse);
        System.out.println("Number of protected in-response: " + numProtectedResponse);
        System.out.println("Number of private in-response: " + numPrivateResponse);
        System.out.println("Number of public not in-response: " + (numPublic - numPublicResponse));
        System.out.println("Number of protected not in-response: " + (numProtected - numProtectedResponse));
        System.out.println("Number of private not in-response: " + (numPrivate - numPrivateResponse));
        System.out.println();

        System.out.println("Number of all in-response: " + numAllResponse);
        System.out.println("Number of self in-response: "  + numSelfResponse);
        System.out.println("Number of subscribers in-response " + numSubscribersResponse);
        System.out.println("Number of user in-response: " + numUserResponse);
        System.out.println("Number of all not in-response: " + (numToAll - numAllResponse));
        System.out.println("Number of self not in-response: "  + (numToSelf - numSelfResponse));
        System.out.println("Number of subscribers not in-response " + (numToSubscribers - numSubscribersResponse));
        System.out.println("Number of user not in-response: " + (numToUser - numUserResponse));

        //Write to output file if file name was provided
        if (fileWriter != null) {
            writer.print("[");
            JsonWriter jsonWriter = writerFactory.createWriter(fileWriter);
            JsonObjectBuilder wordHistogramBuilder = builderFactory.createObjectBuilder();
            JsonObject wordHistogram;

            for (int i = 2; i <= maxWords; i++) {
                wordHistogramBuilder.add((i + "-words"), numWords[i]);
            }
            wordHistogram = wordHistogramBuilder.build();

            JsonObject jsonObject = builderFactory.createObjectBuilder()

                    .add("basic stats:", builderFactory.createObjectBuilder()
                            .add("total messages", numMessages)
                            .add("unique users", numUniqueUsers)
                            .add("avg words", (double)totalNumWords/numMessages)
                            .add("avg chars", (double)totalNumChars/numMessages)
                            .add("std dev words", Math.sqrt((squaredTotalWords - (totalNumWords*totalNumWords/numMessages))/numMessages))
                            .add("std dev chars", Math.sqrt((squaredTotalChars - (totalNumChars*totalNumChars/numMessages))/numMessages)))
                    .add("message type histogram", builderFactory.createObjectBuilder()
                            .add("public messages", numPublic)
                            .add("protected messages", numProtected)
                            .add("private messages", numPrivate)
                            .add("to all", numToAll)
                            .add("to self", numToSelf)
                            .add("to subscribers", numToSubscribers)
                            .add("to users", numToUser)
                            .add("in-response", numInResponse)
                            .add("not in-response", numNotResponse)
                            .add("word histogram", wordHistogram))
                    .add("stats for subsets of messages", builderFactory.createObjectBuilder()
                            .add("avg words public", (double)numWordsPublic/numPublic)
                            .add("avg chars public", (double)numCharsPublic/numPublic)
                            .add("avg words protected", (double)numWordsProtected/numProtected)
                            .add("avg chars protected", (double)numCharsProtected/numProtected)
                            .add("avg words private", (double)numWordsPrivate/numPrivate)
                            .add("avg chars private", (double)numCharsPrivate/numPrivate)
                            .add("std dev public words", Math.sqrt((squaredPublicWords - (numWordsPublic*numWordsPublic/numPublic))/numPublic))
                            .add("std dev public chars", Math.sqrt((squaredPublicChars - (numCharsPublic*numCharsPublic/numPublic))/numPublic))
                            .add("std dev protected words", Math.sqrt((squaredProtectedWords - (numWordsProtected*numWordsProtected/numProtected))/numProtected))
                            .add("std dev protected chars", Math.sqrt((squaredProtectedChars - (numCharsProtected*numCharsProtected/numProtected))/numProtected))
                            .add("std dev private words", Math.sqrt((squaredPrivateWords - (numWordsPrivate*numWordsPrivate/numPrivate))/numPrivate))
                            .add("std dev private chars", Math.sqrt((squaredPrivateChars - (numCharsPrivate*numCharsPrivate/numPrivate))/numPrivate))
                            .add("avg words to all", (double)numWordsAll/numToAll)
                            .add("avg chars to all", (double)numCharsAll/numToAll)
                            .add("avg words to self", (double)numWordsSelf/numToSelf)
                            .add("avg chars to self", (double)numCharsSelf/numToSelf)
                            .add("avg words to subscribers", (double)numWordsSubscribers/numToSubscribers)
                            .add("avg chars to subscribers", (double)numCharsSubscribers/numToSubscribers)
                            .add("avg words to users", (double)numWordsUser/numToUser)
                            .add("avg chars to users", (double)numCharsUser/numToUser)
                            .add("std dev words to all", Math.sqrt((squaredToAllWords - (numWordsAll*numWordsAll/numToAll))/numToAll))
                            .add("std dev chars to all", Math.sqrt((squaredToAllChars - (numCharsAll*numCharsAll/numToAll))/numToAll))
                            .add("std dev words to self", Math.sqrt((squaredToSelfWords - (numWordsSelf*numWordsSelf/numToSelf))/numToSelf))
                            .add("std dev chars to self", Math.sqrt((squaredToSelfChars - (numCharsSelf*numCharsSelf/numToSelf))/numToSelf))
                            .add("std dev words to subscribers", Math.sqrt((squaredToSubscribersWords - (numWordsSubscribers*numWordsSubscribers/numToSubscribers))/numToSubscribers))
                            .add("std dev chars to subscribers", Math.sqrt((squaredToSubscribersChars - (numCharsSubscribers*numCharsSubscribers/numToSubscribers))/numToSubscribers))
                            .add("std dev words to users", Math.sqrt((squaredToUserWords - (numWordsUser*numWordsUser/numToUser))/numToUser))
                            .add("std dev chars to users", Math.sqrt((squaredToUserChars - (numCharsUser*numCharsUser/numToUser))/numToUser))
                            .add("avg words in-response", (double)numWordsResponse/numInResponse)
                            .add("avg chars in-response", (double)numCharsResponse/numInResponse)
                            .add("avg words not in-response", (double)numWordsNotResponse/(numNotResponse))
                            .add("avg chars not in-response", (double)numCharsNotResponse/(numNotResponse))
                            .add("std dev words in-response", Math.sqrt((squaredInResponseWords - (numWordsResponse*numWordsResponse/numInResponse))/numInResponse))
                            .add("std dev chars in-response", Math.sqrt((squaredInResponseChars - (numCharsResponse*numCharsResponse/numInResponse))/numInResponse))
                            .add("std dev words not in-response", Math.sqrt((squaredNotResponseWords - (numWordsNotResponse*numWordsNotResponse/numNotResponse))/numNotResponse))
                            .add("std dev chars not in-response", Math.sqrt((squaredNotResponseChars - (numCharsNotResponse*numCharsNotResponse/numNotResponse))/numNotResponse)))
                    .add("conditional histograms", builderFactory.createObjectBuilder()
                            .add("public to all", numPublicAll)
                            .add("public to self", numPublicSelf)
                            .add("public to subscribers", numPublicSubscribers)
                            .add("public to users", numPublicUser)
                            .add("protected to all", numProtectedAll)
                            .add("protected to self", numProtectedSelf)
                            .add("protected to subscribers", numProtectedSubscribers)
                            .add("protected to user", numProtectedUser)
                            .add("private to all", numPrivateAll)
                            .add("private to self", numPrivateSelf)
                            .add("private to subscribers", numPrivateSubscribers)
                            .add("private to user", numPrivateUser)
                            .add("public in-response", numPublicResponse)
                            .add("protected in-response", numProtectedResponse)
                            .add("private in-response", numPrivateResponse)
                            .add("public not in-response", (numPublic - numPublicResponse))
                            .add("protected not in-response", (numProtected - numProtectedResponse))
                            .add("private not in-response", (numPrivate - numPrivateResponse))
                            .add("all in-response", numAllResponse)
                            .add("self in-response", numSelfResponse)
                            .add("subscribers in-response",numSubscribersResponse)
                            .add("user in-response", numUserResponse)
                            .add("all not in-response", (numToAll - numAllResponse))
                            .add("self not in-response", (numToSelf - numSelfResponse))
                            .add("subscribers not in-response", (numToSubscribers - numSubscribersResponse))
                            .add("user not in-response", (numToUser - numUserResponse)))
                    .build();

            jsonWriter.write(jsonObject);
            writer.println("\n]");
            jsonWriter.close();
            writer.close();
        }
    }


    public static class Message {
        String status;
        String recepient;
        Boolean inResponse;
        int numWords;
        int numChars;

        public Message() {
            this.status = "";
            this.recepient = "";
            this.inResponse = false;
            this.numWords = 0;
            this.numChars = 0;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getNumWords() {
            return numWords;
        }

        public void setNumWords(int numWords) {
            this.numWords = numWords;
        }

        public int getNumChars() {
            return numChars;
        }

        public void setNumChars(int numChars) {
            this.numChars = numChars;
        }

        public String getRecepient() {
            return recepient;
        }

        public void setRecepient(String recepient) {
            this.recepient = recepient;
        }

        public Boolean getInResponse() {
            return inResponse;
        }

        public void setInResponse(Boolean inResponse) {
            this.inResponse = inResponse;
        }
    }
}
