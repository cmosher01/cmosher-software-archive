#!/bin/sh

usage() {
  cat <<EOF
$0

  {{Does something}}

usage:

  $0 OPTIONS ARGUMENTS

options:

  -o  {{some option}}
  -h  print this help

EOF
}



# check options and argument
option=false
while getopts oh opt
do
  case $opt in
    o) option=true ;;
    h) usage ; exit ;;
    \?) usage ; exit 1 ;;
  esac
done

shift $((OPTIND-1))

if [ $# -lt 1 -o 1 -lt $# ]
then
  usage
  exit 1
fi
