rem given dos 3.1 build exe image in file "dos", puts it into a clean nibble disk image "dos.nib"

java -cp bin dd --skip=0x1B00 <dos >dos.d13
java -cp bin dd --count=0x1B00 <dos >>dos.d13
java -cp bin dd --count=0xB800 --const=0 >>dos.d13
java -cp bin CreateCatalog --version=310 >>dos.d13
java -cp bin dd --count=0xDD00 --const=0 >>dos.d13

java -cp bin ConvertD13toNibble <dos.d13 >dos.nib
