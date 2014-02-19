Parse4J - Java Library for parse.com
====================================

Summary
-------


Getting Started
---------------

### Maven ###

```XML
	<project ...>
	    ...
	    <dependencies>
	        <dependency>
	            <groupId>org.parse4j</groupId>
	            <artifactId>parse4j</artifactId>
	            <version>1.0</version>
	        </dependency>
	    </dependencies>
	    ...
	</project>
```


Objects
-------

```Java
	ParseObject gameScore = new ParseObject("GameScore");
	gameScore.put("score", 1337);
	gameScore.put("playerName", "Sean Plott");
	gameScore.put("cheatMode", false);
	gameScore.saveInBackground();
```

To get the values out of the ParseObject, there's a getX method for each data type:

```Java
	int score = gameScore.getInt("score");
	String playerName = gameScore.getString("playerName");
	boolean cheatMode = gameScore.getBoolean("cheatMode");
```

If you don't know what type of data you're getting out, you can call get(key), but then you probably have to cast it right away anyways. In most situations you should use the typed accessors like getString.

The three special values have their own accessors:

```Java
	String objectId = gameScore.getObjectId();
	Date updatedAt = gameScore.getUpdatedAt();
	Date createdAt = gameScore.getCreatedAt();
```


```Java
	ParseObject gameScore = new ParseObject("GameScore");
	gameScore.put("score", 1337);
	gameScore.put("playerName", "Sean Plott");
	gameScore.put("cheatMode", false);
	gameScore.saveInBackground();
```

```Java
	ParseObject gameScore = new ParseObject("GameScore");
	gameScore.put("score", 1337);
	gameScore.put("playerName", "Sean Plott");
	gameScore.put("cheatMode", false);
	gameScore.saveInBackground();
```

Queries
-------

Pending...

Users
-----

In development...

Roles
-----

Pending...


Files
-----

```JAVA
	byte[] data = "Working at Parse is great!".getBytes();
	ParseFile file = new ParseFile("resume.txt", data);
	file.save();
```


```JAVA
	byte[] data = getBytes("song.mp3");
	ParseFile file = new ParseFile("song.mp3", data);
	file.save(new ProgressCallback() {
		
		@Override
		public void done(Integer percentDone) {
			System.out.println("uploadPdf(): progress " + percentDone + "%");
		}
	});
```


```JAVA
	byte[] data = getBytes("song.mp3");
	ParseFile file = new ParseFile("song.mp3", data);
	file.save(new SaveCallback() {
		
		@Override
		public void done(ParseException parseException) {
			System.out.println(parseException);
		}
	});
```


Analytics
---------

Pending ...

Cloud Functions
---------------

In development...

GeoPoints
---------



Notes
-----

### License


TODO
-----

### Pending Functionalities

 * Queries
 * Analytics
 * Push Notifications
 * Cloud Functions
 * Instalations
 
### Implemented Functionalities

 * Objects
 * Files
 * Geopoints
  
 
 