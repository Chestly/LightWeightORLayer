# LightWeightORLayer
A Light weight OR layer for all of your small projects!


## Guide
- [Getting started](#getting-started)
- [ORLayer Utilities](#orlayer-utilities)
  * [Creating](#creating)
  * [Reading](#reading)
  * [Updating](#updating)
  * [Deleting](#deleting)
- [DataBase Utilities](#database-utilities)
  * [DataBase creation](#database-creation)
  * [Table Creation](#table-creation)
    - [From a Class](#from-a-class)
    - [From resources](#from-resources)
 - [Data Structures](#data-structures)
 - [Relationships](#relationships)
 - [Whole System Example](#whole-system-example)
 - [Speed Tests](#speed-tests)
 - [Compatibility](#compatibility)
 
## Getting Started

Currently not build is available. Instead you can clone this repo(The Main branch is suggested) and install it via Maven.

## ORLayer Utilities

To configure the `ORLayerUtils` do the following
```java
final ORLayerUtils util = ORSetup.configureORLayerUtils("YOUR URL", "YOUR PASSWORD", "YOUR DB USERNAME");
```


### Creating

Creating information is a giant part of using a ORLayer, with the `LightWeigherORLayer` this is extremely simple
```java
//Variable `utils` defined above
utils.create(YOUR_OBJECT);
```

### Reading

Likewise reading information from your database can be accomplished in about the same way.

```java
//Variable `utils` defined above
final YOUR_OBJECT example = utils.query(YOUR_OBJECT.class, "YOUR_ID");
```

### Updating

Currently updating can be quite intensive(Check the speed tests to see averages). However it does supprt one-to-many relation ships and any other updating features you can think of.

```java
//Variable `utils` defined above
utils.query(YOUR_OBJECT); //The primary key will be taken directly from the object so make sure its not null.
```

### Deleting

To delete, as you might guess it follows the same pattern of the others(The same format as a query).

```java
//Variable `utils` defined above
utils.delete(YOUR_OBJECT.class, "YOUR_ID");
```

## DataBase Utilities

To configure the DB utilites you can do the following
```java
final ORLayerDBUtils orLayerDBUtils = ORSetup.configureDBUtils("URL", "PASSWORD", "USERNAME"); //In this case you do 
//NOT have to provide a database ending on your URL if you are creating one. If your are going to create a DB then 
//create a table you would configure a DBUtils(without the DB ending) and then use the return value of that to set
//up a new utility for creating tables.
```

### DataBase creation

If you are creating a application that requires a DB but should be easy to set up this is what you will need. To create a DataBase:
```java
//Variable `orLayerDBUtils` defined above
 final ORSetup.DBInformation exampleDB = orLayerDBUtils.createDB("exampleDB");
```

### Table Creation

To create a table there are 2 ways of doing it. 

#### From a class

Advantages | Disadvantages 
--- | --- 
Creates a whole heirchy of tables | Much slower
Allows for flexibility in design and freedom to change structure | not optimized

```java
//Variable `orLayerDBUtils` defined above
orLayerDBUtils.createTable(YOUR_OBJECT.class, false); //The boolean value indicates if you want to do a deep
//create or a light top level create(Creating a whole structure of tables or just the one of that class)
```

#### From resources

Advantages | Disadvantages 
--- | --- 
Very fast | Can only create 1 table at a time
Easy to optimize(pure sql_ | Not flexible

The first step is to define the directory in your resources. Each table creation statement will go in a seperate file under the same directory. This defaults to "manifest" but can be changed

Example
```
-src
  -main
    -java
      -YOUR_PROJECT
    -resources
      -manifest
        -CREATE_TABLE.sql
```
The name of the table you are creating should match with the file name

Next you need to create the table with the DBUtils

```java
//Variable `orLayerDBUtils` defined above
orLayerDBUtils.createTable("FILE_NAME"); //Can be a .sql file, but .txt works etc...

//To define your own structure you can provide the path and the file name from the resources dir
orLayerDBUtils.createTable("FILE_NAME", "FILE_LOCATION");
```

## Data Structures

LightWeightORLayer has a very heavy annotation based API. Here is how each data bean should look with annotations and how it should represent its corresponding table.

### Required
1) Every Class should be annotated with `@SQLNode` this will tell the API what table it represents and that it is a node.
2) Every class should have a member variable annotated with `@SQLPrimaryIndex`. This will tell the API what is the primary key for this column

3) Every bean needs a non-paremeterized constructor.

### One To Many
1) All One-To-Many relationship chlidren need to be marked with `@OneToManyRelationshipChild`
2) All columns holding one to many relation ships(Collection) must be marked with `@SQLOneToMany`, This takes a class which will tell the ORlayer what to iterate.
3) All One-To-Many relationship chlidren need to have a member variable(and a corresponding SQL Column) marked with `@SQLChildRelationalColumn`. This will be the value that identifies it to its parent(This value corresponds to the parents ID(only tested with integers))

### Extra
1) To ignore a member variable you can mark it with `@SQLIgnore`. Additionally all `transient` or `static` fields will be ignored.
2) To mark a member variable as having a different column name in SQL as in Java it can be marked with `@SQLColumnName` and provide the column name in SQL.

For a full example please see the end of this document.

## Relationships

We can support the following relationships:
- [One to many](#one-to-many)
- [One to One](#required)
- Many to one : By user

## Whole system example

First we create our beans.

**Note:** This is the setup we used in testing so if you want to see it as a project you can go to `src/test/java/net/questcraft/structuretests`

One:
```java
@SQLNode("testTable1")
public class StructuredTestTable1 {

    @SQLColumnName("id") @SQLPrimaryIndex() private Long primary;
    @SQLColumnName("title") private Integer secondary;

    public StructuredTestTable1(Long primary, Integer secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    public StructuredTestTable1() {
    }
}
```

Two:
```java
@SQLNode("testTable2")
public class StructuredTestTable2 {
    @SQLPrimaryIndex private String username;
    private String password;
    @SQLColumnName("postID") private StructuredTestTable1 cardPost; //One to one
    private StructuredTestTable3 profilePic;


    public StructuredTestTable2(String username, String password, StructuredTestTable1 cardPost, StructuredTestTable3 profilePic) {
        this.username = username;
        this.password = password;
        this.cardPost = cardPost;
        this.profilePic = profilePic;
    }

    public StructuredTestTable2() {
    }
}
```

Three:
```java
@SQLNode("testTable3")
public class StructuredTestTable3 {
    @SQLPrimaryIndex
    private Long id;
    private String image;
    private Integer identifier;
    @SQLOneToMany(StructuredTestTable4.class)
    private List<StructuredTestTable4> friends; //One to many
    @SQLOneToMany(StructuredTestTable5.class)
    private List<StructuredTestTable5> blocks; //One to many

    public StructuredTestTable3(Long id, String image, Integer identifier, List<StructuredTestTable4> friends, List<StructuredTestTable5> blocks) {
        this.id = id;
        this.image = image;
        this.identifier = identifier;
        this.friends = friends;
        this.blocks = blocks;
    }

    public StructuredTestTable3() {
    }
}
```
Four:
```java
@SQLNode("testTable4")
@OneToManyRelationshipChild //Marked as a one to many child, a node cannot be a one to many child and a one to one child
                            // Instead there should be two seperate tables for identical children.
public class StructuredTestTable4 {
    @SQLPrimaryIndex
    private Long id;
    @SQLChildRelationalColumn
    private String value;
    @SQLColumnName("third")
    private String three;

    public StructuredTestTable4(Long id, String value, String three) {
        this.id = id;
        this.value = value;
        this.three = three;
    }

    public StructuredTestTable4() {
    }
}
```
Five:


















