import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Date;

public class AuctionTest {
	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException{
//		int x = Integer.parseInt(args[0]);
		String address = "localhost"; int port = 0;
		AuctionClientInterface aci = new AuctionClientImpl(args[0]);
		AuctionServerInterface asi = (AuctionServerInterface) Naming.lookup("rmi://" + address + ":" + port + "/AuctionSystem");
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 10000; i ++){
			asi.createBidObject(aci, "TESTING", 1, new Timestamp(new Date().getTime()), address, port);
		}
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		double portion = Double.parseDouble(Long.toString(totalTime)) / Double.parseDouble("10000");
		System.out.println("Time taken : " + totalTime + " ms || time taken / command : " + portion + " ms/call");
	}
}
