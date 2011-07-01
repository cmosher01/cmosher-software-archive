#!/bin/bash
cd /var/log/ntpstats
peer.awk peerstats
ntpq -c "hostname no" -c peers
ntpq -c peers
grep '^\w*server' /etc/ntp.conf
ntpq -c as -c rv -c "rv &1" -c cl -c mru
cd -
