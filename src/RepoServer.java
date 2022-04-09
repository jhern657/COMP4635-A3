import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RepoServer {
	
	public RepoServer(String repoName) {
		try {
			
			//Register object with RMI
			try {
				LocateRegistry.getRegistry(1099).list();
				
			} catch(RemoteException e) {
				LocateRegistry.createRegistry(1099);
			}
			
			WordRepositoryServer repoServer = new WordRepo(); 
			
			// Create string URL
			String rmiObjectName = "rmi://localhost/" + repoName;
			Naming.rebind(rmiObjectName, repoServer);
			System.out.println(repoServer + " is ready!");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] argv) {
		String repoName = "WordRepo";
		
		new RepoServer(repoName);
	}

}
