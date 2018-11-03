#!/bin/sh

set +e

something() {
    echo "something echoed"
    echo "ERROR" >&2
    return 1
}

echo "1"
x=$(something)
echo "status: $?"
echo "2"
echo "$x"
echo "3"
