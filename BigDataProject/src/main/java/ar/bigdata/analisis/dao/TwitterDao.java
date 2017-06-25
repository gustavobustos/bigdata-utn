package ar.bigdata.analisis.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

public interface TwitterDao {
	
	void findAll(String dbCollectionName);
	
	void insertTweet(String collectionName, Document document);
	
	void insertManyTweets(String collectionName, List<Document> documents);
	
	MongoCollection<Document> getDBCollection(String dbCollectionName);
	
	UpdateResult updateCollectionTweets(String collectionName, Bson bsonFilter, Document document);
	
	Set<String> projectionByAttribute(String dbCollectionName, String attribute);
	
	List<String> getSingleProjectionByFilterKeyValue(String dbCollectionName, String projectionFieldKey, String filterKey, String filterValue );
	
	MongoCursor<Document> projectionByAttributes(String dbCollectionName, Map<String, Object> filter, String... attributes); 
	
	UpdateResult updateCollectionTweets(String collectionName, Bson bsonFilter, Document document, UpdateOptions updateOptions);

}
