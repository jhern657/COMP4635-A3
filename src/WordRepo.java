import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

@SuppressWarnings("serial")
public class WordRepo extends UnicastRemoteObject implements WordRepositoryServer {
	private ArrayList<String> words;

	public WordRepo() throws RemoteException {
		super();
		words = new ArrayList<>();

		try {
			Scanner scanner = new Scanner(new File("words.txt"));

			while (scanner.hasNextLine()) {
				words.add(scanner.nextLine()); // adds words into an array list so CRUD operations can be done
			}
		} catch(FileNotFoundException e) {
			System.out.println("Could not find: words.txt");
			e.printStackTrace();
		}
	}

	//creates word if not already in the word file
	@Override
	public synchronized boolean createWord(String word) throws RemoteException {
		boolean success = false;
		if(!checkWord(word)) {
			success = words.add(word);
		}

		return success;
	}

	//removes word if it's in the word file
	@Override
	public synchronized boolean removeWord(String word) throws RemoteException {
		return words.remove(word);
	}

	// checks if word entered is in the word file
	@Override
	public boolean checkWord(String word) throws RemoteException {
		return words.contains(word);
	}

	//asks for i(level) random words from the word repository,
    // and concatenates them to create the phrase for a game round
	@Override
	public String getRandomWord(int length) throws RemoteException {

	    Random random = new Random();
        int randomNum = random.nextInt(words.size());
        StringBuilder phrase = new StringBuilder();

        for(int i = 0; i < length; i++) {
            // Grab one word and append it to the phrase
            try {
                // Move to a random spot
                randomNum = random.nextInt(words.size());

                // Append word to phrase
                phrase.append(words.get(randomNum));

                // Add space if needed
                if(i < length - 1)
                    phrase.append(" ");
            } catch(Exception e) {
                System.out.println("Failed to seek word =(");
                e.printStackTrace();
            }
        }

        return phrase.append("\n").toString();

	}

}