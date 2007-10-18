rem given dos 3.1 build exe image in file "dos", puts it into a clean nibble disk image "dos.dsk"
java -cp bin dd --skip=0x1B00 dos >dos.d13
java -cp bin dd --count=0x1B00 dos >>dos.d13
java -cp bin dd --count=0xB800 --const=0 >>dos.d13
java -cp bin CreateCatalog --version=310 --used=37 >>dos.d13
java -cp bin dd --count=0xDD00 --const=0 >>dos.d13
java -cp bin DosMasterToImage dos.d13 dos.dsk
