package ar.bigdata.analisis.dao;

import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;

public interface TwitterDao {
	
	void findAll(String dbName, String dbCollectionName);
	
	void insertTweet(String dbName, String collectionName, Document document);
	
	void insertManyTweets(String dbName, String collectionName, List<Document> documents);
	
	MongoCollection<Document> getDBCollection(String dbName, String dbCollectionName);
	
	UpdateResult updateCollectionTweets(String dbName, String collectionName, Bson bsonFilter, Document document);

}
