# planisense_server
Server side of the technical test.

# The database has to already have been setup as such:
* It has a table named *arbres*
* *arbres* has 3 columns:
  ```id: Integer, arrondissement: varchar, genre: varchar```
* It has been previously filled with *planisense_add_data*

### Building the app
```mvn clean package```

### Running the app

It needs an *application.properties* file located in the same place as the jar
```
$ tree
.
|_ application.properties
|_ planisense_server-{version}.jar

$ java -jar planisense_server-{version}.jar
```

### Application.properties
* **database.url** : cf. https://jdbc.postgresql.org/documentation/head/connect.html
* **database.username**
* **database.password**
* **application.host**: Default to localhost so that the client can call the api
* **application.port**: Default to 8080 so that the client can call the api