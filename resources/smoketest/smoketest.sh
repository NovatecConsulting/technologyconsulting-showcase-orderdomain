#!/bin/bash
declare HOST=localhost
declare PORT=9080

. `dirname $0`/options.sh
. `dirname $0`/mockserver.sh
. `dirname $0`/orderdomain.sh

function main
{
	script_options
	# setup the database
	setup

	# do some business calls
	start_mockserver
	business_calls
	stop_mockserver
}

main $@
