# technologyconsulting-security-showcase-orderdomain
orderdomain is a part of a showcase implementation which is running on a open liberty instance. It is structured right now like this

- **orderdomainParent** Parent maven module
    - **orderdomainDTO** - contains all classes used in the rest controllers
    - **orderdomainWAR** - contains the rest controllers and all EJB classes and entities
    - **orderdomainEAR** - contains the war module

## The projects consists of the following packages

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
There is a little script smoketest.sh in the orderdomainParent\resources\smoketest folder which could be used to test if the very basic functionality works after staring the open liberty server with the orderdomain as EAR.

- create 24 items
- create a customer
- create an order
- call addInventory for created customer
- call sellInventory for created customer 

## openAPI
check [openAPI](http://localhost:9080/api/explorer/) if the server is running for the  API of the domain

## TODOs:

- Better REST Responses including status codes
- some validations to avoid NPE's
- check functionality of ItemSession.getSubList -> refactor this "PARTS"-thing...
- OrderStatus model is not clean, i.e. DELETED order are also counted with order/count_by_customer/{id}