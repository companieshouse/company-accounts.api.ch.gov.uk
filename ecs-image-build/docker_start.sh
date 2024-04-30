#!/bin/bash
#
# Start script for company-accounts-api

PORT=8080

exec java -jar -Dserver.port="${PORT}" "company-accounts.api.ch.gov.uk.jar"
