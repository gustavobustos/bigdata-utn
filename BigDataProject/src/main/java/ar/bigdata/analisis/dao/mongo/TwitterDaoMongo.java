package ar.bigdata.analisis.dao.mongo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.bigdata.analisis.dao.TwitterDao;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

public class TwitterDaoMongo implements TwitterDao {
	
	private final Logger log = LoggerFactory.getLogger(TwitterDaoMongo.class);
	
	private String dbName;
	private MongoClient mongoClient;
	
	
	public TwitterDaoMongo (String dbName, String host, int port) {
		this.dbName = dbName;
		this.mongoClient = new MongoClient(host, port);
		
	}
	
	public MongoDatabase getDatabase() {
		MongoDatabase mongoDb = mongoClient.getDatabase(dbName);
		return mongoDb;
	}
	
	public void findAll(String dbCollectionName) {
		
		MongoCollection<Document> collection = getDBCollection(dbCollectionName);
		
		MongoCursor<Document> cursor = collection.find().iterator();
		
		try {
			while (cursor.hasNext()) {
				log.info(cursor.next().toJson());
			}
		} finally {
			cursor.close();
		}
	}
	

	public Set<String> projectionByAttribute(String dbCollectionName, String attribute) {
	
		Set<String> set = new HashSet<String>();
		MongoCursor<Document> cursor = projectionByAttributes(dbCollectionName, null,attribute);
		
		try {
			while (cursor.hasNext()) {
				String jsonField = cursor.next().get(attribute).toString();
				set.add(jsonField);
			}
		} finally {
			cursor.close();
		}
		
		if(log.isDebugEnabled()) {
			log.info("fetching: " + set);
		}
		
		return set;
	}
	
	public List<String> getSingleProjectionByFilterKeyValue(String dbCollectionName, String projectionFieldKey, String filterKey, String filterValue ) {
		
		List<String> list = new ArrayList<String>();
		MongoCollection<Document> collection = getDBCollection(dbCollectionName);
		MongoCursor<Document> cursor = collection.find(Filters.eq(filterKey, filterValue)).iterator();
		
		try {
			while (cursor.hasNext()) {
				String jsonField = cursor.next().get(projectionFieldKey).toString();
				list.add(jsonField);
			}
		} finally {
			cursor.close();
		}
		
		if(log.isDebugEnabled()) {
			log.info("fetching: " + list);
		}
		
		return list;
	}
	
	public MongoCursor<Document> projectionByAttributes(String dbCollectionName, Map<String, Object> filter, String... attributes) {
		
		MongoCollection<Document> collection = getDBCollection(dbCollectionName);
		
		MongoCursor<Document> cursor = null;
		
		if (filter != null) {
			Bson bsonFilter = new Document(filter);
			cursor = collection.find(bsonFilter).projection(Projections.include(attributes)).iterator();
		} else {
			cursor = collection.find().projection(Projections.include(attributes)).iterator();
		}
		
		return cursor;
	}
	
	public MongoCollection<Document> getDBCollection(String dbCollectionName) {
		
		MongoDatabase mongoDb = getDatabase();
		MongoCollection<Document> collection = mongoDb.getCollection(dbCollectionName);

		return collection;
	}

	public static void main(String[] args) {
		TwitterDaoMongo test = new TwitterDaoMongo("testdb","localhost", 27017);

		//test.getConnection();
	}
	
	public void insertTweet(String collectionName, Document document) {
		MongoCollection<Document> collection = getDBCollection(collectionName);
		collection.insertOne(document);
	}
	
	public void insertManyTweets(String collectionName, List<Document> documents) {
		MongoCollection<Document> collection = getDBCollection(collectionName);
		collection.insertMany(documents);
	}
	
	public UpdateResult updateCollectionTweets(String collectionName, Bson bsonFilter, Document document) {
		MongoCollection<Document> collection = getDBCollection(collectionName);
		UpdateResult result = collection.updateOne(bsonFilter, document);
		return result;
	}
	
	public UpdateResult updateCollectionTweets(String collectionName, Bson bsonFilter, Document document, UpdateOptions updateOptions) {
		MongoCollection<Document> collection = getDBCollection(collectionName);
		UpdateResult result = collection.updateOne(bsonFilter, document, updateOptions);
		return result;
	}

}
