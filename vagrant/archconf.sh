#!/bin/sh

umount -vl /run

# etc
cd /etc
echo "vagrant" >hostname
ln -s /usr/share/zoneinfo/UTC localtime
sed -i -e "/^HOOKS/ s/block/block lvm2/" mkinitcpio.conf
echo 'LANG="en_US.UTF-8"' >> locale.conf
echo 'en_US.UTF-8 UTF-8'  >> locale.gen
locale-gen
mkinitcpio -p linux

# grub
pacman --noconfirm -S grub-bios
grub-install --target=i386-pc --recheck /dev/sda

cd /etc/default
sed -i -e '/CMDLINE_LINUX/ s/quiet//' grub
sed -i -e '/PRELOAD_MODULES/ s/part_msdos/lvm/' grub
sed -i -e '/DISABLE_LINUX_UUID/ s/#//' grub
sed -i -e '/TERMINAL_OUTPUT/ s/#//' grub
sed -i -e '/SAVEDEFAULT/ s/#//' grub
sed -i -e '/SAVEDEFAULT/ s/true/false/' grub

cd /boot/grub/locale
ln -s en\@quot.mo en.mo
cd ..
rm grubenv
rm grub.cfg.example
grub-mkconfig >grub.cfg
