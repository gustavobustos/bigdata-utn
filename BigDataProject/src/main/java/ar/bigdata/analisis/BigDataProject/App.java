package ar.bigdata.analisis.BigDataProject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String args[]) throws Exception {
		// The factory instance is re-useable and thread safe.
		Twitter twitter = TwitterFactory.getSingleton();
		//twitter.setOAuthConsumer("DUwQw5rwHgoa3PbtMyKgfw6RV", "GJpvLOHRBoXb2uU98Y0lgdqCj2cxKqyKV4zUq3bizJljBHsSbX");
		
		System.out.println("key:" + twitter.getConfiguration().getOAuthConsumerKey());
		System.out.println("secret: " + twitter.getConfiguration().getOAuthConsumerSecret());
		
	    List<Status> statuses = twitter.getHomeTimeline();
	    System.out.println("Showing home timeline.");
	    for (Status status : statuses) {
	        System.out.println(status.getUser().getName() + ":" +
	                           status.getText());
	    }
		System.exit(0);
	}

	private static void storeAccessToken(long userId, AccessToken accessToken) {
		System.out.println("user: " + userId);
		System.out.println("accesToken: " + accessToken);
		// store accessToken.getToken()
		// store accessToken.getTokenSecret()
	}
}
