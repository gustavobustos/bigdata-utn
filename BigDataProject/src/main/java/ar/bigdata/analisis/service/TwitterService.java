package ar.bigdata.analisis.service;

import java.util.List;

import twitter4j.Status;

public interface TwitterService {

	List<Status> fetchTweets (String hashtag);
}
