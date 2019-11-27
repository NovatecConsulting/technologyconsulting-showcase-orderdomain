# technologyconsulting-security-showcase-orderdomain-showcase
orderdomain is a part of a showcase implementation which is running on a open liberty instance. It ist structured right now like this

- **orderdomainParent** Parent maven module
  - **orderdomainDTO** - contains all classes used in the rest controllers
  - **orderdomainWAR** - contains the rest controllers and all EJB classes and entities
  - **orderdomainEAR** - contains the war module

#### The project consists of the following packages

- **de.novatec.showcase.orders.entity** - with all related order domain entities
- **de.novatec.showcase.orders.session** - with the order domain EJB session beans
- **de.novatec.showcase.Controller** - with corresponding REST controllers for Item, Customer and Order

#### build, run and stop orderdomain on an open liberty server
- **build:** mvn clean install
- **run:** mvn liberty:run
- **stop:** mvn liberty:stop
- **run open liberty in development mode:** mvn liberty:dev

All command have to be executed from the orderdomainEAR/WAR folder. IN development mode you can run the the integration tests (*IT.java classes) by pressing RETURN/ENTER when the server is up. Code changes in the IT tests are hot replaced.

Right now the integration tests run just in the WAR module. For running this also in the EAR a jar with the DTO has to be created. So that the objects are also be used in the tests when running in the EAR module. 

#### Smoketest
There is a little script smoketest.sh in the orderdomainParent\resources\smoketest folder which could be used to test if the very basic functionality works after staring the open liberty server with the orderdomain as WAR or as EAR.

- create three items
- create a customer
- create an order
- call addInventory for created customer
- call sellInventory for created customer 

#### TODOs:

- replace formerly existing MDB/JMS code with REST clients to other domains (manufacturing, supplier)
- Better REST Responses including status codes
- some validations to avoid NPE's
- check functionality of ItemSession.getSubList -> refactor this "PARTS"-thing...
- OrderStatus model is not clean, i.e. DELETED order are also counted with order/count_by_customer/{id}
- move the integration tests to the EAR by using the orderdomainDTO project
