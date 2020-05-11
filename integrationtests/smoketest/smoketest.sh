#!/bin/bash

get_token(){
    token_field=$(curl  -d "client_secret=3b07e614-4940-44fd-8349-a67634f01733" -d "client_id=order-domain" -d "username=$1" -d "password=$2" -d "grant_type=password" http://localhost:8080/auth/realms/orderDomain/protocol/openid-connect/token | grep -Po '"access_token":.*?[^\\]",')
    token=$(echo $token_field | cut -c17-$((${#token_field}-2)))
}
#curl -u "order-domain:3b07e614-4940-44fd-8349-a67634f01733"  -d "username=$1" -d "password=$2" -d "grant_type=client_credentials" "http://localhost:8080/auth/realms/orderDomain/protocol/openid-connect/token"
# setup the database
#-d "client_secret=3b07e614-4940-44fd-8349-a67634f01733" -d "client_id=order-domain" -d "username=$1" -d "password=$2" -d "grant_type=password" http://localhost:8080/auth/realms/orderDomain/protocol/openid-connect/token
source ./setup-db.sh

# do some business calls
source ./business-calls.sh
