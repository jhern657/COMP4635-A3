import java.rmi.*;
public interface PhraseGuessingGameServer extends Remote{
public String startGame(
		String player,
		int number_of_words,
		int failed_attempt_factor
) throws RemoteException, IllegalArgumentException;

public String guessLetter(String player,char letter) throws RemoteException, IllegalArgumentException;
public String guessPhrase(String player, String phrase) throws RemoteException, IllegalArgumentException;
public String endGame(String player) throws RemoteException;
public String restartGame(String player) throws RemoteException, IllegalArgumentException;
public String addWord(String clientName, String word) throws RemoteException;
public String removeWord(String clientName, String word) throws RemoteException;
public String checkWord(String clientName, String word) throws RemoteException;

public boolean checkUsername(String clientName) throws RemoteException;
public boolean heartBeat(String name) throws RemoteException;
public void keepMyNameWhileAlive(String name) throws RemoteException;
}