#!/bin/sh

# root password
passwd <<EOF
vagrant
vagrant
EOF

# network
systemctl enable dhcpcd@enp0s3.service
systemctl start dhcpcd@enp0s3.service

# ssh
pacman --noconfirm -S openssh
systemctl enable sshd.service
systemctl start sshd.service

# sudo
pacman --noconfirm -S sudo
sed -i -e '/wheel.*NOPASSWD/ s/^#//' /etc/sudoers

# vagrant user
useradd -m -g users -G wheel,audio,video,storage,power -s /bin/bash vagrant
passwd vagrant <<EOF
vagrant
vagrant
EOF

# vagrant keys
cd /home/vagrant
mkdir .ssh
chmod 0700 .ssh
chown vagrant: .ssh
cd .ssh
curl 'https://raw.github.com/mitchellh/vagrant/master/keys/vagrant.pub' -o authorized_keys
chmod 0600 authorized_keys
chown vagrant: authorized_keys
cd

# virtualbox guest additions
pacman --noconfirm -S virtualbox-guest-modules
pacman --noconfirm -S --nodeps --nodeps virtualbox-guest-utils
modprobe -a vboxguest vboxsf vboxvideo
cat <<EOF >/etc/modules-load.d/virtualbox.conf
vboxguest
vboxsf
vboxvideo
EOF
