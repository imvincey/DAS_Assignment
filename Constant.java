import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Constant {
	private Matcher matcher;
	private Pattern pattern;
    private final String IPADDRESS_PATTERN =
		"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	
	public Constant(){
		pattern = Pattern.compile(IPADDRESS_PATTERN);
	}
	
	public String getHost(){
		return "localhost";
	}
	
	public int getPort(){
		return 1099;
	}
	
	public String getProgramInterface(){
		return "What would you like to do ? \n" +
		"1. Create a bidding item \n" + 
		"2. Bid for an item \n" + 
		"3. View all item that has listed \n" +
		"4. Exit the program \n";
	}
	
	public boolean checkInteger(String str){
		try {
			Integer.parseInt(str);
		} catch (Exception e){
			return false;
		}
		return true;
	}

   /**
    * Validate ip address with regular expression
    * @param ip ip address for validation
    * @return true valid ip address, false invalid ip address
    */
    public boolean validateIPAddress(String ip){
	  matcher = pattern.matcher(ip);
	  return matcher.matches();
    }
}
