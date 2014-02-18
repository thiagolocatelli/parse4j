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

### Graddle ###

```XML
	dependencies {
	    compile group: 'org.parse4j', name: 'parse4j', version: '1.0'
	    ...
	}
```

### Ivy ###

```XML
	<ivy-module version="2.0">
	    ...
	    <dependencies>
	        <dependency org="org.parse4j" name="parse4j" rev="1.0"/>
	        ...
	    </dependencies>
	</ivy-module>
```

Objects
-------

```Java

```

Queries
-------


Users
-----


Roles
-----


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


Cloud Functions
---------------


GeoPoints
---------

Notes
-----


TODO
-----

Pending Functionalities

 * Queries
 * Analytics
 * Push Notifications
 * Cloud Functions
 * Instalations
 
 Implemented Functionalities

 * Objects
 * Files
 * Geopoints
  
 
 