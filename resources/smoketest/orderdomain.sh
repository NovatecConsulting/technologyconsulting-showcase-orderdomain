function setup
{
        # setup customer with id 1
        curl -u admin:adminpwd --header "Content-Type: application/json" --request POST --data @data/customer.json http://$HOST:$PORT/orderdomain/customer

        # setup items with id 1..24
        for i in {1..24}; do
                curl -u admin:adminpwd --header "Content-Type: application/json" --request POST --data @data/item_"$i".json http://$HOST:$PORT/orderdomain/item;
        done
}

function business_calls
{
        # create a new order (large order) for customer with id 1
        curl -u admin:adminpwd --header "Content-Type: application/json" --request POST --data @data/items_quantity_pairs_large_order.json http://$HOST:$PORT/orderdomain/order/1

        # create a new order for customer with id 1
        curl -u admin:adminpwd --header "Content-Type: application/json" --request POST --data @data/items_quantity_pairs.json http://$HOST:$PORT/orderdomain/order/1

        # try to create a new order for a non existing customer with id 100
        curl -u admin:adminpwd --header "Content-Type: application/json" --request POST --data @data/items_quantity_pairs.json http://$HOST:$PORT/orderdomain/order/100

        # add inventory for customer with id 1
        curl -u testuser:pwd --header "Content-Type: application/json" --request PUT http://$HOST:$PORT/orderdomain/customer/add_inventory/1

        # sell inventory for customer with id 1, item with id 2 and the quantity 1
        curl -u testuser:pwd --header "Content-Type: application/json" --request PUT http://$HOST:$PORT/orderdomain/customer/sell_inventory/1/2/1
}

