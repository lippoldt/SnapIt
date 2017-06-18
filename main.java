package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;





/** SnapIt
 * Given an image provided through url, this class will extract the text on it and find the corresponding event
 * on social media.
 *
 * @author franziska
 *
 */

public class main{

	// list of first 10 search result links using Google
	private String[] Glinks;
	//Facebook Id of the event
	private String FBid;
	FacebookClient facebookClient;


	public main(){
		// URL of image to check, to be filled in by the user
		String url = "";

		// event name parsed from the image
		String event = "";

		//tokens for the usage of FB and Google services
		String FBtoken ="";
		String Gtoken = "";

		if (LogInGoogle(Gtoken)){
			//Apply OCR on the image
			event = getTextfromImage(url);
			// find corresponding results on Google
				try{
					findOnGoogle(event);
				}catch(Exception e){
					System.err.println("Querrying search results on Google failed");
					e.printStackTrace();
				}


		}

	//get Facebookevent details for recommendation
		System.out.println("The event you are looking for is " + event);

		System.out.println(FBdetails(FBid));
	return;
	}

/*	 *//** Authorizes the installed application to access user's protected data. *//*
	 private static Credential authorize() throws Exception {
	   // load client secrets
	   GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
	       new InputStreamReader(CalendarSample.class.getResourceAsStream("/client_secrets.json")));
	   // set up authorization code flow
	   GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
	       httpTransport, JSON_FACTORY, clientSecrets,
	       Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(dataStoreFactory)
	      .build();
	   // authorize
	   return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}
*/
	/** Log in into the Google account
	 * @param token
	 * @return boolean whether suceeded or not
	 */
	private boolean LogInGoogle(String token){
		  try {
			    httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			    dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
			    // authorization
			    Credential credential = authorize();
			    // set up global Plus instance
			    plus = new Plus.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(
			        APPLICATION_NAME).build();
			   // ...
			}
		  catch (Exception e){
			  return false;
		  }
		return true;
	}

	/**
	 * Credentials
	 * @return
	 * @throws Exception
	 */
	private static Credential authorize() throws Exception {
		  // load client secrets
		  GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
		      new InputStreamReader(PlusSample.class.getResourceAsStream("/client_secrets.json")));
		  // set up authorization code flow
		  GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
		      httpTransport, JSON_FACTORY, clientSecrets,
		      Collections.singleton(PlusScopes.PLUS_ME)).setDataStoreFactory(
		      dataStoreFactory).build();
		  // authorize
		  return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		}

	/**
	 * Acess FB through a token
	 * @param token
	 * @return boolean if the action completed
	 */
	private boolean LogInFB(String token){
		FacebookClient facebookClient = new DefaultFacebookClient(token);

		return true;
	}

	/**
	 * Use the Google UCR to read the text from an image
	 *
	 * @param url the path of the image
	 * @return the text on the image
	 */
	private String getTextfromImage(String url){
		String text = "";


		return text;
	}
	/**
	 * Find google search results of the input String and put them into a list of top10
	 * also scans for FB event specifically and saves the event nr extracted from the url
	 * @param eventname name to be searched
	 * @throws Exception if Google search fails
	 */
	private void findOnGoogle(String eventname)throws Exception {
	    String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
	    String charset = "UTF-8";

	    URL url = new URL(google + URLEncoder.encode(eventname, charset));
	    Reader reader = new InputStreamReader(url.openStream(), charset);
	    GoogleResults results = new Gson().fromJson(reader, GoogleResults.class);
	    BufferedReader in = new BufferedReader(new StringReader(resultJson));

	    Pattern regex = Pattern.compile(".*\"link\": \"(.*)\",");
	    Collection<String> links = new ArrayList<String>();
	    String line = null;
	    while ((line = in.readLine()) != null) {
	        Matcher matcher = regex.matcher(line);
	        if (matcher.matches()) {
	            String link = matcher.group(1);
	            links.add(link);
	        }
	    }

	    Glinks = new String[10];
	    ((ArrayList<String>) links).removeRange(10,links.size());
	    links.toArray(Glinks);

	    //search for FB ID
	    String sub;
	    String fb = "https://www.facebook.com/events/";
	    for (int i = 0; i< 10 ; i++){
	    	sub = Glinks[i].substring(0, fb.length()-1);
	    	if (sub.equals(fb)){
	    		FBid = Glinks[i].substring(fb.length()-1,fb.length()-1+16);
	    		break;
	    	}
	    }

	}

	/**
	 * given an event ID for a FB event, this method prints the corresponding date and possibly other
	 * event details
	 * @param eventID FB event id
	 * @return String message of details
	 */
	private String FBdetails(String eventID){
		String details ="";

	    String date = facebookClient.fetchObject("{" + eventID + "}/date", String.class);
	    System.out.println("The event is on: " + date);
	    details = date;

		return details;

	}
}