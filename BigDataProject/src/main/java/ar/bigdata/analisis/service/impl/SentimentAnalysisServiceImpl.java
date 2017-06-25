package ar.bigdata.analisis.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOptions;

import ar.bigdata.analisis.dao.TwitterDao;
import ar.bigdata.analisis.dao.mongo.TwitterDaoMongo;
import ar.bigdata.analisis.model.TweetWithSentiment;
import ar.bigdata.analisis.service.SentimentAnalysisService;
import ar.bigdata.analisis.util.StringUtil;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class SentimentAnalysisServiceImpl implements SentimentAnalysisService {
	
	private static final Logger log = LoggerFactory.getLogger(SentimentAnalysisServiceImpl.class);
	
	private StanfordCoreNLP stanforNLP;
	
	public SentimentAnalysisServiceImpl () {
		stanforNLP = new StanfordCoreNLP("StanfordCoreNLP-spanish");
	}
	
	public TweetWithSentiment findSentiment(String line) {
		
		int mainSentiment = 0;
		
		if (line != null && line.length() > 0) {
			
			int longest = 0;
			//line = line.toLowerCase();
			Annotation annotation = stanforNLP.process(line);
			
			for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
				
				Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
				
				log.info("Tree: " + tree);
				
				if (tree != null) {

					int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
					String partText = sentence.toString();
					if (partText.length() > longest) {
						mainSentiment = sentiment;
						longest = partText.length();
					}
					
					log.info("sentiment: " + sentiment + " frase: " + partText);
				}

			}
		}
		
		TweetWithSentiment tweetWithSentiment = new TweetWithSentiment(line, toCss(2), mainSentiment);
		
		return tweetWithSentiment;
	}
	
	public static void main(String[] args) {
		
		SentimentAnalysisService sentimentAnalysisService = new SentimentAnalysisServiceImpl();
		TwitterDao twitterDao = new TwitterDaoMongo(SentimentApp.MONGO_DB, SentimentApp.MONGO_HOST, SentimentApp.MONGO_PORT);
		
		Map<String, Object> filter = new HashMap<String, Object>();
		
		//filter.put("hashTag", "@infobae");
		//filter.put("tweetId", 878679646547701760l);
		
		String idField = "tweetId";
		String sentimentField = "sentiment";
		String textField = "text";
		
		MongoCursor<Document> cursor = twitterDao.projectionByAttributes("mltraining", filter, idField , textField);
		Set<TweetWithSentiment> tweetList = new HashSet<TweetWithSentiment>();
		try {
			while (cursor.hasNext()) {
				
				Document document = cursor.next();
				String jsonText = document.get(textField).toString();
				jsonText = StringUtil.removeEmojisAndOtherChars(jsonText);
				Long tweetIdValue = Long.valueOf(document.get(idField).toString());
				
				TweetWithSentiment tweetWithSentiment = sentimentAnalysisService.findSentiment(jsonText);
				
				Bson filterBson = new Document(idField, tweetIdValue);
				Bson newValue = new Document(sentimentField, String.valueOf(tweetWithSentiment.getSentiment()));

				Document updateOperationDocument = new Document("$set", newValue);
				
				twitterDao.updateCollectionTweets("mltraining", filterBson, updateOperationDocument);
				
				
				
				
				/*if(jsonText.indexOf("@infobae") > 0) {
					
					
					jsonText = jsonText.substring(jsonText.indexOf("@infobae".length()) + 1, jsonText.length());
					log.info("jsonText: " + jsonText);
				}*/
				//log.info("Text: " + jsonText);
				//TweetWithSentiment tweetWithSentiment = sentimentAnalysisService.findSentiment(jsonText);
				//TweetWithSentiment tweetWithSentiment = new TweetWithSentiment(jsonText,"",2 );
				//tweetList.add(tweetWithSentiment);
				//log.info("Text tweets: " + sentiment.getSentiment());
				//log.
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		
		//prepareSentimentFile(tweetList);
		
		
	}
	
	private static void prepareSentimentFile (Set<TweetWithSentiment> phrases) {
		
		try {
			File fout = new File("twitter-dataset.txt");
			FileOutputStream fos = new FileOutputStream(fout);
		 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			for (TweetWithSentiment tweetWithSentiment : phrases) {
				
				String line = "" + tweetWithSentiment.getSentiment() + "\t" + tweetWithSentiment.getLine() ;
				bw.write(line);
				bw.newLine();
				bw.newLine();
			}
			
			bw.close();
		} catch (IOException ioException) {
			log.error("IO: ",ioException);
		}
			
		
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

}
