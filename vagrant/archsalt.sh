#!/bin/sh

cd
cd abs

curl -O https://aur.archlinux.org/packages/py/python2-ply/python2-ply.tar.gz
tar xzvf python2-ply.tar.gz
cd python2-ply
makepkg --noconfirm -is
cd ..

curl -O https://aur.archlinux.org/packages/py/python2-pycparser/python2-pycparser.tar.gz
tar xzvf python2-pycparser.tar.gz
cd python2-pycparser
makepkg --noconfirm -is
cd ..

curl -O https://aur.archlinux.org/packages/py/python2-cffi/python2-cffi.tar.gz
tar xzvf python2-cffi.tar.gz
cd python2-cffi
makepkg --noconfirm -is
cd ..

curl -O https://aur.archlinux.org/packages/py/python2-msgpack/python2-msgpack.tar.gz
tar xzvf python2-msgpack.tar.gz
cd python2-msgpack
makepkg --noconfirm -is
cd ..

curl -O https://aur.archlinux.org/packages/sa/salt/salt.tar.gz
tar xzvf salt.tar.gz
cd salt
makepkg --noconfirm -is
cd ..
