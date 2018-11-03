#!/bin/sh

set -e

archboot=2014.01.05

usage()
{
  echo "$0"
  echo ""
  echo "  Create a VM for installing Arch Linux"
  echo ""
  echo "usage:"
  echo ""
  echo "  $0 [ -fh ] [ -n name ]"
  echo ""
  echo "options:"
  echo ""
  echo "  -f       force execution (if omitted, then dry-run)"
  echo "  -h       display this help, and exit"
  echo "  -n name  name for the new VM. Defaults to vbox-YYYYMMDDHHMMSS in UTC."
}

cmd()
{
  if ${force}
  then
    $1
  else
    printf "%s\n" "$1"
  fi
}

# check options and argument
force=false
vm=vbox-`date -u +%Y%m%d%H%M%S`
while getopts 'fn:' opt
do
  case ${opt} in
    f) force=true ;;
    n) vm=${OPTARG} ;;
    h) usage ; exit ;;
    \?) usage ; exit 1 ;;
  esac
done

shift $((OPTIND-1))

if [ $# -ne 0 ]
then
  usage
  exit 1
fi



if ${force}
then
  :
else
  echo ""
  echo "#************************************"
  echo "#    DRY RUN"
  echo "#************************************"
  echo "# use '-f' to actually run"
  echo "#************************************"
  echo ""
fi





echo "finding bootable iso image..."

archive=http://mirror.rit.edu/archlinux
image=archlinux-${archboot}-dual.iso
if [ -r /etc/pacman.d/mirrorlist ]
then
  archive=`grep ^Server /etc/pacman.d/mirrorlist | head -n 1 | cut -d = -f 2 | tr -d [:space:] | sed -e 's,/$repo/os/$arch,,'`
fi
printf 'using archive: %s\n' "${archive}"

if curl ${archive}/iso/${archboot}/md5sums.txt | grep ${image} | md5sum -c
then
  echo "using existing iso image"
else
  cmd "curl -O ${archive}/iso/${archboot}/${image}"
fi





echo "creating and configuring VM..."

cmd "vboxmanage createvm --name ${vm} --ostype ArchLinux_64 --basefolder `pwd` --register"
cmd "cd ${vm}"
cmd "vboxmanage modifyvm ${vm} --rtcuseutc on --memory 256 --nic1 nat"
cmd "vboxmanage storagectl ${vm} --name SATA --add sata --portcount 2 --hostiocache off --bootable on"
cmd "vboxmanage createhd --filename archlinux.vdi --size 65536"
cmd "vboxmanage storageattach ${vm} --storagectl SATA --port 0 --device 0 --type hdd --medium archlinux.vdi"
cmd "vboxmanage storageattach ${vm} --storagectl SATA --port 1 --device 0 --type dvddrive --medium ../${image}"






# misc commands:

# start the vm
#vboxmanage startvm ${vm}

# send keyboard scancodes (this example is for Enter)
#vboxmanage controlvm ${vm} keyboardputscancode 1C 9C

#after setup is done, remove the iso image:
#vboxmanage storageattach ${vm} --storagectl SATA --port 1 --device 0 --medium none
#vboxmanage closemedium dvd archlinux-2014.01.05-dual.iso

# cleanly shutdown the vm
#vboxmanage controlvm ${vm} acpipowerbutton
