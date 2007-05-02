#!/bin/sh
set -e
cd bulkemailer
BEJAR=/usr/ssi/deploy/app/bulkemailer/${SWLEVEL}/lib/bulkemailer.jar
java -server -Xmx384m -Dbulkemailer=1 -Dcom.sun.management.jmxremote.port=60002 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -jar $BEJAR >>bulkemailer.out 2>&1
