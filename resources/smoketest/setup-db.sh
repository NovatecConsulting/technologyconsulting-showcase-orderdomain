#!/bin/bash
declare HOST=localhost
declare PORT=9080

. `dirname $0`/options.sh
. `dirname $0`/orderdomain.sh


function main_setup
{
	script_options $@
	setup
}

main_setup $@
