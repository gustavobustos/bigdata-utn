package ar.bigdata.analisis.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.bigdata.analisis.service.TwitterService;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterServiceImpl implements TwitterService {
	
	private final Logger log = LoggerFactory.getLogger(TwitterServiceImpl.class);
	
	private int numberOfTweets = 100;
	
	private Twitter twitter;
	private ConfigurationBuilder configurationBuilder;
	
	public static String consumerKey = "bt3oOrjg8Yc58zLqefBUa4ptc";
	public static String consumerSecret = "ueUhYxtFRBFWajrPOpKBTT3BixNQhxs8DpfErsyAMun8UXRw99";
	public static String accessToken = "855886082260578304-NGiviQxyxnq6NdiQejY3RQNHhNs1JPF";
	public static String accessTokenSecret = "DbrDWIW2IHV3eV17raMwtrwlguj0vpJ6jaXa6Vz03P2xR";
	
	public TwitterServiceImpl (int numberOfTweets) {
		
		configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setDebugEnabled(true)
				.setOAuthConsumerKey(consumerKey)
				.setOAuthConsumerSecret(consumerSecret)
				.setOAuthAccessToken(accessToken)
				.setOAuthAccessTokenSecret(accessTokenSecret);
		
		twitter = new TwitterFactory(configurationBuilder.build()).getInstance();
		
		this.numberOfTweets = numberOfTweets;
	}
	
	public List<Status> fetchTweets (String hashtag) {
		
		Query query = new Query(hashtag);
		
		long lastID = Long.MAX_VALUE;
		
		List<Status> tweets = new ArrayList<Status>();
		
		while (tweets.size() < numberOfTweets) {
			
			if (numberOfTweets - tweets.size() > 100) {
				query.setCount(100);
			} else {
				query.setCount(numberOfTweets - tweets.size());
			}
			
			try {
				QueryResult result = twitter.search(query);
				tweets.addAll(result.getTweets());
				
				log.info("Gathered " + tweets.size() + " tweets");
				
				for (Status t : tweets) {
					if (t.getId() < lastID) {
						lastID = t.getId();
					}
				}
			}

			catch (TwitterException te) {
				log.error("Couldn't connect: " + te);
			}
			query.setMaxId(lastID - 1);
		}
		
		return tweets;
	}

}
