rm -Rf build
mkdir build
cd build



mkdir apple1
cd apple1
../../intbasic/build.sh 1
mv intbasic apple1_A\$E000_L\$1000_intbasic
../../monitor/build.sh 0
mv monitor apple1_A\$FF00_L\$0100_monitor
cd ..



mkdir apple2
cd apple2
../../intbasic/build.sh 2
mv intbasic apple2_A\$E000_L\$1425_intbasic
../../other/build.sh
mv other apple2_A\$F425_L\$03DB_other
../../monitor/build.sh 1
mv monitor apple2_A\$F800_L\$0800_monitor
cd ..



mkdir apple2plus
cd apple2plus
../../applesoft/build.sh
mv applesoft apple2plus_A\$D000_L\$2800_applesoft
../../monitor/build.sh 2
mv monitor apple2plus_A\$F800_L\$0800_monitor
cd ..



mkdir disk2_13sector
cd disk2_13sector
../../disk2/build.sh 13
mv slot6 disk2_A\$C600_L\$0100_13sector
cd ..



mkdir disk2_16sector
cd disk2_16sector
../../disk2/build.sh 16
mv slot6 disk2_A\$C600_L\$0100_16sector
cd ..



cd ..
