package ar.bigdata.analisis.dao;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

public interface TwitterDao {
	
	void findAll(String dbName, String dbCollectionName);
	
	void insertCollectionTweets(String dbName, String collectionName, Document document);
	
	MongoCollection<Document> getDBCollection(String dbName, String dbCollectionName);

}
