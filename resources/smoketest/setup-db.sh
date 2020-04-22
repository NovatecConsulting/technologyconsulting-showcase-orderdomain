#!/bin/bash

get_token "admin" "adminpwd"

# setup customer with id 1
curl --header "Authorization: Bearer $token" --header "Content-Type: application/json" --request POST --data @data/customer.json http://localhost:9080/orderdomain/customer

# setup items with id 1..24
for i in {1..24}; do
	curl --header "Authorization: Bearer $token" --header "Content-Type: application/json" --request POST --data @data/item_"$i".json http://localhost:9080/orderdomain/item;
done
