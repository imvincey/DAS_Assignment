import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Client {
	static Constant constant = new Constant ();
	public static void main (String[] args) throws FileNotFoundException, IOException, Exception {
		String address = constant.getHost();
		int port = constant.getPort();
		if (args.length == 2){
			if (constant.checkInteger(args[1]) && (constant.validateIPAddress(args[0]) || args[0].equalsIgnoreCase("localhost"))){
				address = args[0];
				port = Integer.parseInt(args[1]);
			} else {
				System.out.println("Unable to detect input properly. Systemw will run with default and address and port.");
			}
		}
		
		System.out.println("Welcome to Auction System. \nBefore you begin, please enter your username: ");
		Scanner sc = new Scanner(System.in);
		String username = sc.nextLine();
		AuctionServerInterface serverInt = null;
		AuctionClientInterface clientInt = null;
		try {
			clientInt = new AuctionClientImpl(username);
			serverInt = (AuctionServerInterface) Naming.lookup("rmi://" + address + ":" + port + "/AuctionSystem");
			while (!serverInt.checkUser(clientInt)){
				System.out.println(username + "has already logined. Please try different username");
				username = sc.nextLine();
				clientInt = new AuctionClientImpl(username);
			}
			System.out.println("Hello " + username);
			clientInt.checkClient(address, port);
			startProcedure(serverInt, clientInt, address, port);
			
		} catch (MalformedURLException | RemoteException | NotBoundException | ArithmeticException exception){
			System.out.println("Server can't be reached. Please try again later.");
		}
		
	}
	
	public static void startProcedure(AuctionServerInterface serverInt, AuctionClientInterface clientInt, String address, int port) throws FileNotFoundException, IOException{
		while (true){
			System.out.println(constant.getProgramInterface());
			Scanner sc = new Scanner(System.in);
			while (!sc.hasNextInt()){
				System.out.println("Command not recognised. Please choose a valid option");
				sc.nextLine();
			}
			int option = sc.nextInt();
			switch (option) {
				case 1: createBidItem(serverInt, clientInt, address, port);
					break;
				case 2: bidItem(serverInt, clientInt);
					break;
				case 3: viewAllItem(serverInt, clientInt);
					break;
				case 4: System.exit(0);
					break;
				default: System.out.println("Could not recognise input. Please try again");
					break;
			}
		}
	}
	
	public static void createBidItem(AuctionServerInterface serverInt, AuctionClientInterface clientInt, String address, int port) throws RemoteException{
		Scanner sc = new Scanner (System.in);
		System.out.println("Item name : ");
		String itemName = sc.nextLine();
		System.out.println("Minimum bid value : ");
		while (!sc.hasNextDouble()){
			System.out.println("Unable to read the bid value. Please try again");
			System.out.println("Minimum bid value : ");
			sc = new Scanner(System.in);
		}
		double bidValue = sc.nextDouble();
		if (bidValue <= 0){
			System.out.println("Starting bid cannot be 0 or less. Bid is rounded up to the nearest \"1\" digit");
			bidValue = 1;
		}
		System.out.println("Duration for auction (in seconds) : ");
		while (!sc.hasNextInt()){
			System.out.println("Unable to read duration. Please try again");
			System.out.println("Duration for auction (in seconds) : ");
			sc = new Scanner(System.in);
		}
		int duration = sc.nextInt();
		if (duration < 1) {
			System.out.println("Closing duration cannot be 0 or negative. System has round up to the nearest minute.");
			duration = 60;
		}
		int timer = duration * 1000;
		Date date = new Date();
		Timestamp endTime = new Timestamp(date.getTime() + timer);
		serverInt.createBidObject(clientInt, itemName, bidValue, endTime, address, port);
	}
	
	public static void bidItem(AuctionServerInterface serverInt, AuctionClientInterface clientInt) throws RemoteException{
		Scanner sc = new Scanner (System.in);
		System.out.println("Please enter the Bid ID of the item: ");
		while (!sc.hasNextInt()){
			System.out.println("Unable to recognise your input. Please try again.");
			sc = new Scanner(System.in);
		}
		int itemID = Integer.parseInt(sc.nextLine().trim());
		if (serverInt.checkItemIDExist(itemID)){
			if (serverInt.checkItemOwner(clientInt, itemID)){
				System.out.println("You cannot bid your own item.");
			} else {
				System.out.println("Please enter your bid amount : ");
				while (!sc.hasNextDouble()){
					System.out.println("Unable to recognise your input. Please try again.");
					sc = new Scanner(System.in);
				}
				double bidAmount = sc.nextDouble();
				if (!serverInt.bidItem(clientInt, itemID, bidAmount)){
					System.out.println("Bid amount is lower than the start bid or bid has already ended. Please try again.");
				}
			}
		} else {
			System.out.println("Either item validity has expired or item does not exist. Please try again.");
		}
	}
	
	public static void viewAllItem(AuctionServerInterface serverInt, AuctionClientInterface clientInt) throws RemoteException{
		String itemOwner = clientInt.getUsername();
		List<String> viewList = serverInt.viewItem(clientInt, itemOwner);
		for (int i = 0; i < viewList.size(); i ++){
			System.out.println(viewList.get(i));
		}
	}
}
