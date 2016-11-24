import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class AuctionServerImpl extends UnicastRemoteObject implements AuctionServerInterface {
	private static final String fileName = "systemstate.bin";
	private Integer mapNextID;
	private Map<Integer, AuctionObject> auctionMap;
	private Map<String, AuctionClientInterface> connectedClient;
	protected AuctionServerImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		connectedClient = new ConcurrentHashMap<String, AuctionClientInterface>();
		auctionMap = new ConcurrentHashMap<Integer, AuctionObject>();
		mapNextID = 0;
	}
	
	public Map<String, AuctionClientInterface> getConnectedMap(){
		return connectedClient;
	}
	
	@Override
	public int getAuctionID() throws RemoteException {
		// TODO Auto-generated method stub
		int mapID;
		synchronized (mapNextID){
			mapID = mapNextID;
			mapNextID++;
		}
		return mapID;
	}

	@Override
	public synchronized void createBidObject(AuctionClientInterface clientInt, String itemName, double bidValue, Timestamp endDate, String address, int port)
			throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("[" + new Timestamp(new Date().getTime()).toString() + "] " + clientInt.getUsername() + " has triggered createBidObject");
		int aucMapID = getAuctionID();
		AuctionObject aucObj = null;
		try {
			aucObj = new AuctionObject(clientInt, aucMapID, itemName, bidValue, endDate, address, port);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		auctionMap.put(aucMapID, aucObj);
		clientInt.notification("Successfully listed your item with item ID : " + aucMapID);
		try {
			saveState();
			System.out.println("[" + new Timestamp(new Date().getTime()).toString() + "] " + "Successfully save the system state");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean checkItemIDExist(int itemID) throws RemoteException {
		// TODO Auto-generated method stub
		if (!auctionMap.isEmpty()){
			if (auctionMap.containsKey(itemID)){
				if (new Timestamp(new Date().getTime()).after(auctionMap.get(itemID).getEndDate())){
					return false;
				}
				return true;
			}
		} 
		return false;
	}

	@Override
	public synchronized boolean bidItem(AuctionClientInterface clientInt, int itemID, double bidAmount) throws RemoteException{
		// TODO Auto-generated method stub
		System.out.println("[" + new Timestamp(new Date().getTime()).toString() + "] " + clientInt.getUsername() + " has triggered bidItem");
		AuctionObject aucObj = auctionMap.get(itemID);
		if (bidAmount < aucObj.getStartBid()){
			return false;
		} else {
			if (new Timestamp(new Date().getTime()).after(auctionMap.get(itemID).getEndDate())){
				return false;
			} else {
				aucObj.addBidder(clientInt, bidAmount);
				auctionMap.put(itemID, aucObj);
				try {
					saveState();
					System.out.println("[" + new Timestamp(new Date().getTime()).toString() + "] " + "Successfully save the system state");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}
		}
	}
	
	

	@Override
	public List<String> viewItem(AuctionClientInterface clientInt, String itemOwner) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("[" + new Timestamp(new Date().getTime()).toString() + "] " + clientInt.getUsername() + " has triggered viewItem");
		List<String> auctionList = new ArrayList<String>();
		Calendar calendar = new GregorianCalendar();
		for (Entry<Integer, AuctionObject> auctionEntry : auctionMap.entrySet()){
			int id = auctionEntry.getKey();
			AuctionObject auctionObj = auctionEntry.getValue();
			Timestamp x = new Timestamp(new Date().getTime());
			if (x.after(new Timestamp(auctionObj.getEndDate().getTime() + 60000))){
				auctionMap.remove(id);
				System.out.println("[" + new Timestamp(new Date().getTime()).toString() + "] " + "Item ID: "+ id + " is deleted from listing.");
				continue;
			} else {
				auctionList.add(auctionObj.returnDetails(clientInt));
			}
		}
		return auctionList;
	}

	@Override
	public synchronized void saveState() throws FileNotFoundException, IOException{
		// TODO Auto-generated method stub
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
		oos.writeObject(auctionMap);
		oos.writeObject(mapNextID);
		oos.close();
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void restoreState() throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileName)));
		try {
			auctionMap = (Map<Integer, AuctionObject>) ois.readObject();
			mapNextID = (Integer) ois.readObject();
			for (Entry<Integer, AuctionObject> auctionEntry : auctionMap.entrySet()){
				Timestamp currTime = new Timestamp(new Date().getTime());
				if (currTime.after(new Timestamp(auctionEntry.getValue().getEndDate().getTime() + 60000))){
					auctionEntry.getValue().setItemAvail(false);
					auctionMap.remove(auctionEntry.getKey());
					continue;
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("System unable to restore state");
		}
		System.out.println("Server has restore state successfully");
	}

	@Override
	public void ping() throws RemoteException {
		// TODO Auto-generated method stub
		// empty method to check if the server is still alive...
	}

	@Override
	public boolean checkUser(AuctionClientInterface clientInt) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("[" + new Timestamp(new Date().getTime()).toString() + "] " + clientInt.getUsername() + " has triggered checkUser");
		if (connectedClient.containsKey(clientInt.getUsername())){
			return false;
		} else {
			connectedClient.put(clientInt.getUsername(), clientInt);
			System.out.println("[" + new Timestamp(new Date().getTime()).toString() + "] " + clientInt.getUsername() + " is now connected");
			return true;
		}
	}

	@Override
	public void checkClient() throws Exception{
		// TODO Auto-generated method stub
		(new Timer(true)).schedule(new TimerTask() {
			public void run (){
				if (!connectedClient.isEmpty()){
					for (Entry<String, AuctionClientInterface> entrySet : connectedClient.entrySet()){
						try {
							entrySet.getValue().ping();
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							connectedClient.remove(entrySet.getKey());
							System.out.println("[" + new Timestamp(new Date().getTime()).toString() + "] " + entrySet.getKey() + " is now disconnected");
						}
					}
				}
			}
		},0, 1000);
	}

	@Override
	public void promptErr(String str) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("[" + new Timestamp(new Date().getTime()).toString() + "] " + str);
	}

	@Override
	public boolean checkItemOwner(AuctionClientInterface clientInt, int itemID) throws RemoteException {
		// TODO Auto-generated method stub
		if (auctionMap.get(itemID).getOwnerName().equals(clientInt.getUsername())){
			return true;
		} else {
			return false;
		}
	}
}
