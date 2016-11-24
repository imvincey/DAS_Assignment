import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

public class AuctionClientImpl extends UnicastRemoteObject implements AuctionClientInterface {
	String username;
	public AuctionClientImpl(String username) throws RemoteException {
		// TODO Auto-generated constructor stub
		super();
		this.username = username;
	}
	@Override
	public void notification(String notification) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("Notification : " + notification);
	}
	@Override
	public String getUsername() throws RemoteException {
		// TODO Auto-generated method stub
		return this.username;
	}
	@Override
	public void ping() throws RemoteException {
		// TODO Auto-generated method stub
		// ping client do nothing
	}
	@Override
	public void checkClient(String address, int port) throws Exception {
		// TODO Auto-generated method stub
		(new Timer(true)).schedule(new TimerTask() {
			public void run (){
				try {
					AuctionServerInterface serverIntPing = (AuctionServerInterface)Naming.lookup("rmi://" + address + ":" + port + "/AuctionSystem");
					serverIntPing.ping();
				} catch (Exception e){
					System.out.println("Unable to reach server ... Server might be downed. Please try again later..");
					try {
						System.out.println("Client is now exiting...");
						Thread.sleep(2000);
					} catch (InterruptedException ie){
						ie.printStackTrace();
					}
					System.exit(0);
				}
			}
		},0, 1000);
	}
	
	

}
