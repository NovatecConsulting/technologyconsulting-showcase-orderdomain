curl -u admin:adminpwd --header "Content-Type: application/json" --request POST --data @data/customer.json http://localhost:9080/orderdomain/customer

for i in {1..24}; do
	curl -u admin:adminpwd --header "Content-Type: application/json" --request POST --data @data/item_"$i".json http://localhost:9080/orderdomain/item;
done

curl -u admin:adminpwd --header "Content-Type: application/json" --request POST --data @data/items_quantity_pairs.json http://localhost:9080/orderdomain/order/1

curl -u testuser:pwd --header "Content-Type: application/json" --request PUT http://localhost:9080/orderdomain/customer/add_inventory/1
curl -u testuser:pwd --header "Content-Type: application/json" --request PUT http://localhost:9080/orderdomain/customer/sell_inventory/1/2/1
