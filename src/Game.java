import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.Scanner;

/**
 *
 * @author Janel Hernandez, Matt Smith, Angela Li
 *
 * This class is for a word guessing game similar to hangman.
 *
 */
public class Game  {

	int level;
	int failed_attempts;
	int f_a_counter;
	String line;
	String phrase;
	public String hidden;
	WordRepositoryServer wordRepo;
//	WordRepositoryServer wordRepo = new WordRepo();
	
	boolean isActive;
	private String clientName;
	private Timestamp registeredSince;
	static final int NUM_OF_BYTES = 240000; // Number of bytes in word.txt
	
	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	public Game(String player) {
		clientName = player;
		isActive = true;
		registeredSince = new Timestamp(System.currentTimeMillis());
	}

	public Game(int input_level, int input_failed_attempts) throws RemoteException {
		level = input_level;
		failed_attempts = input_failed_attempts;
		f_a_counter = level * failed_attempts;
		isActive = true;
		
		 wordRepo = new WordRepo();
//		try {
//			wordRepo = (WordRepositoryServer) Naming.lookup("WordRepo");
//		} catch (MalformedURLException | RemoteException | NotBoundException e1) {
//			e1.printStackTrace();
//			System.exit(0);
//		}


		phrase = wordRepo.getRandomWord(level);
		System.out.println(phrase);
		hidden = hide_phrase(phrase);
	}

	// hide words behind '-' and show failed attempt counter
	private String hide_phrase(String phrase) {

	    char[] letters = phrase.toCharArray();
	    StringBuilder hidden = new StringBuilder();

	    for (int i=0; i < letters.length; i++) {

	    	if(letters[i] == ' ') {

	             hidden.append(" ");

	        } else if(letters[i] != '\n') {
	        	hidden.append("-");
	        }
	        else {
	        	// set failed attempt counter
	            hidden.append("C" + f_a_counter + "\n");
	        }

	    }

	     return hidden.toString();

	}

	// update game counter string value sent to the client
	private String update_counter(String hidden) {

	    StringBuilder hidden_copy = new StringBuilder();

	    for(int i = 0; i < hidden.length(); i++) {

	        hidden_copy.append(hidden.charAt(i));

	    }

	    hidden_copy.replace(hidden.lastIndexOf("C"), hidden.lastIndexOf("\n"), "C" + Integer.toString(f_a_counter));

	    hidden = hidden_copy.toString();

	    return hidden;
	}

	// update game counter based on wrong guesses, reveals where correct character guess is, if counter == 0 lets client know they lost the round
	public void guess(char letter) {

		char[] letters = phrase.toCharArray();
		StringBuilder updated = new StringBuilder();

		if(!phrase.contains(String.valueOf(letter))) {
			f_a_counter--;

			hidden = update_counter(hidden);

			if(f_a_counter == 0) {

			    hidden = "Lost this round! :( score = 0 \n";
			}

		} else {

			// Goes through phrase
			for(int i = 0; i < hidden.length() ; i++) {

				if(i < phrase.length() && letters[i] == letter) {
					// Copy letter to hidden
					updated.append(letter);

				} else {
					updated.append(hidden.charAt(i));
				}
			}

			hidden = updated.toString();
		}

		return;
	}

	// if client guesses word correctly
	public void guess(String word) {

	    word = word + "\n";

	    if(!phrase.equals(word)) {
	        f_a_counter--;
	        hidden = update_counter(hidden);
	    }
	    else {
	        hidden = "You guessed it right! :) score = " + f_a_counter + "\n";
	    }
	}

	// looks through the word file and checks if word found
	public String word_lookup(String word) {

		try {

		Scanner scanner = new Scanner(new File("words.txt"));
		String currentLine;

		while(scanner.hasNextLine())
		{
			currentLine = scanner.nextLine();

		    if(currentLine.contains(word))
		    {
		         return "Word found!";
		    }
		}

		scanner.close();

		} catch (Exception e) {
			System.out.println("Exception!" + e);
		}

		return "Not found!";

	}

	public void setIsActive(boolean active) {
		// TODO Auto-generated method stub
		this.isActive = active;
		
	}
	// This method adds a word to the arrayList which is our wordRepo
	public void addWord(String word) throws RemoteException {
		wordRepo.createWord(word);

	}

	// This method removes a word from the arrayList which is our wordRepo
	public void removeWord(String word)throws RemoteException
	{
		wordRepo.removeWord(word);
	}

	// This method checks if a word is part of the arrayList which is our wordRepo
	public boolean checkWord(String word) throws RemoteException
	{
		return wordRepo.checkWord(word);
	}
	
	public synchronized Boolean getIsActive() throws RemoteException {
		return isActive;
	}

}
