import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

public class AuctionObject implements Serializable {
	private AuctionServerInterface serverInt;
	private int itemID;
	private String itemName;
	private double startBid;
	private Timestamp endDate;
	private Map<String, AuctionClientInterface> bidderMap;
	private String topBidName = null;
	private double topBidValue = 0;
	private boolean itemAvail;
	private String ownerName;
	private String address;
	private int port;
	
	public AuctionObject (AuctionClientInterface clientInt, int itemID, String itemName, double value, Timestamp endDate, String address, int port) throws Exception{
		super();
		this.address = address;
		this.port = port;
		this.itemID = itemID;
		this.itemName = itemName;
		startBid = value;
		this.endDate = endDate;
		ownerName = clientInt.getUsername();
		bidderMap = new HashMap<String, AuctionClientInterface>();
		callback();
	}
	
	public String getOwnerName(){
		return ownerName;
	}

	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public double getStartBid() {
		return startBid;
	}

	public void setStartBid(double startBid) {
		this.startBid = startBid;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public Map<String, AuctionClientInterface> getBidderMap() {
		return bidderMap;
	}

	public void setBidderMap(Map<String, AuctionClientInterface> bidderMap) {
		this.bidderMap = bidderMap;
	}

	public String getTopBidName() {
		return topBidName;
	}

	public void setTopBidName(String topBidName) {
		this.topBidName = topBidName;
	}

	public double getTopBidValue() {
		return topBidValue;
	}

	public void setTopBidValue(double topBidValue) {
		this.topBidValue = topBidValue;
	}

	public boolean isItemAvail() {
		return itemAvail;
	}

	public void setItemAvail(boolean itemAvail) {
		this.itemAvail = itemAvail;
	}
	
	public void callback() {
		this.setItemAvail(true);
		(new Timer(true)).schedule(new TimerTask() {
			public void run (){
				AuctionObject.this.closing();
			}
		}, getEndDate());
	}
	
	public String returnDetails (AuctionClientInterface client) throws RemoteException {
		String details = "Owner: " +  ownerName + 
				"\nID: " + getItemID() + 
				"\nItem Name: " + itemName + 
				"\nStarting bid: " + startBid + 
				"\nNo of Bidders: " + bidderMap.size() + 
				"\nClosing Date: " + getEndDate().toString() + 
				"\nAuction Status: ";
		if (isItemAvail()){
			details += "Available \n";
		} else {
			if (topBidName == null){
				details += "Not Available \n" +
						"There are no bidders for this auction.\n";
			} else {
				details += "Not Available \n" +
						"Bidder Name:  " + topBidName + 
						"\nBids: " + topBidValue + "\n";
			}
		}
		return details;
	}

	public void addBidder(AuctionClientInterface clientInt, double bidAmount) throws RemoteException {
		// TODO Auto-generated method stub
		bidderMap.put(clientInt.getUsername(), clientInt);
		System.out.println(bidAmount);
		System.out.println(topBidValue);
		System.out.println(clientInt.getUsername());
		if (topBidValue < bidAmount){
			topBidName = clientInt.getUsername();
			topBidValue = bidAmount;
		}
		clientInt.notification("Bid Accepted");
	}
	
	public void closing() {
		setItemAvail(false);
		System.out.println("End auction of " + itemName + " with ID: " + itemID);
		try{
			try {
				serverInt = (AuctionServerInterface)Naming.lookup("rmi://" + address + ":" + port + "/AuctionSystem");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Map<String, AuctionClientInterface> connectedClient = serverInt.getConnectedMap();
			if (connectedClient.containsKey(ownerName)){
				connectedClient.get(ownerName).notification("Auction of " + itemName + " (ItemID: " + itemID + ") has ended. ");
				if (topBidName != null){
					connectedClient.get(ownerName).notification("Your item " + itemName + " (ItemID: " + itemID + ") has been bought by " + topBidName + " @ $" + topBidValue);
				} else {
					connectedClient.get(ownerName).notification("No one has bid for the item " + itemName + " (Item ID: " + itemID + ")");
				}
			}
			if (topBidName != null){
				for (Entry <String, AuctionClientInterface> bidderEntry : bidderMap.entrySet()){
					
					if (connectedClient.containsKey(bidderEntry.getKey())){
						if (bidderEntry.getKey().equals(topBidName)){
							connectedClient.get(bidderEntry.getKey()).notification("You have won the auction of the "+ itemName +" item ID : " + itemID);
						} else {
							connectedClient.get(bidderEntry.getKey()).notification("Winner of " + itemName +" (itemID: " + itemID + ") is " + topBidName + " with the price of " + topBidValue);
						}
					}
				}
			}
		} catch (RemoteException re){
			re.printStackTrace();
			System.out.println("RMI ERROR");
		}
	}
}
