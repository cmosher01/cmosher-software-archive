#!/bin/sh

# NOTE: THIS IS NOT WORKING!
# The hard links are not being created. I don't know why.
# (This is on Ubuntu 14.04.2)

do_backup() {
	cp time_curr.txt time_prev.txt
	date +%Y%m%d.%H%M%S >time_curr.txt
	rsync -avizhPr --delete --stats \
		--link-dest="$(cat time_prev.txt)" \
                --checksum \
		--files-from="b.rsync" \
		\
		$tmp \
		backup/$(cat time_curr.txt)
	printf "%s\n" "----------------------------------------------------"
}


tmp=`mktemp -d`

cd $tmp




mkdir realdir1 realdir2 backup
cd realdir1
echo "foobar" >foobar
echo "junk" >junk
cd -
cd realdir2
echo "snafu" >snafu

cd $tmp
cat - >b.rsync <<EOF
realdir1
realdir2
EOF

date +%Y%m%d.%H%M%S >time_curr.txt
sleep 2
do_backup
sleep 2
rm realdir1/junk
do_backup
sleep 2
touch realdir2/foo
do_backup




ls -lRi --full-time --color $tmp
tree -D --inodes $tmp

printf "\n    rm -R %s\n\n" $tmp
rm -R $tmp
