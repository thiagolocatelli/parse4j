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
<ivy-module version="2.0">
    ...
    <dependencies>
        <dependency org="org.parse4j" name="parse4j" rev="1.0"/>
        ...
    </dependencies>
</ivy-module>
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
testParseFile(file);
```

Analytics
---------


Cloud Functions
---------------


GeoPoints
---------

Notes
-----