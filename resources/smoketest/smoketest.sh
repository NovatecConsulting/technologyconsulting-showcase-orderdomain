#!/bin/bash

java -Dmockserver.initializationJsonPath=./data/init_expectations.json -jar ./lib/mockserver-netty-5.8.1-jar-with-dependencies.jar -serverPort 9090 -logLevel ERROR &
#wait while mockserver is staring
sleep 1

curl -u admin:adminpwd --header "Content-Type: application/json" --request POST --data @data/customer.json http://localhost:9080/orderdomain/customer

for i in {1..24}; do
	curl -u admin:adminpwd --header "Content-Type: application/json" --request POST --data @data/item_"$i".json http://localhost:9080/orderdomain/item;
done

curl -u admin:adminpwd --header "Content-Type: application/json" --request POST --data @data/items_quantity_pairs.json http://localhost:9080/orderdomain/order/1

curl -u testuser:pwd --header "Content-Type: application/json" --request PUT http://localhost:9080/orderdomain/customer/add_inventory/1
curl -u testuser:pwd --header "Content-Type: application/json" --request PUT http://localhost:9080/orderdomain/customer/sell_inventory/1/2/1

curl -X PUT "http://localhost:9090/stop" -H  "accept: */*"