package ar.bigdata.analisis.dao.mongo;

import org.bson.Document;

import ar.bigdata.analisis.dao.TwitterDao;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class TwitterDaoMongo implements TwitterDao {
	
	private MongoClient mongoClient;
	
	public TwitterDaoMongo () {
		this.mongoClient = new MongoClient("localhost", 27017);
	}
	
	public void findAll(String dbName, String dbCollectionName) {
		
		MongoCollection<Document> collection = getDBCollection(dbName, dbCollectionName);
		
		MongoCursor<Document> cursor = collection.find().iterator();
		
		try {
			while (cursor.hasNext()) {
				System.out.println(cursor.next().toJson());
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
		TwitterDaoMongo test = new TwitterDaoMongo();

		//test.getConnection();
	}
	
	public void insertCollectionTweets(String dbName, String collectionName, Document document) {
		MongoCollection<Document> collection = getDBCollection(dbName, collectionName);
		collection.insertOne(document);
	}

}
