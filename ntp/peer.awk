#!/usr/bin/awk -f
# awk program to scan peerstats files and report errors/statistics
#
# usage: peer.awk peerstats
#
# format of peerstats record
#  $1     $2     $3      $4    $5          $6         $7
#  MJD    sec    ident   stat  offset (s)  delay (s)  disp (s)
# 49235 11.632 128.4.2.7 f414  -0.000041    0.21910   0.00084
#
# output time values in milliseconds
#

BEGIN {
        n = 0
        MAXDISTANCE = 1.0
}

#
# scan all records in file
#
# we toss out all distances greater than one second on the assumption the
# peer is in initial acquisition
#
{
        if (NF >= 7 && ($7 + $6 / 2) < MAXDISTANCE)
        {
                i = n
                for (j = 0; j < n; j++)
                {
                        if ($3 == peer_ident[j])
                        i = j
                }
                if (i == n)
                {
                        peer_ident[i] = $3
                        peer_tmax[i] = peer_dist[i] = -1e9
                        peer_tmin[i] = 1e9
                        n++
                }
                peer_count[i]++
                if ($5 > peer_tmax[i])
                        peer_tmax[i] = $5
                if ($5 < peer_tmin[i])
                        peer_tmin[i] = $5
                dist = $7 + $6 / 2
                if (dist > peer_dist[i])
                        peer_dist[i] = dist
                peer_time[i] += $5
                peer_time_rms[i] += $5 * $5
                peer_delay[i] += $6
                peer_disp[i] +=  $7
        }
}

END {
        printf "       ident     cnt     mean   std*100    rms      min     max     delay     dist     disp\n"
        printf "===========================================================================================\n"
        for (i = 0; i < n; i++)
        {
                std = sqrt(peer_time_rms[i]/peer_count[i] - (peer_time[i]/NR)^2)*100
                peer_time[i] /= peer_count[i]
                peer_time_rms[i] = sqrt(peer_time_rms[i] / peer_count[i])
                peer_delay[i] /= peer_count[i]
                peer_disp[i] /= peer_count[i]
                printf "%-15s%5d%9.3f%9.3f%9.3f%9.3f%9.3f%9.3f%9.3f%9.3f\n", peer_ident[i], peer_count[i], peer_time[i] * 1e3, std, peer_time_rms[i] * 1e3, peer_tmin[i] * 1e3, peer_tmax[i] * 1e3, peer_delay[i] * 1e3, peer_dist[i] * 1e3, peer_disp[i] * 1e3
        }
}
