# Code Challenge
This is a small [spring-boot](https://projects.spring.io/spring-boot/) based project that contains a mixture of 
unit and component tests within a [maven multi-module](https://maven.apache.org/guides/mini/guide-multiple-modules.html) project.

The project requires [java 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) or greater 
and [apache maven](https://maven.apache.org/download.cgi) is comprised of the following modules:
```
commons = shared code
integration = simple example integrations to the provided RESTful service
service = example of a simple RESTful service
```

To perform a build and execute all unit tests across all of the modules:
```
mvn clean install
```

To execute all component tests across all of the modules:
```
mvn -P test-component test
```

By default, application.properties uses an in memory instance of an [H2 database](h2database.com) so that anyone can use this project immediately without further configuartion or installation.

To run with default h2 in memory database:
```
cd service
mvn spring-boot:run
```

Once the application is running, the REST [swagger ui](http://swagger.io/swagger-ui/) based documentation and tester will be available for use via:
```
http://localhost:8080/webjars/swagger-ui/2.1.8-M1/index.html?url=/v2/api-docs/#!/card-rest-controller
```

--

It is not required but the project can also be run with a locally installed MySQL database configured via port 3306 with a schema named `learnvest-code-challenge`
as defined within `/service/src/main/resources/application-localmysql.properties`:
```
cd service
mvn spring-boot:run -Drun.jvmArguments="-Dspring.profiles.active=localmysql"
```

See `/service/src/main/resources` for available profiles.

## Assignment

*Step 1*: Perform a build and execute all unit tests across all of the modules:

```mvn clean install```

> _Please note that when the above command is run that the following should be part of the output generated:_
> ```Tests run: 26, Failures: 0, Errors: 0, Skipped: 7```

*Step 2*: Execute all of component level tests across the project:

```mvn -P test-component test```

> _Please note that when the above command is run that the following should be part of the output generated:_
> ```Tests run: 12, Failures: 0, Errors: 0, Skipped: 6```

There are a total of thirteen tests that are currently being ignored *using the `@Ignore` annotation of the junit library) 
because the corresponding functionality within the project is incomplete.

Twelve of the tests that need to be successfully executing once the assignment is complete are located within the 
CardRestControllerUnitTest (`/service/src/test/java/com/learnvest/qacodechallenge/service/controller/CardRestControllerUnitTest.java`) 
and CardRestControllerComponentTest (`/service/src/test-component/java/com/learnvest/qacodechallenge/service/controller/CardRestControllerComponentTest.java`) classes:
- deleteCard
- deleteCardNonExistent
- updateCard
- updateCardColumnTooLong
- updateCardNull
- updateCardNullId

The CardDaoUnitTest class (`/service/src/test/java/com/learnvest/qacodechallenge/service/db/CardDaoUnitTest.java`) also 
contains an ignored test that exists within its update method.

At the successful completion of this assignment the above listed tests will no longer be ignored and instead need to be 
properly exercising the units of code to be completed in the parts of the assignment below. Once the functional code changes 
listed below are complete simply remove the `@Ignore` annotations and the corresponding unnecessary import statement and execute 
the unit tests and component tests to ensure the code changes are complete and correct.

———————

#### Assignment #1:
The CardDao class (`/code-challenge-learnvest/service/src/main/java/com/learnvest/qacodechallenge/service/db/CardDao.java`) 
performs database CRUD (Create, Read, Update and Delete) operations for a Card object (`/commons/src/main/java/com/learnvest/qacodechallenge/commons/model/card/Card.java`) 
by using SQL statements that are located within the `card.sql` resource file (`/service/src/main/resources/dao/card.sql`). 

The `card.sql` file does not currently contain the necessary statement to perform the database update function. 
We need you to construct a properly structured and terminated statement and insert it into this file.

Once in place and in order to verify that the SQL statement is correct, locate the CardDaoUnitTest class (`/service/src/test/java/com/learnvest/qacodechallenge/service/db/CardDaoUnitTest.java`), 
the CardRestControllerUnitTest class (`/service/src/test/java/com/learnvest/qacodechallenge/service/controller/CardRestControllerUnitTest.java`) 
and the CardRestControllerComponentTest (`/service/src/test-component/java/com/learnvest/qacodechallenge/service/controller/CardRestControllerComponentTest.java`) 
and remove the `@Ignore` annotations from the update specific test methods provided. Verify that when these test are run they are successfully exercising the 
CardDao’s update functionality which consumes the SQL that you created within the `card.sql` file.

#### Assignment #2:
The CardRestController class (`/code-challenge-learnvest/service/src/main/java/com/learnvest/qacodechallenge/service/controller/CardRestController.java`) 
contains a `delete` method that needs to be completed. The entire class contains java docs that describe each of the REST controllers functionality 
and by following the documentation provided in the project’s README file you can stand up and run the RESTful web service that is 
produced by the project.

The TODOs within the CardRestController’s delete method specify the code to be completed and the 
CardRestControllerUnitTest (`/service/src/test/java/com/learnvest/qacodechallenge/service/controller/CardRestControllerUnitTest.java`) 
and CardRestControllerComponentTest (`/service/src/test-component/java/com/learnvest/qacodechallenge/service/controller/CardRestControllerComponentTest.java`) classes 
contain the deleteCard and deleteNonExistent tests that will verify the functionality is correct once their corresponding 
`@Ignore` annotation has been removed.

#### Assignment #3:
You have the opportunity to wow us by doing all the work necessary to create a new field within the existing Card 
object (`/commons/src/main/java/com/learnvest/qacodechallenge/commons/model/card/Card.java`) and the code that supports its underlying 
CRUD operations.

Start by adding a new field to the Card object (`/commons/src/main/java/com/learnvest/qacodechallenge/commons/model/card/Card.java`) 
that will be used to contain a text description of the card. Do all of the corresponding work within that class to follow the existing 
implementations already there.

In order to add the column to the database that is defined within the schema file (`/service/src/main/resources/db/migration/V001.001__service.sql`) 
you will need to create a new file named `V002.001__alter_card_table.sql` and store it in the `/service/src/main/resources/db/migration` directory.
This file must contain a proper SQL statement to alter the existing card table (`ALTER TABLE card ADD COLUMN`) to include the new column 
that you are creating. For reference about this specific functionality please refer to [flywaydb](https://flywaydb.org/getstarted/) as this project 
uses the flyway API to perform database schema migrations automatically.

Once the new column has been added to the database you will need to make the corresponding changes to the appropriate SQL statements 
within the card.sql (`/code-challenge-learnvest/service/src/main/resources/dao/card.sql`) to support the new field.

Next locate the CardDaoRowMapper class (`/service/src/main/java/com/learnvest/qacodechallenge/service/db/CardDaoRowMapper.java`) and 
update the mapObject and mapRow methods to support the new field added to the Card object and the underlying data. 
The CardDao class uses the CardDaoRowMapper class to bind the fields within the database to the corresponding fields within the Card object.

Once you have verified that all of the existing tests that were corrected in assignments #1 and #2 are still correctly working,
you can use the existing test code as a guide and create any new tests that you feel would be necessary to properly exercise and 
verify your newly revised application code is working correctly.
