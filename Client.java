import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

public class Client implements Runnable{
	private static final String DEFAULT_CLIENT_NAME = "generic";
	
	private static final int TIMELIMIT_SECONDS = 5;
	
	
	
    String clientName;
    PhraseGuessingGameServer gameServer;

    static BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));

    // Set of valid commands a client can enter
    static enum CommandName
    {
        startGame, guessLetter, guessPhrase, endGame, restartGame, addWord, removeWord, checkWord;
    };
    public Client(String clientName)
    {
        this.clientName = clientName;

        try
        {
            // Connect to rmi registry
            gameServer = (PhraseGuessingGameServer) Naming.lookup(clientName);
            gameServer.keepMyNameWhileAlive(clientName);

            // Check if username is available
//            while(gameServer.checkUsername(this.clientName)) {
//            	System.out.println("Username unavailable, please use a different one.");
//            	this.clientName = consoleIn.readLine();
//            }
            System.out.println(this.clientName);
        } catch (Exception e)
        {
            System.out.println("Runtime failure: " + e.getMessage());
            System.exit(0);
        }

        System.out.println("Connected to PhraseGuesser, welcome " + this.clientName);
        System.out.println("Commands: startGame, guessLetter, guessPhrase, endGame, restartGame, addWord, removeWord, checkWord");
        System.out.println("To start game enter: startGame level failedAttempts");
    }
//    public Client() {
//		this(DEFAULT_CLIENT_NAME);
//	}
	public static void main(String[] args) throws RemoteException {
		if ((args.length > 1) || (args.length > 0 && args[0].equals("-h"))) {
//			System.out.println(USAGE);
			System.exit(1);
		}

		String clientName;
		Client thisClient;

		if (args.length > 0) {
			clientName = args[0];
			
			// Create a thread to send heart-beats to the server
			
		 thisClient = new Client(clientName);
		
			(new Thread(thisClient)).start();
//			new Client(userName).run();
		} else {
			 thisClient = new Client(args[0]);
			(new Thread(thisClient)).start();

		}
		while (true) {

			System.out.print(thisClient.clientName + "@" + thisClient.clientName + ">");
			try {
				BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));

				String userInput = consoleIn.readLine();
				thisClient.execute(thisClient.parse(userInput));
				
		
////					try {
//						TimeUnit.SECONDS.sleep(TIMELIMIT_SECONDS);
//						pggs.heartBeat(userName);
////					} catch (Exception e) {
//						e.printStackTrace();
//						break;
//					}
//				
				
			} catch (Exception re) {
				System.out.println(re);
			}
		}
	}

    // Run the game
    public void run()
    {
        // Main game loop
        while (true)
        {
            try
            {
                // Parse input for command parameters
                // Execute command
//                String userInput = consoleIn.readLine();
//                execute(parse(userInput));
            	TimeUnit.SECONDS.sleep(TIMELIMIT_SECONDS);
            	gameServer.heartBeat(clientName);
            	
           
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }
    }

 

    // executes command based on the valid commands a user can enter and calls the
    // appropriate game server method.
    void execute(Command command) throws RemoteException, RejectedException
    {
        if (command == null)
        {
            System.out.println("Invalid command, please try again.");
            return;
        }

        switch (command.getCommandName())
        {
            case startGame:
                try
                {
                    int numWords = command.getNumWords();
                    int failedAttempts = command.getFailedAttempts();
                    String clientName = command.getUserName();
                    System.out.println(gameServer.startGame(clientName.toString(), numWords, failedAttempts));
                } catch (Exception e)
                {
                    System.out.println("Invalid Command, try again.");
                    e.printStackTrace();
                    return;
                }
                break;
            case guessLetter:
                try
                {
                    String clientName = command.getUserName();

                    String letter = command.getLetter();
                    System.out.println(gameServer.guessLetter(clientName, letter.charAt(0)));
                } catch (Exception e)
                {
                    System.out.println("Invalid Command, try again.");
                    e.printStackTrace();
                    return;
                }
                break;
            case guessPhrase:
                try
                {
                    String clientName = command.getUserName();

                    String phrase = command.getPhrase();
                    System.out.println(gameServer.guessPhrase(clientName, phrase));
                } catch (Exception e)
                {
                    System.out.println("Invalid Command, try again.");
                    e.printStackTrace();
                    return;
                }
                break;
            case restartGame:
                try
                {
                    String clientName = command.getUserName();

                    System.out.println(gameServer.restartGame(clientName));
                } catch (Exception e)
                {
                    System.out.println("Invalid Command, try again.");
                    e.printStackTrace();
                    return;
                }
                break;
            case addWord:
                try
                {
                    String clientName = command.getUserName();

                    System.out.println(gameServer.addWord(clientName, command.getPhrase()));
                } catch (Exception e)
                {
                    System.out.println("Invalid Command, try again.");
                    e.printStackTrace();
                    return;
                }
                break;
            case removeWord:
                try
                
                {
                    String clientName = command.getUserName();

                    System.out.println(gameServer.removeWord(clientName, command.getPhrase()));
                } catch (Exception e)
                {
                    System.out.println("Invalid Command, try again.");
                    e.printStackTrace();
                    return;
                }
                break;
            case checkWord:
                try
                {
                    String clientName = command.getUserName();

                    System.out.println(gameServer.checkWord(clientName, command.getPhrase()));
                } catch (Exception e)
                {
                    System.out.println("Invalid Command, try again.");
                    e.printStackTrace();
                    return;
                }
                break;
            case endGame:
                String clientName = command.getUserName();

                System.out.println(gameServer.endGame(clientName));
                System.exit(0);
                break;
            default:
                try {
                System.out.println("Invalid Command, try again.");
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                break;


        }

    }// execute closure

    private class Command
    {
        private String phrase;
        private String letter;
        private int numWords;
        private int failedAttempts;
        private String userName;
        private CommandName commandName;

        private Command(Client.CommandName commandName,  String userName, String letter, String phrase, int numWords, int failedAttempts)
        {
            this.commandName = commandName;
            this.letter = letter;
            this.phrase = phrase;
            this.numWords = numWords;
            this.failedAttempts = failedAttempts;
            this.userName = userName;
        }

        private CommandName getCommandName()
        {
            return commandName;
        }

		public String getUserName() {
			return userName;
		}
        private String getLetter()
        {
            return letter;
        }

        private String getPhrase()
        {
            return phrase;
        }

        private int getNumWords()
        {
            return numWords;
        }

        private int getFailedAttempts()
        {
            return failedAttempts;
        }

    }

    // parses the command entered by the user
    private Command parse(String userInput)
    {
        if (userInput == null)
        {
            return null;
        }

        StringTokenizer tokenizer = new StringTokenizer(userInput);
        if (tokenizer.countTokens() == 0)
        {
            return null;
        }

        CommandName commandName = null;
        String letter = null;
        String phrase = "";
        int numWords = 0;
        String userName = "";
        int failedAttempts = 0;
        String commandNameString;
        String[] tempPhrase;
        StringBuilder temp2 = new StringBuilder();


        while (tokenizer.hasMoreTokens())
        {
            switch (userInput.split(" ")[0])
            {
                case "startGame":
                    try
                    {
                        commandNameString = tokenizer.nextToken();
                        userName = tokenizer.nextToken();
                        numWords = Integer.parseInt(tokenizer.nextToken());
                        failedAttempts = Integer.parseInt(tokenizer.nextToken());
                        commandName = CommandName.valueOf(CommandName.class, commandNameString);

                    } catch (IllegalArgumentException commandDoesNotExist)
                    {
                        System.out.println("Illegal command");
                        return null;
                    }
                    catch(NoSuchElementException e) {
                        return null;
                    }
                    break;
                case "guessLetter":
                    try {
                    commandNameString = tokenizer.nextToken();
                    userName = tokenizer.nextToken();

                    letter = tokenizer.nextToken();
                    commandName = CommandName.valueOf(CommandName.class, commandNameString);
                    }
                    catch(Exception e) {
                        return null;
                    }
                    break;
                case "guessPhrase":
                    try
                    {

                        commandNameString = tokenizer.nextToken();
                        userName = tokenizer.nextToken();

                        tempPhrase = userInput.split(" ");
                        int i;

                        for(i =1; i < tempPhrase.length-1; i++) {
                            temp2.append(tempPhrase[i] + " ");
                        }

                        temp2.append(tempPhrase[i]);
                        phrase = temp2.toString();

                        commandName = CommandName.valueOf(CommandName.class, commandNameString);

                        while(tokenizer.hasMoreTokens()) {
                            tokenizer.nextToken();
                        }

                    } catch (NumberFormatException e)
                    {
                        System.out.println("Illegal guess");
                        return null;
                    }
                    catch(Exception e) {
                        return null;
                    }
                    break;
                case "addWord":
                case "removeWord":
                case "checkWord":
                    try {
                    commandNameString = tokenizer.nextToken();
                    userName = tokenizer.nextToken();

                    phrase = tokenizer.nextToken();
                    commandName = CommandName.valueOf(CommandName.class, commandNameString);
                    }
                    catch(Exception e)
                    {
                        return null;
                    }
                    break;
                default:
                    try {
                    commandNameString = tokenizer.nextToken();
                    commandName = CommandName.valueOf(CommandName.class, commandNameString);
                    break;
                    }
                    catch(IllegalArgumentException commandDoesNotExist)
                    {
                        return null;
                    }

            }

        }

        return new Command(commandName, userName, letter, phrase, numWords, failedAttempts);
    }

}
