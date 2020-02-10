declare MOCKSERVER_HOST=localhost
declare MOCKSERVER_PORT=9090

function start_mockserver
{
	java -Dmockserver.initializationJsonPath=./data/init_expectations.json -jar ./lib/mockserver-netty-5.8.1-jar-with-dependencies.jar -serverPort $MOCKSERVER_PORT -logLevel ERROR &
	#wait while mockserver is staring
	until $(curl --output /dev/null --silent --head --fail -X PUT "http://$MOCKSERVER_HOST:$MOCKSERVER_PORT/mockserver/status"); do
		sleep 1  
	done	
}

function stop_mockserver
{
	# wait for still executing calls to mockserver
        sleep 1
        # stop the mockserver
        curl -X PUT "http://$MOCKSERVER_HOST:$MOCKSERVER_PORT/stop" -H  "accept: */*"
}

