package ar.bigdata.analisis.BigDataProject;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class TestBG {
	public List<Status> search(String keyword) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey("bt3oOrjg8Yc58zLqefBUa4ptc")
				.setOAuthConsumerSecret(
						"ueUhYxtFRBFWajrPOpKBTT3BixNQhxs8DpfErsyAMun8UXRw99")
				.setOAuthAccessToken(
						"855886082260578304-NGiviQxyxnq6NdiQejY3RQNHhNs1JPF")
				.setOAuthAccessTokenSecret(
						"DbrDWIW2IHV3eV17raMwtrwlguj0vpJ6jaXa6Vz03P2xR");

		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		Query query = new Query(
				keyword
						+ " -filter:retweets -filter:links -filter:replies -filter:images");
		query.setCount(100);
		//query.since("2016-06-21");
		//query.setUntil("2017-05-21");
		query.setLocale("es");
		query.setLang("es");
		System.out.println("GeoCode: "+ query.getGeocode());
		;
		try {
			//QueryResult queryResult = twitter.search(query);
			/*while (queryResult.hasNext()) {
				Query nextQuery = (Query) queryResult.nextQuery();
				
				
			}*/
			List<Status> allTweets = new ArrayList<Status>();
			fetchTweets(allTweets, twitter, query, 0);
			System.out.println("allTweets: " + allTweets);
			//return queryResult.getTweets();
			return allTweets;
		} catch (Exception e) {
			// ignore
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
	
	public void fetchTweets(List<Status> allTweets, Twitter twitter, Query query, int countpages) throws TwitterException, InterruptedException {
		
		QueryResult queryResult = null;
		try {
			queryResult = twitter.search(query);
			allTweets.addAll(queryResult.getTweets());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
			if(countpages == 20) {
				return;
			} else {
			
				if (queryResult.hasNext()) {
					Query nextQuery = (Query) queryResult.nextQuery();
					countpages ++;
					this.wait(5000);
					fetchTweets(allTweets, twitter, nextQuery, countpages);
				}
			}
		
		
		
	}

	public TweetWithSentiment findSentiment(String line) {

		// Properties props = new Properties();
		// props.load(inStream);
		// props.setProperty("annotators",
		// "tokenize, ssplit, parse, sentiment");
		/*
		 * Properties prop = new Properties(); try { InputStream in =
		 * getClass().getResourceAsStream("StanfordCoreNLP-spanish.properties");
		 * prop.load(in); in.close(); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		
		/*StanfordCoreNLP pipeline = new StanfordCoreNLP(
				"StanfordCoreNLP-spanish");
		int mainSentiment = 0;
		if (line != null && line.length() > 0) {
			int longest = 0;
			Annotation annotation = pipeline.process(line);
			for (CoreMap sentence : annotation
					.get(CoreAnnotations.SentencesAnnotation.class)) {
				Tree tree = sentence
						.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
				System.out.println("Tree: " + tree);
				if (tree != null) {

					int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
					String partText = sentence.toString();
					if (partText.length() > longest) {
						mainSentiment = sentiment;
						longest = partText.length();
					}
					System.out.println("sentiment: "+ sentiment +" frase: " +  partText);
				}

			}
		}
		if (mainSentiment == 2 || mainSentiment > 4 || mainSentiment < 0) {
			return null;
		}*/
		/*TweetWithSentiment tweetWithSentiment = new TweetWithSentiment(line,
				toCss(2));*/
		return null; //tweetWithSentiment;
		

	}

	private String toCss(int sentiment) {
		switch (sentiment) {
		case 0:
			return "alert alert-danger";
		case 1:
			return "alert alert-danger";
		case 2:
			return "alert alert-warning";
		case 3:
			return "alert alert-success";
		case 4:
			return "alert alert-success";
		default:
			return "";
		}
	}

	public List<Result> sentiments(String searchKeywords) {
		List<Result> results = new ArrayList();
		if (searchKeywords == null || searchKeywords.length() == 0) {
			return results;
		}

		Set<String> keywords = new HashSet();
		for (String keyword : searchKeywords.split(",")) {
			keywords.add(keyword.trim().toLowerCase());
		}
		if (keywords.size() > 3) {
			keywords = new HashSet(new ArrayList(keywords).subList(0, 3));
		}
		for (String keyword : keywords) {
			List<Status> statuses = search(keyword);
			System.out.println("Found statuses ... " + statuses.size());
			List<TweetWithSentiment> sentiments = new ArrayList();
			//try {
				//FileWriter fw = new FileWriter("traindos.txt");
				for (Status status : statuses) {
					System.out.println("line: " + status.getText());
					if(status.getGeoLocation() != null) {
						System.out.println("Geocode: " + status.getGeoLocation());
					}
					String line = status.getText();
					//fw.write(line);

					TweetWithSentiment tweetWithSentiment = findSentiment(line);
					if (tweetWithSentiment != null) {
						sentiments.add(tweetWithSentiment);
					}
				}
				//fw.close();
			//} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			//}

			Result result = new Result(keyword, sentiments);
			System.out.println(result);
			results.add(result);
		}
		return results;
	}

	public static void main(String[] args) {

		TestBG n = new TestBG();

		//n.sentiments("macrigato");
		/*
		 * ObjectInputStream in = null; try { in = new ObjectInputStream(new
		 * FileInputStream(
		 * "/home/gustavo/Workspaces/utn/big-data/stanford-english-corenlp-2016-10-31-models/edu/stanford/nlp/models/sentiment/sentiment.binary.ser"
		 * )); int count = 0;
		 * 
		 * while (true) { count++; try { Object obj = in.readObject();
		 * System.out.println("obj #" + count + " is a: " + obj.getClass());
		 * System.out.println(obj + ".toString(): " + obj); } catch
		 * (ClassNotFoundException e) { System.out.println("can't read obj #" +
		 * count + ": " + e); } }
		 * 
		 * 
		 * } catch (Exception e) { // unfortunately ObjectInputStream doesn't
		 * have a good way to detect the end of the stream // so just ignore
		 * this exception - it's expected when there are no more objects }
		 * finally {
		 * 
		 * }
		 */

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey("bt3oOrjg8Yc58zLqefBUa4ptc")
				.setOAuthConsumerSecret(
						"ueUhYxtFRBFWajrPOpKBTT3BixNQhxs8DpfErsyAMun8UXRw99")
				.setOAuthAccessToken(
						"855886082260578304-NGiviQxyxnq6NdiQejY3RQNHhNs1JPF")
				.setOAuthAccessTokenSecret(
						"DbrDWIW2IHV3eV17raMwtrwlguj0vpJ6jaXa6Vz03P2xR");
		Twitter twitter = new TwitterFactory(cb.build()).getInstance();
		Query query = new Query("#macrigato");
		int numberOfTweets = 5000;
		long lastID = Long.MAX_VALUE;
		ArrayList<Status> tweets = new ArrayList<Status>();
		while (tweets.size() < numberOfTweets) {
			if (numberOfTweets - tweets.size() > 100)
				query.setCount(100);
			else
				query.setCount(numberOfTweets - tweets.size());
			try {
				QueryResult result = twitter.search(query);
				tweets.addAll(result.getTweets());
				System.out.println("Gathered " + tweets.size() + " tweets"
						+ "\n");
				for (Status t : tweets) {
					if (t.getId() < lastID) {
						lastID = t.getId();
					}
				}
			}

			catch (TwitterException te) {
				System.out.println("Couldn't connect: " + te);
			}
			;
			query.setMaxId(lastID - 1);
		}

		for (int i = 0; i < tweets.size(); i++) {
			Status t = (Status) tweets.get(i);

			GeoLocation loc = t.getGeoLocation();

			String user = t.getUser().getScreenName();
			String msg = t.getText();
			String time = "";
			if (loc != null) {
				Double lat = t.getGeoLocation().getLatitude();
				Double lon = t.getGeoLocation().getLongitude();
				System.out.println(i + " USER: " + user + " wrote: " + msg
						+ "\n");
			} else
				System.out.println(i + " USER: " + user + " wrote: " + msg
						+ "\n");
		}

	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see java.lang.Object#Object()
	 */

}