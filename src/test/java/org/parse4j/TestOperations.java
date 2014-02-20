package org.parse4j;

import java.net.UnknownHostException;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;



public class TestOperations {

	public static void main(String[] args) throws ParseException, UnknownHostException {
		// TODO Auto-generated method stub
		
		String APP_ID = "RWjFpDDbwCIXF8Gy9dHEBpR7Fs2PZ0UzcNdxhAvf";
		String APP_REST_API_ID = "EWpTGoOFgGr9vXfPLBRYZjhDL0pg4MQ1F7i3wWAq";
		Parse.initialize(APP_ID, APP_REST_API_ID);
		
		MongoClient client = new MongoClient();
		DB nba = client.getDB("nba");
		DBCollection games = nba.getCollection("games");
		
		//db.games.find({}, { date: 1, losingTeam: 1, winningTeam: 1}).sort({date:-1})
		DBObject fields = new BasicDBObject();
		fields.put("date", 1);
		fields.put("losingTeam", 1);
		fields.put("winningTeam", 1);
		
		DBObject sort = new BasicDBObject();
		sort.put("date", -1);
		
		
		DBCursor cursor = games.find(new BasicDBObject(), fields).sort(sort);
		System.out.println(cursor.count());
		while(cursor.hasNext()) {
			BasicDBObject game = (BasicDBObject) cursor.next();
			ParseObject g = new ParseObject("games");
			BasicDBObject losingTeam = (BasicDBObject) game.get("losingTeam");
			BasicDBObject winningTeam = (BasicDBObject) game.get("winningTeam");
			g.put("losingTeam", losingTeam.getString("name"));
			g.put("losingScore", losingTeam.getInt("score"));
			g.put("winningTeam", winningTeam.getString("name"));
			g.put("winningScore", winningTeam.getString("score"));
			g.put("date", game.getDate("date"));
			g.save();
		}
		
		
		
	}

}
