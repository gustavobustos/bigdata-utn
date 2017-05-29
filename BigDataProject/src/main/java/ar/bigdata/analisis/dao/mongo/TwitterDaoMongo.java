package ar.bigdata.analisis.dao.mongo;

import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.bigdata.analisis.dao.TwitterDao;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;

public class TwitterDaoMongo implements TwitterDao {
	
	private final Logger log = LoggerFactory.getLogger(TwitterDaoMongo.class);
	
	private MongoClient mongoClient;
	
	public TwitterDaoMongo (String host, int port) {
		this.mongoClient = new MongoClient(host, port);
	}
	
	public void findAll(String dbName, String dbCollectionName) {
		
		MongoCollection<Document> collection = getDBCollection(dbName, dbCollectionName);
		
		MongoCursor<Document> cursor = collection.find().iterator();
		
		try {
			while (cursor.hasNext()) {
				log.info(cursor.next().toJson());
			}
		} finally {
			cursor.close();
		}
	}

	public MongoCollection<Document> getDBCollection(String dbName, String dbCollectionName) {
		
		MongoDatabase mongoDb = mongoClient.getDatabase(dbName);
		MongoCollection<Document> collection = mongoDb.getCollection(dbCollectionName);

		return collection;
	}

	public static void main(String[] args) {
		TwitterDaoMongo test = new TwitterDaoMongo("localhost", 27017);

		//test.getConnection();
	}
	
	public void insertTweet(String dbName, String collectionName, Document document) {
		MongoCollection<Document> collection = getDBCollection(dbName, collectionName);
		collection.insertOne(document);
	}
	
	public void insertManyTweets(String dbName, String collectionName, List<Document> documents) {
		MongoCollection<Document> collection = getDBCollection(dbName, collectionName);
		collection.insertMany(documents);
	}
	
	public UpdateResult updateCollectionTweets(String dbName, String collectionName, Bson bsonFilter, Document document) {
		MongoCollection<Document> collection = getDBCollection(dbName, collectionName);
		UpdateResult result = collection.updateOne(bsonFilter, document);
		return result;
	}

}
