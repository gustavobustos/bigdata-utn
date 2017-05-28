package ar.bigdata.analisis.service;

import ar.bigdata.analisis.model.TweetWithSentiment;

public interface SentimentAnalysisService {

	TweetWithSentiment findSentiment(String line);
}
