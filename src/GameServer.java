import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("serial")
public class GameServer extends UnicastRemoteObject implements PhraseGuessingGameServer {
	// gamesStates (player, game status)
	private Map<String,Game> gameStates = new HashMap<>();
	//WordRepositoryServer wordRepo = new WordRepo();
	String gameName;

	public GameServer(String gameName) throws RemoteException {
		super();
		this.gameName = gameName;
		//wordRepo = new WordRepo();

		// Connect to rmi registry
//	    try {
////			wordRepo = (WordRepositoryServer) Naming.lookup("WordRepo");
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//			System.exit(0);
//		} catch (RemoteException e) {
//			e.printStackTrace();
//			System.exit(0);
//		} catch (NotBoundException e) {
//			e.printStackTrace();
//			System.exit(0);
//		}
	}

	@Override
    public boolean checkUsername(String username) throws RemoteException{
		boolean isTaken = gameStates.containsKey(username);

		return isTaken;
	}


	// starts a new game based on level, and failed attempt factor.
	@Override
	public synchronized String startGame(String player, int number_of_words, int failed_attempt_factor) throws RemoteException, IllegalArgumentException {
		String phrase = null;

		try {
		if(number_of_words > 0 && failed_attempt_factor > 0) {
			Game game = new Game(number_of_words, failed_attempt_factor);
			gameStates.put(player, game);
			phrase = game.hidden;
		} else {
			throw new IllegalArgumentException("Cannot start game with level: " + number_of_words + " and failed attempts: " + failed_attempt_factor);
		}
		}
		catch(Exception e) {
		    System.out.println("Cannot find player");
		}


		return phrase;
	}


	// based on entered character, this will check the game class if letter exist within the phrase.
	@Override
	public synchronized String guessLetter(String player, char letter) throws RemoteException, IllegalArgumentException {
		Game game = gameStates.get(player);
		String phrase = null;

		if(game != null) {
			game.guess(letter);
			phrase = game.hidden;
		} else {

			throw new IllegalArgumentException("Could not find player: " + player + ", guess failed");
		}

		return phrase;
	}
	public Iterator<Map.Entry<String, Game>> getEntrySet() {
		return this.gameStates.entrySet().iterator();
	}

	//based on entered string, this will check if the string was guessed correctly else it will decrement the failed attempt factor
	@Override
	public synchronized String guessPhrase(String player, String phrase) throws RemoteException, IllegalArgumentException {
		Game game = gameStates.get(player);
		String updatedPhrase = null;

		if(game != null) {
			game.guess(phrase);
			updatedPhrase = game.hidden;
		} else {

			throw new IllegalArgumentException("Could not find player" + player + ", guess failed");
		}

		return updatedPhrase;
	}

	// ends game for a specific player
	@Override
	public synchronized String endGame(String player) throws RemoteException {
		gameStates.put(player, null);

		return "Game Ended, Goodbye " + player + "!";
	}

	//restarts game with the same level and failed attempt factor
	@Override
	public synchronized String restartGame(String player) throws RemoteException, IllegalArgumentException {
		Game game = gameStates.get(player);
		String status = null;

		if (game != null) {
			// Create a new game with the same level and failed attempts
			int failed = game.f_a_counter/game.level;
			Game newGame = new Game(game.level, failed);
			gameStates.put(player, newGame);
			status = newGame.hidden;
		} else {
			throw new IllegalArgumentException("Could not find " + player + ", restart failed");
		}

		return status;
	}

	public synchronized void setGameStates(HashMap<String, Game> gameStates) {
		this.gameStates = gameStates;
	}

	//Adds word in the word repository if it doesn't exist yet. Uses the createWord method inside WordRepositoryServer, tells the player if their word was successfully added to the word repository.
	@Override
	public String addWord(String clientName, String word) throws RemoteException {
//		String status = null;
//
//		if(wordRepo.createWord(word)) {
//			status = "Successfully added " + word;
//		} else {
//			status = "Failed to add " + word;
//		}
//		return status;
	    System.out.println(clientName);
		gameStates.get(clientName).addWord(word);
		return word + " added succesfully!";

	}

	@Override
    public synchronized boolean heartBeat(String name) throws RemoteException {
		Game r = gameStates.get(name);
		if (r != null) {
			r.setIsActive(true);
		}
		return false;
	}

	@Override
    public synchronized void keepMyNameWhileAlive(String name) throws RemoteException {
		// If the record for this client does not exist, create and add record
		if (!gameStates.containsKey(name)) {
			gameStates.put(name, new Game(name));
		}
	}


	//Checks if word is in the Word Repository using the removeWord method inside the WordRepositotyServer, tells player if their word was successfully removed from the word repository
	@Override
	public String removeWord(String clientName, String word) throws RemoteException {
//		String status = null;
//
//		if(wordRepo.removeWord(word)) {
//			status = "Successfully removed " + word;
//		} else {
//			status = "Failed to remove " + word;
//		}
//		return status;
		System.out.println(clientName);

		gameStates.get(clientName).removeWord(word);
		return word + " removed successfully!";
	}

	//Checks if word is in the word repository using the checkWord method inside the WordRepositoryServer, this tells the user if the word is found.
	@Override
	public String checkWord(String clientName, String word) throws RemoteException {
//		String status = null;
//
//		if(wordRepo.checkWord(word)) {
//			status = "Found " + word;
//		} else {
//			status = "Could not find " + word;
//		}
//		return status;
		System.out.println(clientName);
		

		boolean found = gameStates.get(clientName).checkWord(word);
		if (found) {
			return word + " was found!";
		}
		else {
			return word + " was not found!";
		}
	}

	public synchronized void removeEntry(String name) {
		gameStates.remove(name);

	}


}