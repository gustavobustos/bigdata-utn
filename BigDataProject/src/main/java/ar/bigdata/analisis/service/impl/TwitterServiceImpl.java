package ar.bigdata.analisis.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.bigdata.analisis.service.TwitterService;
import ar.bigdata.analisis.util.StringUtil;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterServiceImpl implements TwitterService {
	
	private final Logger log = LoggerFactory.getLogger(TwitterServiceImpl.class);
	
	private int maxTweetsNumber = 100;
	
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
		this.maxTweetsNumber = numberOfTweets;
	}
	
	public List<Status> fetchTweets (Long maxId, String hashtag) {
		
		long lastID = 0;
		if(maxId == null) {
			lastID = Long.MAX_VALUE;
		} else {
			lastID = maxId.longValue();
		}
		
		List<Status> tweets = new ArrayList<Status>();
		
		Query query = new Query(hashtag);
		
		while (tweets.size() < maxTweetsNumber) {
			
			if (maxTweetsNumber - tweets.size() > 100) {
				query.setCount(100);
			} else {
				query.setCount(maxTweetsNumber - tweets.size());
			}
			
			try {

				Map<String, RateLimitStatus> rateLimitMap = twitter.getRateLimitStatus();
				RateLimitStatus rateLimitStatus = rateLimitMap.get("/search/tweets");
				
				int remainingCalls = rateLimitStatus.getRemaining();
				int secondsUntilReset = rateLimitStatus.getSecondsUntilReset();

				log.info("Remaining Calls: " + remainingCalls + " | Remaining Seconds: " + secondsUntilReset);
				
				if (remainingCalls < 2 || secondsUntilReset <= 10 ) {
					
					long sleepMillis = (secondsUntilReset + 1 ) * 1000;
					
					log.info("Sleeping: " + (sleepMillis / 1000));
					
					Thread.sleep(sleepMillis);
				}
				
				QueryResult result = twitter.search(query);
				
				List<Status> searchedTweets = result.getTweets(); 
				
				if(searchedTweets != null && !searchedTweets.isEmpty()) {
					
					tweets.addAll(searchedTweets);
				
					for (Status status : tweets) {
						
						String text = status.getText();
						String textWithoutEmojis = StringUtil.removeEmojisAndOtherChars(text);
						
						if(log.isDebugEnabled()) {
							log.debug("id: " + status.getId() + " date: " + status.getCreatedAt() + " msg: " + textWithoutEmojis);
						}
						
						if (status.getId() < lastID) {
							lastID = status.getId();
						}
					}
					
					log.info("Fetched " + searchedTweets.size() + " tweets");
				} else {
					log.info("no more tweets to read for the hashtag: " + hashtag);
					break;
				}
				
				query.setMaxId(lastID - 1);
				
			} catch (InterruptedException ie) {
				log.error("Thread Exception... ", ie);
				break;
			} catch (TwitterException te) {
				log.error("Couldn't connect... ", te);
				break;
			}
			
		}
		
		log.info("Gathered " + tweets.size() + " tweets");
		
		return tweets;
	}
}
