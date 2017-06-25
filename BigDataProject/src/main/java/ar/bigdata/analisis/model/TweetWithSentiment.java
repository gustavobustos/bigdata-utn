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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((line == null) ? 0 : line.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TweetWithSentiment other = (TweetWithSentiment) obj;
		if (line == null) {
			if (other.line != null)
				return false;
		} else if (!line.equals(other.line))
			return false;
		return true;
	}

	@Override
    public String toString() {
        return "TweetWithSentiment [line=" + line + ", cssClass=" + cssClass + "]";
    }

}