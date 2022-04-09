import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameInterface extends Remote {
	public String startGame(
			String player,
			int number_of_words,
			int failed_attempt_factor
	) throws RemoteException, IllegalArgumentException;

}
