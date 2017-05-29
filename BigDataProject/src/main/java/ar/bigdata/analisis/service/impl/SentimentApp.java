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
import ar.bigdata.analisis.util.StringUtil;

public class SentimentApp {

	private final Logger log = LoggerFactory.getLogger(SentimentApp.class);
	
	private TwitterDao twitterDao;
	private TwitterService twitterService;
	private SentimentAnalysisService sentimentAnalysisService;
	
	public static String HASH = "#";
	public static String MONGO_DB = "bigdata_db";
	public static String MONGO_HOST = "localhost";
	public static String MONGO_TWITTER_COLLECTION = "twitterposts";

	public static int MONGO_PORT = 27017;
	public static int NUMBER_OF_TWEETS = 10;
	
	
	public static String[] TAGS = {"MacriGato"};/*, "FelizCumplePresidente", "TodosPresos",
			"LiberenAMilagro", "Argentina",
			"GobieronDeCorruptosDelicuentes", "DictaduraJudicial", "Venezuela",
			"Cambiemos", "personanograta", "cgt", "buenosaires" };*/
		
	public SentimentApp () {
		twitterService = new TwitterServiceImpl(NUMBER_OF_TWEETS);
		sentimentAnalysisService = new SentimentAnalysisServiceImpl();
		twitterDao = new TwitterDaoMongo(MONGO_HOST, MONGO_PORT);
	}
	
	public static void main(String[] args) {
		
		SentimentApp sentimentApp = new SentimentApp();
		sentimentApp.process();
	}
	
	public void process() {
		
		log.info("Starting... ");
		
		for (String tag : TAGS) {
			
			String hashTag = HASH + tag;
			
			log.info("Procesing hastag: " + hashTag);
			
			List<Document> tweetsList = new ArrayList<Document>();
			List<Status> tweets = twitterService.fetchTweets(hashTag);
			
			for (Status status : tweets) {
				
				if(log.isDebugEnabled()) {
					log.debug("Status: " + status);
				}
				
				String text = status.getText();
				String msg = StringUtil.removeEmojisAndOtherChars(text);
	
				TweetWithSentiment tweetWithSentiment = sentimentAnalysisService.findSentiment(msg);
				
				Document tweetPost = buildPostDocument(tweetWithSentiment.getSentiment(), hashTag, msg, status);
				
				tweetsList.add(tweetPost);
	
			}
			
			twitterDao.insertManyTweets(MONGO_DB, MONGO_TWITTER_COLLECTION , tweetsList);
		}
		
		log.info("Ending... ");
	}
	
	public Document buildPostDocument (int sentiment, String hashTag, String msg, Status status) {
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
