import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuctionClientInterface extends Remote {
	public void notification (String notification) throws RemoteException;
	public String getUsername() throws RemoteException;
	public void ping() throws RemoteException;
	public void checkClient(String address, int port) throws Exception;
}
