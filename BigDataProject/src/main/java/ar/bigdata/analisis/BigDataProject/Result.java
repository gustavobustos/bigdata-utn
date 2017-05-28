package ar.bigdata.analisis.BigDataProject;

import java.util.List;

import ar.bigdata.analisis.model.TweetWithSentiment;

public class Result {
	List<TweetWithSentiment> sentiments;
	String keyword;
	public Result(String keyword,List<TweetWithSentiment> sentiments)
	{
		this.sentiments=sentiments;
		this.keyword=keyword;
	}
	@Override
	public String toString() {
		String out="";
		out="Keyword: "+this.keyword+"\n";
		for (TweetWithSentiment tweetWithSentiment : sentiments) {
			out+="TS: "+tweetWithSentiment.toString()+"\n";
		}
		return out;
	}
	
	

}