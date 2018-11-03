#!/bin/sh

tmpdir=`mktemp -d`
p=$tmpdir/foo
mkfifo $p

echo "starting x..."
./x.sh $p &
echo "reading from pipe..."
while read -r line_in ; do
  lll=$line_in
done <$p
echo "done"

echo "got this: $lll"

rm $p
rmdir $tmpdir
