#!/bin/bash

# start the mockserver
java -Dmockserver.initializationJsonPath=./data/init_expectations.json -jar ./lib/mockserver-netty-5.8.1-jar-with-dependencies.jar -serverPort 9090 -logLevel ERROR &
#wait while mockserver is staring
sleep 2

# create a new order for customer with id 1
curl -u admin:adminpwd --header "Content-Type: application/json" --request POST --data @data/items_quantity_pairs.json http://localhost:9080/orderdomain/order/1

# add inventory for customer with id 1
curl -u testuser:pwd --header "Content-Type: application/json" --request PUT http://localhost:9080/orderdomain/customer/add_inventory/1

# sell inventory for customer with id 1, item with id 2 and the quantity 1
curl -u testuser:pwd --header "Content-Type: application/json" --request PUT http://localhost:9080/orderdomain/customer/sell_inventory/1/2/1

# stop the mockserver
curl -X PUT "http://localhost:9090/stop" -H  "accept: */*"