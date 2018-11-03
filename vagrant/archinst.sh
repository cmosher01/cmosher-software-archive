#!/bin/sh

usage()
{
  echo "$0"
  echo ""
  echo "  Initialize an Arch Linux system on /dev/sda"
  echo ""
  echo "usage:"
  echo ""
  echo "  $0 [ -fh ]"
  echo ""
  echo "options:"
  echo ""
  echo "  -f       force execution !!! DANGER: ERASES /dev/sda !!!"
  echo "  -h       display this help, and exit"
}

# check options and argument
force=false
while getopts 'f' opt
do
  case ${opt} in
    f) force=true ;;
    h) usage ; exit ;;
    \?) usage ; exit 1 ;;
  esac
done

shift $((OPTIND-1))

if ${force}
then

# disk partitions
sgdisk -Z /dev/sda
sgdisk -n 1:1M:+2M -t 1:EF02 -c 1:grub /dev/sda
sgdisk -n 2:4M:0   -t 2:8E00 -c 2:pv   /dev/sda

# logical volumes
pvcreate /dev/sda2
vgcreate arch /dev/sda2
lvcreate -L 256M -n boot arch
lvcreate -L  24G -n root arch
lvcreate -L   4G -n var  arch
lvcreate -L  24G -n home arch

# file systems
mkfs.ext4 -v /dev/arch/boot
mkfs.ext4 -v /dev/arch/root
mkfs.ext4 -v /dev/arch/var
mkfs.ext4 -v /dev/arch/home

# mount new volumes
mount /dev/arch/root /mnt
cd /mnt
mkdir boot var home
mount /dev/arch/boot boot
mount /dev/arch/var  var
mount /dev/arch/home home
cd /

# install arch
pacstrap /mnt

# pre-configure
genfstab -p /mnt | awk '/^\// { print $1,"\t",$2,"\t",$3,"\t rw,noatime \t",$5,"\t",$6; }' >> /mnt/etc/fstab

mkdir /mnt/run/lvm
mount -o bind /run/lvm /mnt/run/lvm

else
  usage
fi
