Parse4J - Java Library for parse.com
====================================

The non-official java library for <font color='red'>[Parse](https://parse.com)</font>

ps.: most of the following code snippets and text have been extracted from the parse website, since the java library mimicks the android library. I am not a Parse/Facebook employee.

Summary
-------

The Parse platform provides a complete backend solution for your mobile application. Our goal is to totally eliminate the need for writing server code or maintaining servers.

If you're familiar with web frameworks like Ruby on Rails, we've taken many of the same principles and applied them to our platform. In particular, our SDK is ready to use out of the box with minimal configuration on your part.

#### Apps

On Parse, you create an App for each of your applications. Each App has its own application id and client key that you apply to your application, like the code below. Your account on Parse can accommodate multiple Apps. This is useful even if you have one application, since you can deploy different versions for test and production.

```Java
	Parse.initialize(APP_ID, APP_REST_API_ID);
```

Getting Started
---------------

#### Download the library manually



#### Maven (pending sonatype request)

```XML
	<project ...>
	    ...
	    <dependencies>
	        <dependency>
	            <groupId>github.com.thiagolocatelli</groupId>
	            <artifactId>parse4j</artifactId>
	            <version>1.0</version>
	        </dependency>
	    </dependencies>
	    ...
	</project>
```

<a name="Objects"></a>
Objects
-------

Storing data on Parse is built around the ParseObject. Each **ParseObject** contains key-value pairs of JSON-compatible data. This data is schemaless, which means that you don't need to specify ahead of time what keys exist on each **ParseObject**. You simply set whatever key-value pairs you want, and our backend will store it.

For example, let's say you're tracking high scores for a game. A single ParseObject could contain:

```Java
	score: 1337, playerName: "Sean Plott", cheatMode: false 
```

Keys must be alphanumeric strings. Values can be strings, numbers, booleans, or even arrays and objects - anything that can be JSON-encoded.

Each ParseObject has a class name that you can use to distinguish different sorts of data. For example, we could call the high score object a GameScore. Parse recommend that you NameYourClassesLikeThis and nameYourKeysLikeThis, just to keep your code looking pretty.

#### Saving Objects

```Java
	ParseObject gameScore = new ParseObject("GameScore");
	gameScore.put("score", 1337);
	gameScore.put("playerName", "Sean Plott");
	gameScore.put("cheatMode", false);
	gameScore.save();
```

Use saveInBackground() if you want to delegate the operation to a background thread

```Java
	gameScore.saveInBackground();  
```

#### Retrieving Objects

```Java
	ParseQuery<ParseObject> query = ParseQuery.getQuery("GameScore");
	query.getInBackground("xWMyZ4YEGZ", new GetCallback<ParseObject>() {
	  public void done(ParseObject object, ParseException e) {
	    if (e == null) {
	      // object will be your game score
	    } else {
	      // something went wrong
	    }
	  }
	}); 
```


To get the values out of the **ParseObject**, there's a getX method for each data type:

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

#### Callback functions

You can also add a callback function to the save operation

```Java
	final ParseObject gameScore = new ParseObject("GameScore");
	gameScore.put("score", 1337);
	gameScore.put("playerName", "Sean Plott");
	gameScore.put("cheatMode", false);
	gameScore.saveInBackground(new SaveCallback() {			
			@Override
			public void done(ParseException parseException) {
				System.out.println("saveInBackground(): objectId: " + parseObject.getObjectId());
				System.out.println("saveInBackground(): createdAt: " + parseObject.getCreatedAt());
				System.out.println("saveInBackground(): updatedAt: " + parseObject.getUpdatedAt());
			}
		});
```

#### Delete

To delete an object, just call the method delete().

```Java
	gameScore.delete();
```
You can also attach a callback function on the delete/deleteInBackground methods.

```Java
	gameScore.deleteInBackground(new DeleteCallback() {
			@Override
			public void done(ParseException parseException) {
				//do something
			}
		});
```

#### Counters

You can also increment and decrement values from Number (int, long, double...) attributes.

```Java
	gameScore.increment("score");
	gameScore.decrement("score");
	gameScore.saveInBackground();
```
You can also increment by any amount using increment(key, amount) or decrement by any amount using decrement(key, amount).

#### Arrays

```Java
gameScore.addAllUnique("skills", Arrays.asList("flying", "kungfu"));
gameScore.saveInBackground();
```

<a name="Queries"></a>
Queries
-------

Pending...

<a name="Users"></a>
Users
-----

At the core of many applications, there is a notion of user accounts that lets users access their information in a secure manner. Parse provides a specialized user class called ParseUser that automatically handles much of the functionality required for user account management.

With this class, you'll be able to add user account functionality in your application.

**ParseUser** is a subclass of the **ParseObject**, and has all the same features, such as flexible schema, automatic persistence, and a key value interface. All the methods that are on **ParseObject** also exist in ParseUser. The difference is that **ParseUser** has some special additions specific to user accounts.

#### Properties

**ParseUser** has several properties that set it apart from **ParseObject**:

* username: The username for the user (required).
* password: The password for the user (required on signup).
* email: The email address for the user (optional).

We'll go through each of these in detail as we run through the various use cases for users. Keep in mind that if you set username and email using the setters, you do not need to set it using the put method.

#### Signing Up

The first thing your app will do is probably ask the user to sign up. The following code illustrates a typical sign up:

```Java
	ParseUser user = new ParseUser();
	user.setUsername("my name");
	user.setPassword("my pass");
	user.setEmail("email@example.com");
	 
	// other fields can be set just like with ParseObject
	user.put("phone", "650-253-0000");
	user.signUp();
```

This call will synchronously create a new user in your Parse application. Before it does this, it checks to make sure that both the username and email are unique. Also, it securely hashes the password in the cloud. Parse never stores passwords in plaintext, nor will Parse ever transmit passwords back to the client in plaintext.

You can also use signUpInBackground method, but never saveInBackground method. New ParseUsers should always be created using the signUpInBackground (or signUp) method. Subsequent updates to a user can be done by calling save.

If a signup isn't successful, you should read the error object that is returned. The most likely case is that the username or email has already been taken by another user. You should clearly communicate this to your users, and ask them try a different username.

You are free to use an email addresses as the username. Simply ask your users to enter their email, but fill it in the username property — ParseUser will work as normal. We'll go over how this is handled in the reset password section.

#### Logging In

Of course, after you allow users to sign up, you need be able to let them log in to their account in the future. To do this, you can use the class methods logInInBackground or logIn.

```Java
	ParseUser.logInInBackground("Jerry", "showmethemoney", new LogInCallback() {
	  public void done(ParseUser user, ParseException e) {
	    if (user != null) {
	      // Hooray! The user is logged in.
	    } else {
	      // Signup failed. Look at the ParseException to see what happened.
	    }
	  }
	});
```

#### Verifying Emails

#### Anonymous User

pending...

#### Security For User Objects

pending...

#### Security for Other Objects

pending...

#### Resetting Passwords

It's a fact that as soon as you introduce passwords into a system, users will forget them. In such cases, our library provides a way to let them securely reset their password.

To kick off the password reset flow, ask the user for their email address, and call:

```Java
	ParseUser.requestPasswordReset("some@email.com");
```

This will attempt to match the given email with the user's email or username field, and will send them a password reset email. By doing this, you can opt to have users use their email as their username, or you can collect it separately and store it in the email field.

The flow for password reset is as follows:

 1. User requests that their password be reset by typing in their email.
 2. Parse sends an email to their address, with a special password reset link.
 3. User clicks on the reset link, and is directed to a special Parse page that will allow them type in a new password.
 4. User types in a new password. Their password has now been reset to a value they specify.

Note that the messaging in this flow will reference your application by the name that you specified when you created this appplication on Parse.


<a name="Roles"></a>
Roles
-----

Pending...


<a name="Files"></a>
Files
-----

**ParseFile** lets you store application files in the cloud that would otherwise be too large or cumbersome to fit into a regular **ParseObject**. The most common use case is storing images but you can also use it for documents, videos, music, and any other binary data (up to 10 megabytes).

Getting started with **ParseFile** is easy. First, you'll need to have the data in byte[] form and then create a ParseFile with it. In this example, we'll just use a string:

```JAVA
	byte[] data = "Working at Parse is great!".getBytes();
	ParseFile file = new ParseFile("resume.txt", data);
	file.save();
```

Notice in this example that we give the file a name of resume.txt. There's two things to note here:

* You don't need to worry about filename collisions. Each upload gets a unique identifier so there's no problem with uploading multiple files named resume.txt.
* It's important that you give a name to the file that has a file extension. This lets Parse figure out the file type and handle it accordingly. So, if you're storing PNG images, make sure your filename ends with .png.

Next you'll want to save the file up to the cloud. As with ParseObject, there are many variants of the save method you can use depending on what sort of callback and error handling suits you.

```JAVA
	file.saveInBackground();
```
Finally, after the save completes, you can associate a ParseFile onto a ParseObject just like any other piece of data:

```JAVA
	ParseObject jobApplication = new ParseObject("JobApplication");
	jobApplication.put("applicantName", "Joe Smith");
	jobApplication.put("applicantResumeFile", file);
	jobApplication.save();
```

Retrieving it back involves calling one of the getData variants on the ParseObject. Here we retrieve the resume file off another JobApplication object:

```JAVA
	ParseFile applicantResume = (ParseFile) anotherApplication.get("applicantResumeFile");
	applicantResume.getDataInBackground(new GetDataCallback() {
	  public void done(byte[] data, ParseException e) {
	    if (e == null) {
	      // data has the bytes for the resume
	    } else {
	      // something went wrong
	    }
	  }
	});
```

#### Progress

It's easy to get the progress of both uploads and downloads using **ParseFile** by passing a ProgressCallback to saveInBackground and getDataInBackground. For example:

```JAVA
	byte[] data = getBytes("song.mp3");
	ParseFile file = new ParseFile("song.mp3", data);
	file.save(new ProgressCallback() {
		
		@Override
		public void done(Integer percentDone) {
			//do something
		}
	});
```

or:

```JAVA
	byte[] data = getBytes("song.mp3");
	ParseFile file = new ParseFile("song.mp3", data);
	file.save(new SaveCallback() {
		
		@Override
		public void done(ParseException parseException) {
			//do something
		}
	});
```

There also another possibility to save the file and give both callbacks at one.

<a name="Analytics"></a>
Analytics
---------

Pending ...

<a name="CloudFunctions"></a>
Cloud Functions
---------------

Cloud Functions can be called from Android using **ParseCloud**. For example, to call the Cloud Function named "Multiply", defined below:

```JAVASCRIPT
	Parse.Cloud.define("Multiply", function(request, response) {
	  response.success(request.params.A * request.params.B);
	});
```

and to run the code in the cloud:

```JAVA
	HashMap<String, Integer> params = new HashMap<String, Integer>();
	params.put("A", 12);
	params.put("B", 4);
	Integer result = ParseCloud.callFunction("Multiply", params);
```

Take a look at the [Cloud Code Guide](https://parse.com/docs/rest#cloudfunctions) to learn more about Cloud Functions.

<a name="GeoPoints"></a>
GeoPoints
---------

Parse allows you to associate real-world latitude and longitude coordinates with an object. Adding a **ParseGeoPoint** to a ParseObject allows queries to take into account the proximity of an object to a reference point. This allows you to easily do things like find out what user is closest to another user or which places are closest to a user.

#### ParseGeoPoint

To associate a point with an object you first need to create a **ParseGeoPoint**. For example, to create a point with latitude of 40.0 degrees and -30.0 degrees longitude:

```JAVA
	ParseGeoPoint point = new ParseGeoPoint(40.0, -30.0);
```

This point is then stored in the object as a regular field.

```JAVA
	placeObject.put("location", point);
```

#### Geo Queries

Now that you have a bunch of objects with spatial coordinates, it would be nice to find out which objects are closest to a point. This can be done by adding another restriction to **ParseQuery** using whereNear. Getting a list of ten places that are closest to a user may look something like:

```JAVA
	ParseGeoPoint userLocation = (ParseGeoPoint) userObject.get("location");
	ParseQuery<ParseObject> query = ParseQuery.getQuery("PlaceObject");
	query.whereNear("location", userLocation);
	query.setLimit(10);
	query.findInBackground(new FindCallback<ParseObject>() { ... });
```

At this point nearPlaces will be an array of objects ordered by distance (nearest to farthest) from userLocation. Note that if an additional orderByAscending()/orderByDescending() constraint is applied, it will take precedence over the distance ordering.

To limit the results using distance, check out whereWithinKilometers, whereWithinMiles, and whereWithinRadians.

It's also possible to query for the set of objects that are contained within a particular area. To find the objects in a rectangular bounding box, add the whereWithinGeoBox restriction to your **ParseQuery**.

```JAVA
	ParseGeoPoint southwestOfSF = new ParseGeoPoint(37.708813, -122.526398);
	ParseGeoPoint northeastOfSF = new ParseGeoPoint(37.822802, -122.373962);
	ParseQuery<ParseObject> query = ParseQuery.getQuery("PizzaPlaceObject");
	query.whereWithinGeoBox("location", southwestOfSF, northeastOfSF);
	query.findInBackground(new FindCallback<ParseObject>() { ... });
```

#### Caveats

At the moment there are a couple of things to watch out for:

* Each **ParseObject** class may only have one key with a **ParseGeoPoint** object. The last one added is the one that is gonna be saved to the backend.
* Points should not equal or exceed the extreme ends of the ranges. Latitude should not be -90.0 or 90.0. Longitude should not be -180.0 or 180.0. Attempting to set latitude or longitude out of bounds will cause an error.

<a name="PushNotifications"></a>
Push Notifications
---------

Pending...

<a name="Instalations"></a>
Instalations
---------

Pending...

Notes
-----

### License


TODO
-----

### Implemented Functionalities

 * [Objects](#Objects)
 * [CloudFunctions](#CloudFunctions) 
 * [GeoPoints](#GeoPoints)

 
### In development

 * [Files](#Files) 
 * [Queries](#Queries)  
 * [Users](#Users) 

### Pending Functionalities

 * [ACL](#Objects)  
 * [Roles](#Roles) 
 * [Analytics](#Analytics) 
 * [Push Notifications](#PushNotifications) 
 * [Instalations](#Instalations) 
 

  
 
 