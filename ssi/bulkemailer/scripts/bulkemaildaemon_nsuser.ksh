#!/bin/ksh
#
# Starts or stops the Bulk Emailer server process.
# This script is designed to be placed in the
# /etc/init.d directory, and to have a symbolic
# link to it in an rc*.d directory, so it runs at
# system startup or shutdown.
#
# Usage: /etc/init.d/bulkemailerdaemon_nsuser.ksh {start|stop}
#
case "$1" in
'start')
        echo " Starting Bulk Emailer"
        su - nsuser -c "at -q b -k -f /usr/ssi/deploy/app/bulkemailer/\${SWLEVEL}/scripts/bem.ksh now"
;;
'stop')
        echo " Killing Bulk Emailer"
        echo "POST /shutdown" >/dev/tcp/127.0.0.1/60001
;;
*)
        echo "Usage: /etc/init.d/bulkemailerdaemon_nsuser.ksh {start|stop}"
        exit 1
;;
esac
