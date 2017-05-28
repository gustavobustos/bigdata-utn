package ar.bigdata.analisis.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.GeoLocation;
import twitter4j.Status;
import ar.bigdata.analisis.dao.TwitterDao;
import ar.bigdata.analisis.dao.mongo.TwitterDaoMongo;
import ar.bigdata.analisis.model.TweetWithSentiment;
import ar.bigdata.analisis.service.SentimentAnalysisService;
import ar.bigdata.analisis.service.TwitterService;

public class MainApp {

	private final Logger log = LoggerFactory.getLogger(MainApp.class);
	
	private TwitterService twitterService;
	private SentimentAnalysisService sentimentAnalysisService;
	private TwitterDao twitterDao;
	
	public static String HASH = "#";
	public static String MONGO_DB = "test_db";
	public static String MONGO_TWITTER_COLLECTION = "twitterpoststest";
	
	public MainApp () {
		twitterService = new TwitterServiceImpl(10);
		sentimentAnalysisService = new SentimentAnalysisServiceImpl();
		twitterDao = new TwitterDaoMongo();
	}
	
	public static void main(String[] args) {
		
		MainApp mainApp = new MainApp();
		mainApp.process();
	}
	
	public void process() {
		
		log.info("Starting... ");
		String tag = "macrigato";
		String hashTag = HASH + tag;
		
		List<Status> tweets = twitterService.fetchTweets(hashTag);
		Document collectionDocument = new Document();
		List<Document> tweetsList = new ArrayList<Document>();
		
		for (Status status : tweets) {
			
			log.info("Status: " + status);
			
			String msg = status.getText();

			TweetWithSentiment tweetWithSentiment = sentimentAnalysisService.findSentiment(msg);
			
			Document tweetPost = buildPostDocument(tweetWithSentiment.getSentiment(), status);
			
			tweetsList.add(tweetPost);

		}

		collectionDocument.append(hashTag, tweetsList);
		
		twitterDao.insertCollectionTweets(MONGO_DB, MONGO_TWITTER_COLLECTION , collectionDocument);
		
		log.info("Ending... ");
	}
	
	public Document buildPostDocument (int sentiment, Status status) {
		Document tweetPost = new Document();
		GeoLocation loc = status.getGeoLocation();

		String user = status.getUser().getScreenName();
		String msg = status.getText();
		
		String sentimentStr = String.valueOf(sentiment);
		
		String latitude = "";
		String longitude = "";
		
		if (loc != null) {
			latitude = String.valueOf(loc.getLatitude());
			longitude = String.valueOf(loc.getLongitude());
		} 
		
		tweetPost.append("sentiment", sentimentStr);
		tweetPost.append("userName", user);
		tweetPost.append("text", msg);
		tweetPost.append("lattitude", latitude);
		tweetPost.append("longitude", longitude);
		tweetPost.append("tweetDate", status.getCreatedAt());
		tweetPost.append("source", status.getSource());
		tweetPost.append("userLocation", status.getUser().getLocation());
		tweetPost.append("userTimezone", status.getUser().getTimeZone());
		
		log.info(" USER: " + user + " wrote: " + msg);
		if(status.getPlace() != null) {
			log.info("place: " + status.getPlace().getName());
		} else {
			log.info("place: ");
		}
		log.info("date: " + status.getCreatedAt());
		log.info("source: " + status.getSource());
		log.info("userLocation: " + status.getUser().getLocation() + " timeZone: " + status.getUser().getTimeZone());
		log.info("");
		
		return tweetPost;
		
	}

}
