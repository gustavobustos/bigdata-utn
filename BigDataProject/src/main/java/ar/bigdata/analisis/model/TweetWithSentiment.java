package ar.bigdata.analisis.model;

public class TweetWithSentiment {
    private String line;
    private String cssClass;
    private int sentiment;

    public TweetWithSentiment() {
    }

    public TweetWithSentiment(String line, String cssClass, int sentiment) {
        super();
        this.line = line;
        this.cssClass = cssClass;
        this.sentiment = sentiment;
    }

    public String getLine() {
        return line;
    }

    public String getCssClass() {
        return cssClass;
    }
    
    public int getSentiment() {
		return sentiment;
	}

	public void setSentiment(int sentiment) {
		this.sentiment = sentiment;
	}

	@Override
    public String toString() {
        return "TweetWithSentiment [line=" + line + ", cssClass=" + cssClass + "]";
    }

}