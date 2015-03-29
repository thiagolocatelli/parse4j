Parse4J - Java Library for [Parse](https://parse.com)
====================================

Ths library is a non-official java library, but it works according to Parse's REST api specification.

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



#### Maven

```XML
<project ...>
    ...
    <dependencies>
        <dependency>
            <groupId>com.github.thiagolocatelli</groupId>
            <artifactId>parse4j</artifactId>
            <version>1.4</version>
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
			System.out.println("saveInBackground(): objectId: " + gameScore.getObjectId());
			System.out.println("saveInBackground(): createdAt: " + gameScore.getCreatedAt());
			System.out.println("saveInBackground(): updatedAt: " + gameScore.getUpdatedAt());
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

#### Batch operations
To reduce the amount of time spent on network round trips, you can create, update, or delete up to 50 objects in one call. The response from batch will be a list with the same number of elements as the input list. Each item in the list with be a dictionary with either the success or error field set. 
```
//insert objects
ParseBatch batcher = new ParseBatch();
final ParseObject gameScore = new ParseObject("GameScore");
final ParseObject anotherGameScore = new ParseObject("GameScore");
batcher.createObject(gameScore);
batcher.createObject(anotherGameScore);
//execute batch request!
JSONArray batch = batcher.batch();
//in this way you will save two objects with one http request

//update objects
ParseBatch batcher = new ParseBatch();
ParseObject gameScore = ParseObject.createWithoutData("GameScore", objectId);
//execute batch request!
JSONArray batch = batcher.batch();

//delete objects
ParseBatch batcher = new ParseBatch();
ParseObject gameScore = ParseObject.createWithoutData("GameScore", objectId);
batcher.deleteObject(gameScore);
//execute batch request!
JSONArray batch = batcher.batch();
```
Format of the JSONArray is:
 
```Json
[{"success":{"objectId":"xxxxxxxx","createdAt":"2015-03-28T10:34:55.564Z"}}]
```

You can easily iterate over returned JSONArray to check if batch operation for every
object is executed successfully or not : 
```Java
		for(int i=0;i<batch.length();i++){
			JSONObject current = batch.getJSONObject(i);
			if(current.get("success")!=null){
				JSONObject obj = current.getJSONObject("success");
				LOGGER.info("status is success ");
			}else{
				LOGGER.info("status is error ");
			}
		}

```
Of course you can mix add, update and delete batch operations.
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

#### Relational data

Objects can have relationships with other objects. To model this behavior, any **ParseObject** can be used as a value in other **ParseObjects**. Internally, the Parse framework will store the referred-to object in just one place, to maintain consistency.

For example, each Comment in a blogging app might correspond to one Post. To create a new Post with a single Comment, you could write:

```Java
// Create the post
ParseObject myPost = new ParseObject("Post");
myPost.put("title", "I'm Hungry");
myPost.put("content", "Where should we go for lunch?");
myPost.save();

// Create the comment
ParseObject myComment = new ParseObject("Comment");
myComment.put("content", "Let's do Sushirrito.");
myComment.put("parent", myPost); // Add a relation between the Post and Comment
myComment.saveInBackground(); // This will save both myPost and myComment
```

You can also link objects using just their objectIds like so:

```Java
// Add a relation between the Post with objectId "1zEcyElZ80" and the comment
myComment.put("parent", ParseObject.createWithoutData("Post", "1zEcyElZ80"));
```

By default, when fetching an object, related **ParseObjects** are not fetched. These objects' values cannot be retrieved until they have been fetched like so:

```Java
fetchedComment.getParseObject("parent")
    .fetchIfNeededInBackground(new GetCallback<ParseObject>() {
        public void done(ParseObject post, ParseException e) {
          String title = post.getString("title");
        }
    });
```


<a name="Subclasses"></a>
Subclasses
-------

Parse is designed to get you up and running as quickly as possible. You can access all of your data using the **ParseObject** class and access any field with get(). In mature codebases, subclasses have many advantages, including terseness, extensibility, and support for autocomplete. Subclassing is completely optional, but can transform this code:

```Java
ParseObject shield = new ParseObject("Armor");
shield.put("displayName", "Wooden Shield");
shield.put("fireproof", false);
shield.put("rupees", 50);
```
Into this:

```Java
Armor shield = new Armor();
shield.setDisplayName("Wooden Shield");
shield.setFireproof(false);
shield.setRupees(50);
```

#### Subclassing ParseObject

To create a ParseObject subclass:

 1. Declare a subclass which extends **ParseObject**.
 2. Add a **@ParseClassName** annotation. Its value should be the string you would pass into the **ParseObject** constructor, and makes all future class name references unnecessary.
 3. Ensure that your subclass has a public default (i.e. zero-argument) constructor. You must not modify any **ParseObject** fields in this constructor.
 4. Call **ParseRegistry.registerSubclass(YourClass.class)** in your Application constructor before calling **Parse.initialize()**.

The following code sucessfully implements and registers the Armor subclass of **ParseObject**:

```Java
// Armor.java
import com.parse.ParseObject;
import com.parse.ParseClassName;
 
@ParseClassName("Armor")
public class Armor extends ParseObject {
}
 
// App.java
import com.parse.Parse;
import android.app.Application;
 
public class App extends Application {
	@Override
	public void onCreate() {
    	super.onCreate();
 
    	ParseRegistry.registerSubclass(Armor.class);
    	Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
  }
}
```

#### Accessors, Mutators, and Methods

Adding methods to your **ParseObject** subclass helps encapsulate logic about the class. You can keep all your logic about a subject in one place rather than using separate classes for business logic and storage/transmission logic.

You can add accessors and mutators for the fields of your **ParseObject** easily. Declare the getter and setter for the field as you normally would, but implement them in terms of **get()** and **put()**. The following example creates a displayName field in the Armor class:

```Java
// Armor.java
@ParseClassName("Armor")
public class Armor extends ParseObject {
	public String getDisplayName() {
		return getString("displayName");
	}
	
	public void setDisplayName(String value) {
		put("displayName", value);
  	}
}
```

You can now access the displayName field using **armor.getDisplayName()** and assign to it using **armor.setDisplayName("Wooden Sword")**. This allows your IDE to provide autocompletion as you develop your app and allows typos to be caught at compile-time.

Accessors and mutators of various types can be easily defined in this manner using the various forms of **get()** such as **getInt()**, **getParseFile()**, or **getMap()**.

If you need more complicated logic than simple field access, you can declare your own methods as well:

```Java
public void takeDamage(int amount) {
  // Decrease the armor's durability and determine whether it has broken
  increment("durability", -amount);
  if (getDurability() < 0) {
    setBroken(true);
  }
}
```

#### Initializing Subclasses

You should create new instances of your subclasses using the constructors you have defined. Your subclass must define a public default constructor that does not modify fields of the **ParseObject**, which will be used throughout the Parse SDK to create strongly-typed instances of your subclass.

To create a reference to an existing object, use **ParseObject.createWithoutData()**:

```Java
Armor armorReference = ParseObject.createWithoutData(Armor.class, armor.getObjectId());
```

#### Queries

You can get a query for objects of a particular subclass using the static method **ParseQuery.getQuery()**. The following example queries for armors that the user can afford:

```Java
ParseQuery<Armor> query = ParseQuery.getQuery(Armor.class);
query.whereLessThanOrEqualTo("rupees", ParseUser.getCurrentUser().get("rupees"));
query.findInBackground(new FindCallback<Armor>() {
  @Override
  public void done(List<Armor> results, ParseException e) {
    for (Armor a : results) {
      // ...
    }
  }
});
```

<a name="Queries"></a>
Queries
-------

#### Basic Queries

In many cases, **getInBackground** isn't powerful enough to specify which objects you want to retrieve. The **ParseQuery** offers different ways to retrieve a list of objects rather than just a single object.

The general pattern is to create a **ParseQuery**, put conditions on it, and then retrieve a List of matching **ParseObjects** using the **findInBackground** method with a **FindCallback**. For example, to retrieve scores with a particular playerName, use the whereEqualTo method to constrain the value for a key:

```Java
ParseQuery<ParseObject> query = ParseQuery.getQuery("GameScore");
query.whereEqualTo("playerName", "Dan Stemkoski");
query.findInBackground(new FindCallback<ParseObject>() {
    public void done(List<ParseObject> scoreList, ParseException e) {
        if (e == null) {
            Log.d("score", "Retrieved " + scoreList.size() + " scores");
        } else {
            Log.d("score", "Error: " + e.getMessage());
        }
    }
});
```

**findInBackground** works similarly to **getInBackground** in that it assures the network request is done on a background thread, and runs its callback in the main thread.

#### Query Constraints

There are several ways to put constraints on the objects found by a **ParseQuery**. You can filter out objects with a particular key-value pair with **whereNotEqualTo**:

```Java
query.whereNotEqualTo("playerName", "Michael Yabuti");
```

You can give multiple constraints, and objects will only be in the results if they match all of the constraints. In other words, it's like an AND of constraints.

```Java
query.whereNotEqualTo("playerName", "Michael Yabuti");
query.whereGreaterThan("playerAge", 18);
```

You can limit the number of results with **limit**. By default, results are limited to 100, but anything from 1 to 1000 is a valid limit:

```Java
query.limit(10); // limit to at most 10 results
```

If you want exactly one result, a more convenient alternative may be to use getFirst or **getFirstBackground** instead of using find.

```Java
ParseQuery<ParseObject> query = ParseQuery.getQuery("GameScore");
query.whereEqualTo("playerEmail", "dstemkoski@example.com");
query.getFirstInBackground(new GetCallback<ParseObject>() {
  public void done(ParseObject object, ParseException e) {
    if (object == null) {
      Log.d("score", "The getFirst request failed.");
    } else {
      Log.d("score", "Retrieved the object.");
    }
  }
});
```

You can skip the first results with **skip**. This can be useful for pagination:

```Java
query.skip(10); // skip the first 10 results
```

For sortable types like numbers and strings, you can control the order in which results are returned:

```Java
// Sorts the results in ascending order by the score field
query.orderByAscending("score");
 
// Sorts the results in descending order by the score field
query.orderByDescending("score");
```

You can add more sort keys to the query as follows:

```Java
// Sorts the results in ascending order by the score field if the previous sort keys are equal.
query.addAscendingOrder("score");
 
// Sorts the results in descending order by the score field if the previous sort keys are equal.
query.addDescendingOrder("score");
```

For sortable types, you can also use comparisons in queries:

```Java
// Restricts to wins < 50
query.whereLessThan("wins", 50);
 
// Restricts to wins <= 50
query.whereLessThanOrEqualTo("wins", 50);
 
// Restricts to wins > 50
query.whereGreaterThan("wins", 50);
 
// Restricts to wins >= 50
query.whereGreaterThanOrEqualTo("wins", 50);
```

If you want to retrieve objects matching several different values, you can use **whereContainedIn**, providing a collection of acceptable values. This is often useful to replace multiple queries with a single query. For example, if you want to retrieve scores made by any player in a particular list:

```Java
String[] names = {"Jonathan Walsh", "Dario Wunsch", "Shawn Simon"};
query.whereContainedIn("playerName", Arrays.asList(names));
```

If you want to retrieve objects that do not match any of several values you can use whereKey:notContainedIn:, providing an array of acceptable values. For example, if you want to retrieve scores from players besides those in a list:

```Java
String[] names = {"Jonathan Walsh", "Dario Wunsch", "Shawn Simon"};
query.whereNotContainedIn("playerName", Arrays.asList(names));
```

If you want to retrieve objects that have a particular key set, you can use whereExists. Conversely, if you want to retrieve objects without a particular key set, you can use whereDoesNotExist.

```Java
// Finds objects that have the score set
query.whereExists("score");
 
// Finds objects that don't have the score set
query.whereDoesNotExist("score");
```

You can use the **whereMatchesKeyInQuery** method to get objects where a key matches the value of a key in a set of objects resulting from another query. For example, if you have a class containing sports teams and you store a user's hometown in the user class, you can issue one query to find the list of users whose hometown teams have winning records. The query would look like:

```Java
ParseQuery<ParseObject> teamQuery = ParseQuery.getQuery("Team");
teamQuery.whereGreaterThan("winPct", 0.5);
ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
userQuery.whereMatchesKeyInQuery("hometown", "city", teamQuery);
userQuery.findInBackground(new FindCallback<ParseUser>() {
  void done(List<ParseUser> results, ParseException e) {
    // results has the list of users with a hometown team with a winning record
  }
});
```

Conversely, to get objects where a key does not match the value of a key in a set of objects resulting from another query, use **whereDoesNotMatchKeyInQuery**. For example, to find users whose hometown teams have losing records:

```Java
ParseQuery<ParseUser> losingUserQuery = ParseUser.getQuery();
losingUserQuery.whereDoesNotMatchKeyInQuery("hometown", "city", teamQuery);
losingUserQuery.findInBackground(new FindCallback<ParseUser>() {
  void done(List<ParseUser> results, ParseException e) {
    // results has the list of users with a hometown team with a losing record
  }
});
```

You can restrict the fields returned by calling **selectKeys** with a collection of keys. To retrieve documents that contain only the score and playerName fields (and also special built-in fields such as objectId, createdAt, and updatedAt):

```Java
ParseQuery<ParseObject> query = ParseQuery.getQuery("GameScore");
query.selectKeys(Arrays.asList("playerName", "score"));
List<ParseObject> results = query.find();
```

The remaining fields can be fetched later by calling one of the **fetchIfNeeded** variants on the returned objects:

```Java
ParseObject object = results.get(0);
object.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
  public void done(ParseObject object, ParseException e) {
    // all fields of the object will now be available here.
  }
});
```

#### Queries on Array Values

If a key contains an array value, you can search for objects where the key's array value contains 2 by:

```Java
// Find objects where the array in arrayKey contains the number 2.
query.whereEqualTo("arrayKey", 2);
```

You can also search for objects where the key's array value contains each of the values 2, 3, and 4 with the following:

```Java
// Find objects where the array in arrayKey contains all of the numbers 2, 3, and 4.
ArrayList<Integer> numbers = new ArrayList<Integer>();
numbers.add(2);
numbers.add(3);
numbers.add(4);
query.whereContainsAll("arrayKey", numbers);
```

#### Queries on String Values

Use **whereStartsWith** to restrict to string values that start with a particular string. Similar to a MySQL LIKE operator, this is indexed so it is efficient for large datasets:

```Java
// Finds barbecue sauces that start with "Big Daddy's".
ParseQuery<ParseObject> query = ParseQuery.getQuery("BarbecueSauce");
query.whereStartsWith("name", "Big Daddy's");
```

#### Relational Queries


Pending...

#### Counting Objects

If you just need to count how many objects match a query, but you do not need to retrieve all the objects that match, you can use count instead of find. For example, to count how many games have been played by a particular player:

```Java
ParseQuery<ParseObject> query = ParseQuery.getQuery("GameScore");
query.whereEqualTo("playerName", "Sean Plott");
query.countInBackground(new CountCallback() {
  public void done(int count, ParseException e) {
    if (e == null) {
      // The count request succeeded. Log the count
      Log.d("score", "Sean has played " + count + " games");
    } else {
      // The request failed
    }
  }
});
```

If you want to block the calling thread, you can also use the synchronous **query.count()** method.

For classes with over 1000 objects, count operations are limited by timeouts. They may routinely yield timeout errors or return results that are only approximately correct. Thus, it is preferable to architect your application to avoid this sort of count operation.


#### Compound Queries

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

Enabling email verification in an application's settings allows the application to reserve part of its experience for users with confirmed email addresses. Email verification adds the emailVerified key to the **ParseUser** object. When a **ParseUser**'s email is set or modified, emailVerified is set to false. Parse then emails the user a link which will set emailVerified to true.

There are three emailVerified states to consider:

* true - the user confirmed his or her email address by clicking on the link Parse emailed them. **ParseUsers** can never have a true value when the user account is first created.
* false - at the time the **ParseUser** object was last fetched, the user had not confirmed his or her email address. If emailVerified is false, consider calling fetch() on the **ParseUser**.
* missing - the **ParseUser** was created when email verification was off or the ParseUser does not have an email.

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

Parse provides a number of hooks for you to get a glimpse into the ticking heart of your app. We understand that it's important to understand what your app is doing, how frequently, and when.

While this section will cover different ways to instrument your app to best take advantage of Parse's analytics backend, developers using Parse to store and retrieve data can already take advantage of metrics on Parse.

Without having to implement any client-side logic, you can view real-time graphs and breakdowns (by device type, Parse class name, or REST verb) of your API Requests in your app's dashboard and save these graph filters to quickly access just the data you're interested in.


#### App-Open / Push Analytics

Our initial analytics hook allows you to track your application being launched. By adding the following line to the onCreate method of your main Activity, you'll begin to collect data on when and how often your application is opened.

```JAVA
ParseAnalytics.trackAppOpened();
```

Graphs and breakdowns of your statistics are accessible from your app's Dashboard.

#### Custom Analytics

**ParseAnalytics** also allows you to track free-form events, with a handful of String keys and values. These extra dimensions allow segmentation of your custom events via your app's Dashboard.

Say your app offers search functionality for apartment listings, and you want to track how often the feature is used, with some additional metadata.

```JAVA
Map<String, String> dimensions = new HashMap<String, String>();
// Define ranges to bucket data points into meaningful segments
dimensions.put("priceRange", "1000-1500");
// Did the user filter the query?
dimensions.put("source", "craigslist");
// Do searches happen more often on weekdays or weekends?
dimensions.put("dayType", "weekday");
// Send the dimensions to Parse along with the 'search' event
ParseAnalytics.trackEvent("search", dimensions);
```

**ParseAnalytics** can even be used as a lightweight error tracker — simply invoke the following and you'll have access to an overview of the rate and frequency of errors, broken down by error code, in your application:

```JAVA
Map<String, String> dimensions = new HashMap<String, String>();
dimensions.put("code", Integer.toString(error.getCode()));
ParseAnalytics.trackEvent("error", dimensions);
```

Note that Parse currently only stores the first eight dimension pairs per call to **ParseAnalytics.trackEvent()**.

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
query.limit(10);
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
 * [Subclasses](#Subclasses) 
 * [Queries](#Queries) 
 * [CloudFunctions](#CloudFunctions) 
 * [GeoPoints](#GeoPoints)
 * [Analytics](#Analytics) 
 * [Files](#Files) 

 
### In development
 
 * [Users](#Users) 
 * [Push Notifications](#PushNotifications) 

### Pending Functionalities

 * [ACL](#Objects)  
 * [Roles](#Roles) 
 * [Instalations](#Instalations) 
 

  
 
 