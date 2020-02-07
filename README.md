# technologyconsulting-showcase-orderdomain
orderdomain is a part of a showcase implementation which is running on a open liberty instance. It is structured right now like this

- **orderdomainParent** Parent maven module
    - **orderdomainDTO** - contains all classes used in the rest controllers
    - **orderdomainWAR** - contains the rest controllers and all EJB classes and entities
    - **orderdomainEAR** - contains the war module

and could be found under the src folder

## Quickstart

In order to run the example, infrastructure components like Kafka are required. As a quick start, you may use the provides Docker environment. 

Start the Docker environment:

```
./up.sh --with-monitoring all
```

For more details about the environment read the [Readme file](environment/README.adoc).

## The projects consists of the following packages

- **de.novatec.showcase.order.dto** - with all related order domain dto's
- **de.novatec.showcase.order.ejb.entity** - with all related order domain entities
- **de.novatec.showcase.order.ejb.session** - with the order domain EJB session beans
- **de.novatec.showcase.order.controller** - with corresponding REST controllers for Item, Customer and Order
- **de.novatec.showcase.order.mapper** - with orika mapper fro dto/entity mapping

## build, run and stop orderdomain on an open liberty server
- **build:** mvn clean install
- **run:** mvn liberty:run
- **stop:** mvn liberty:stop
- **run open liberty in development mode:** mvn liberty:dev

All commands have to be executed from the orderdomainEAR folder. In development mode you can run the the integration tests (*IT.java classes) by pressing RETURN/ENTER when the server is up. Code changes in the IT tests are hot replaced.

## Smoketest
There is a little script smoketest.sh in the ./resources/smoketest folder which could be used to test if the very basic functionality works after staring the open liberty server with the orderdomain as EAR.

- create 24 items
- create a customer
- create an order
- call addInventory for created customer
- call sellInventory for created customer 

The smoketest.sh script consist of two sub scripts - the setup-db.sh and business-calls.sh script. The first one setup the database of the orderdomain via some REST calls. The data for the calls could be found in the folder ./resources/smoketest/data. The second script starts/stops a mockserver which is used for emulating the manufature domain in the business calls. The exceptations for the calls could also be found in data folder.

## openAPI
check [openAPI](http://localhost:9080/api/explorer/) if the server is running for the  API of the domain

## TODOs:

- check functionality of ItemSession.getSubList -> refactor this "PARTS"-thing...
- OrderStatus model is not clean, i.e. DELETED order are also counted with order/count_by_customer/{id}
