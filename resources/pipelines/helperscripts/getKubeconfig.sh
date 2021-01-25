#!/bin/bash
set -e

az login --service-principal \
    -u $ARM_CLIENT_ID \
    -p $ARM_CLIENT_SECRET \
    --tenant $ARM_TENANT_ID \
    --allow-no-subscriptions > /dev/null

az aks get-credentials --resource-group $AKSRESOURCEGROUP --name $AKSNAME --file kubeconfig/.kubeconfig
