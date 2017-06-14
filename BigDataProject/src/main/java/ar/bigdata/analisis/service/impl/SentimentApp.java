package ar.bigdata.analisis.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.GeoLocation;
import twitter4j.Status;
import ar.bigdata.analisis.dao.TwitterDao;
import ar.bigdata.analisis.dao.mongo.TwitterDaoMongo;
import ar.bigdata.analisis.service.SentimentAnalysisService;
import ar.bigdata.analisis.service.TwitterService;
import ar.bigdata.analisis.util.StringUtil;

public class SentimentApp {

	private final Logger log = LoggerFactory.getLogger(SentimentApp.class);

	private TwitterDao twitterDao;
	private TwitterService twitterService;
	private SentimentAnalysisService sentimentAnalysisService;

	public static String HASH = "#";
	public static String MONGO_DB = "bigdata_db";
	public static String MONGO_HOST = "localhost";
	public static String MONGO_TWITTER_COLLECTION = "twitterpoststest4";

	public static int MONGO_PORT = 27017;
	public static int MAX_TWEETS_NUMBER_PER_HASHTAG = 5000;

	public static Set<String> processedTweets = new HashSet<String>();

	public static String[] TAGS = { "MacriGato"};//, "FelizCumplePresidente", "TodosPresos", "LiberenAMilagro", "Argentina","GobieronDeCorruptosDelicuentes", "DictaduraJudicial","Venezuela", "Cambiemos","personanograta", "cgt","buenosaires" };
												 

	public SentimentApp() {
		twitterService = new TwitterServiceImpl(MAX_TWEETS_NUMBER_PER_HASHTAG);
		sentimentAnalysisService = new SentimentAnalysisServiceImpl();
		twitterDao = new TwitterDaoMongo(MONGO_DB, MONGO_HOST, MONGO_PORT);
		processedTweets = twitterDao.projectionByAttribute(
				MONGO_TWITTER_COLLECTION, "tweetId");
	}

	public static void main(String[] args) {

		SentimentApp sentimentApp = new SentimentApp();
		sentimentApp.process();
	}

	public void process() {

		log.info("Starting... ");

		log.info("current total tweets: " + processedTweets.size());
		
		for (String tag : TAGS) {

			String hashTag = HASH + tag;

			List<String> tweetIds = twitterDao.getSingleProjectionByFilterKeyValue(MONGO_TWITTER_COLLECTION, "tweetId", "hashTag", hashTag);
			
			if(!tweetIds.isEmpty()) {
				
				Long min = Long.valueOf(Collections.min(tweetIds)) - 1;
				String maxStr = Collections.max(tweetIds);
				Long max = Long.valueOf(maxStr);
				
				/*List<String> tweetDates = twitterDao.getSingleProjectionByFilterKeyValue(MONGO_TWITTER_COLLECTION, "tweetDate", "hashTag", hashTag);
				log.info("tweetDates: " + tweetDates);*/
				log.info("Procesing hashtag: " + hashTag + " min: " + min + " max: " + max);
				processTweets(min, hashTag);
			} else {
				log.info("The hashtag: " + hashTag + " hasn't tweets: " + tweetIds + ", so creating a new collection");
				processTweets(null, hashTag);
			}
			

		}

		log.info("Ending... ");
	}
	
	public void processTweets (Long min, String hashTag) {
		
		List<Status> tweets = twitterService.fetchTweets(min, hashTag);

		List<Document> tweetsList = extractTweets (hashTag, tweets);
		
		if(!tweetsList.isEmpty()) {
			twitterDao.insertManyTweets(MONGO_TWITTER_COLLECTION , tweetsList);
		}
	}
	
	public List<Document> extractTweets (String hashTag, List<Status> tweets) {
		
		List<Document> newTweetsList = new ArrayList<Document>();
		List<String> processedTweetsList = new ArrayList<String>();
		
		for (Status status : tweets) {

			String tweetId = String.valueOf(status.getId());
			
			if (!processedTweets.contains(tweetId)) {
				if (log.isDebugEnabled()) {
					log.debug("Status: " + status);
				}

				String text = status.getText();
				//String msg = StringUtil.removeEmojisAndOtherChars(text);

				// TweetWithSentiment tweetWithSentiment =
				// sentimentAnalysisService.findSentiment(msg);

				// Document tweetPost =
				// buildPostDocument(tweetWithSentiment.getSentiment(),
				// hashTag, msg, status);
				Document tweetPost = buildPostDocument(2, hashTag, text, status);

				newTweetsList.add(tweetPost);

			} else {
				processedTweetsList.add(tweetId);
				if (log.isDebugEnabled()) {
					log.debug("tweet already processed: " + tweetId);	
				}
				
			}

		}
		log.info("new tweets processed: " + newTweetsList.size());
		log.info("tweets already processed: " + processedTweetsList.size());
		
		return newTweetsList;
	}

	public Document buildPostDocument(int sentiment, String hashTag,
			String msg, Status status) {
		Document tweetPost = new Document();
		GeoLocation loc = status.getGeoLocation();

		String user = status.getUser().getScreenName();
		String sentimentStr = String.valueOf(sentiment);

		String latitude = "";
		String longitude = "";

		if (loc != null) {
			latitude = String.valueOf(loc.getLatitude());
			longitude = String.valueOf(loc.getLongitude());
		}

		tweetPost.append("tweetId", status.getId());
		tweetPost.append("hashTag", hashTag);
		tweetPost.append("sentiment", sentimentStr);
		tweetPost.append("userName", user);
		tweetPost.append("text", msg);
		tweetPost.append("lattitude", latitude);
		tweetPost.append("longitude", longitude);
		tweetPost.append("tweetDate", status.getCreatedAt());
		tweetPost.append("source", status.getSource());
		tweetPost.append("userLocation", status.getUser().getLocation());
		tweetPost.append("userTimezone", status.getUser().getTimeZone());

		if (log.isDebugEnabled()) {
			log.debug(" USER: " + user + " wrote: " + msg);
			if (status.getPlace() != null) {
				log.debug("place: " + status.getPlace().getName());
			} else {
				log.debug("place: ");
			}
			log.debug("date: " + status.getCreatedAt());
			log.debug("source: " + status.getSource());
			log.debug("userLocation: " + status.getUser().getLocation()
					+ " timeZone: " + status.getUser().getTimeZone());
			log.debug("");
		}

		return tweetPost;

	}

}
