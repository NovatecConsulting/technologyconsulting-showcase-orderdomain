curl -u admin:adminpwd --header "Content-Type: application/json" --request POST --data @data/customer.json http://localhost:9080/orderdomain/customer

curl -u admin:adminpwd --header "Content-Type: application/json" --request POST --data @data/item_1.json http://localhost:9080/orderdomain/item
curl -u admin:adminpwd --header "Content-Type: application/json" --request POST --data @data/item_2.json http://localhost:9080/orderdomain/item
curl -u admin:adminpwd --header "Content-Type: application/json" --request POST --data @data/item_3.json http://localhost:9080/orderdomain/item

curl -u admin:adminpwd --header "Content-Type: application/json" --request POST --data @data/items_quantity_pairs.json http://localhost:9080/orderdomain/order/1

curl -u testuser:pwd --header "Content-Type: application/json" --request PUT http://localhost:9080/orderdomain/customer/add_inventory/1
curl -u testuser:pwd --header "Content-Type: application/json" --request PUT http://localhost:9080/orderdomain/customer/sell_inventory/1/2/1
