# tc-orderdomain-showcase
orderdomain is a part of a showcase implementation which is running on a open liberty instance

#### The project consists of the following packages

- **de.novatec.showcase.orders.entity** - with all related order domain entities
- **de.novatec.showcase.orders.session** - with the order domain EJB session beans
- **de.novatec.showcase.Controller** - with corresponding REST controllers for Item, Customer and Order

#### build, run and stop orderdomain on an open liberty server
- **build:** mvn clean install
- **run:** mvn liberty:run
- **stop:** mvn liberty:stop
- **run open liberty in development mode:** mvn liberty:dev

All command have to be executed from the orderdomain folder. IN development mode you can run the the integration tests (*IT.java classes) by pressing RETURN/ENTER when the server is up. Code changes in the IT tests are hot replaced.

#### Smoketest
There is a little script smoketest.sh in the orderdomain folder which could be used to test if the very basic functionality works after staring the open liberty server with the orderdomain.

- create three items
- create a customer
- create an order
- call addInventory for created customer
- call sellInventory for created customer 

#### TODOs:

- replace formerly existing MDB/JMS code with REST clients to other domains (manufacturing, supplier)
- Better REST Responses including status codes
- some validations to avoid NPE's
- more test cases for REST Controllers
- check functionality of ItemSession.getSubList -> refactor this "PARTS"-thing...
- the parent pom version has to be updated as soon as 3.2 is out, there should be a fix for the maven-failsafe-plugin bug (https://github.com/OpenLiberty/ci.maven/issues/570) available, right now integration tests could be excuted just once, then you get an ERROR from maven-failsafe-plugin which makes restart impossible