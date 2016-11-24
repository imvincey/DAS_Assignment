import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface AuctionServerInterface extends Remote {
	public int getAuctionID () throws RemoteException;
	
	public void createBidObject(AuctionClientInterface clientInt, String itemName, double bidValue, Timestamp endDate, String address, int port) throws RemoteException;

	public boolean checkItemIDExist(int itemID) throws RemoteException;

	public boolean bidItem(AuctionClientInterface clientInt, int itemID, double bidAmount) throws RemoteException;

	public List<String> viewItem(AuctionClientInterface clientInt, String itemOwner) throws RemoteException;

	public void saveState() throws FileNotFoundException, IOException, RemoteException;

	public void restoreState() throws FileNotFoundException, IOException, RemoteException;
	
	public void ping () throws RemoteException;

	public boolean checkUser(AuctionClientInterface clientInt) throws RemoteException;

	public void checkClient() throws Exception;
	
	public Map<String, AuctionClientInterface> getConnectedMap() throws RemoteException;
	
	public void promptErr(String str) throws RemoteException;

	public boolean checkItemOwner(AuctionClientInterface clientInt, int itemID) throws RemoteException;
}
