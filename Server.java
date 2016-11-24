import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Naming;
import java.sql.Timestamp;
import java.util.Date;

public class Server {
	public static void main (String[] args) throws FileNotFoundException, IOException{
		Constant constant = new Constant();
		String address = constant.getHost();
		int port = constant.getPort();
		if (args.length == 2){
			if (constant.checkInteger(args[1]) && (constant.validateIPAddress(args[0]) || args[0].equalsIgnoreCase("localhost"))){
				address = args[0];
				port = Integer.parseInt(args[1]);
			} else {
				System.out.println("[" + new Timestamp(new Date().getTime()).toString() + "] " + "Unable to detect input properly. Systemw will run with default and address and port.");
			}
		}
		AuctionServerInterface serverInt = new AuctionServerImpl();
		Naming.rebind("//"+address+":"+port+"/AuctionSystem", serverInt);
		try {
			serverInt.restoreState();
			System.out.println("[" + new Timestamp(new Date().getTime()).toString() + "] " + "Previous save state has been restored");
		} catch (Exception e){
			System.out.println("[" + new Timestamp(new Date().getTime()).toString() + "] " +"Unable to restore state");
		}
		System.out.println("[" + new Timestamp(new Date().getTime()).toString() + "] " + "Server has started");
		try {
			serverInt.checkClient();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
