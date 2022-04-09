import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Server {
	private static final int TIMELIMIT_SECONDS = 10;
	private GameServer gameServer;

	public Server(String gameName) {
		try {

			//Register object with RMI
			try {
				LocateRegistry.getRegistry(1099).list();

			} catch(RemoteException e) {
				LocateRegistry.createRegistry(1099);
			}

			 gameServer = new GameServer(gameName);

			// Create string URL
			String rmiObjectName = "rmi://localhost/" + gameName;
			Naming.rebind(gameName, gameServer);
			System.out.println(gameServer + " is ready!");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) throws RemoteException, InterruptedException {
		if (args.length > 1 || (args.length > 0 && args[0].equalsIgnoreCase("-h"))) {

			System.exit(1);
		}

		String clientname;
		if (args.length > 0) {
			clientname = args[0];
		} else {
			clientname = "generic";
		}

		Server thisServer = new Server(clientname);

		while (true) {

			TimeUnit.SECONDS.sleep(TIMELIMIT_SECONDS);
			System.out.print("Prunning the HashMap...");
			@SuppressWarnings("unchecked")
			Iterator<Map.Entry<String, Game>> it =  thisServer.gameServer.getEntrySet();
			if (it == null || it.hasNext() == false) {
				System.out.println(" nothing to do yet!");
				continue;
			}
			int cntAlive = 0, cntDead = 0;
			ArrayList<String> removeList = new ArrayList<>();
			while (it.hasNext()) {
				Map.Entry<String, Game> pair = it.next();
				String name = pair.getKey();
				Game r = pair.getValue();
				if (r != null && r.getIsActive()) {
					r.setIsActive(false);
					cntAlive++;
				}
				else {
					removeList.add(name);
					cntDead++;
				}
			}
			for (String name:removeList)
				thisServer.gameServer.removeEntry(name);
			System.out.println("Removed " + cntDead + ", " + cntAlive + " still alive!");
		}
	}

}