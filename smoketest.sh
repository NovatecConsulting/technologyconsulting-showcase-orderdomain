curl --header "Content-Type: application/json"   --request POST --data @target/test-classes/customer.json http://localhost:9080/orderdomain/customer

curl --header "Content-Type: application/json"   --request POST --data @target/test-classes/item_1.json http://localhost:9080/orderdomain/item
curl --header "Content-Type: application/json"   --request POST --data @target/test-classes/item_2.json http://localhost:9080/orderdomain/item
curl --header "Content-Type: application/json"   --request POST --data @target/test-classes/item_3.json http://localhost:9080/orderdomain/item

curl --header "Content-Type: application/json"   --request POST --data @target/test-classes/items_quantity_pairs.json http://localhost:9080/orderdomain/order/1

curl --header "Content-Type: application/json"   --request PUT http://localhost:9080/orderdomain/customer/add_inventory/1
# curl --header "Content-Type: application/json"   --request PUT http://localhost:9080/orderdomain/customer/sell_inventory/1/2/1
