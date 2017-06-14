package ar.bigdata.analisis.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.bigdata.analisis.model.TweetWithSentiment;
import ar.bigdata.analisis.service.SentimentAnalysisService;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class SentimentAnalysisServiceImpl implements SentimentAnalysisService {
	
	private final Logger log = LoggerFactory.getLogger(SentimentAnalysisServiceImpl.class);
	
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
		sentimentAnalysisService.findSentiment("Macri es una persona mala");
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
