#!/bin/bash
declare HOST=localhost
declare PORT=9080

. `dirname $0`/options.sh

function main_setup
{
	script_options
	setup
}

function setup
{
        # setup customer with id 1
        curl -u admin:adminpwd --header "Content-Type: application/json" --request POST --data @data/customer.json http://$HOST:$PORT/orderdomain/customer

        # setup items with id 1..24
        for i in {1..24}; do
                curl -u admin:adminpwd --header "Content-Type: application/json" --request POST --data @data/item_"$i".json http://$HOST:$PORT/orderdomain/item;
        done
}

main_setup $@
