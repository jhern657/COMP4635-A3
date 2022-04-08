import java.rmi.Remote;
import java.rmi.RemoteException;
public interface WordRepositoryServer extends Remote{
public boolean createWord(String word) throws RemoteException;
public boolean removeWord(String word) throws RemoteException;
public boolean checkWord(String word) throws RemoteException;
public String getRandomWord(int length) throws RemoteException;
}
