#!/bin/bash
set -e

az login --service-principal \
    -u $ARM_CLIENT_ID \
    -p $ARM_CLIENT_SECRET \
    --tenant $ARM_TENANT_ID \
    --allow-no-subscriptions > /dev/null

DBUSER=$(az keyvault secret show --vault-name "vault-tc-showcase-test" --name "database-user" --query value -o tsv)

DBPWD=$(az keyvault secret show --vault-name "vault-tc-showcase-test" --name "database-password" --query value -o tsv)

# put values to Github's env stage
# this not a safe solution
echo "DBUSER=$DBUSER" >> $GITHUB_ENV
echo "DBPWD=$DBPWD" >> $GITHUB_ENV
