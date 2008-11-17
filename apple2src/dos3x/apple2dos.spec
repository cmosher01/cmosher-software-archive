Summary: Apple ][ Disk Operating System Software (DOS 3.3)
Name: apple2dos
Version: 1.0
Release: 1
Source: %{name}-%{version}.tar.gz
License: Copyright Apple Computer, Inc., and others
Group: System/Emulators/Other
BuildArch: noarch
BuildPrereq: cc65
BuildPrereq: cc65-apple2

%description
This package contains Disk Operating System Software (DOS 3.3) from the Apple ][ series computers,
as built from assembly-language sources. It includes all released versions of DOS, and
a full set of disk images: slave disks, master disks, and System Master disks.

%prep
%setup -q

%build
./configure -p %{_prefix}
make

%install
rm -Rf %{buildroot}/*
make DESTDIR=%{buildroot} install

%clean
rm -Rf %{buildroot}/*

%files
%{_libdir}/apple2/dos3x/
/apple2/dos3x/13sector/controller
/apple2/dos3x/13sector/software/dos310
/apple2/dos3x/13sector/software/dos320
/apple2/dos3x/13sector/software/dos321
/apple2/dos3x/13sector/disks
/apple2/dos3x/13sector/disks/dos310
/apple2/dos3x/13sector/disks/dos310
/apple2/dos3x/13sector/disks/dos310
/apple2/dos3x/13sector/disks/dos310
/apple2/dos3x/13sector/disks/dos320
b/apple2/dos3x/13sector/disks/dos320
        cp 13sector/disks/dos320/clean320.d13 $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks/dos320
        cp 13sector/disks/dos320/clean320.nib $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks/dos320
        cp $(VPATH)/disks/dos321/*.d13 $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks/dos321
        cp $(VPATH)/disks/dos321/*.nib $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks/dos321
        cp 13sector/disks/dos321/clean321.d13 $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks/dos321
        cp 13sector/disks/dos321/clean321.nib $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks/dos321

        cp 16sector/controller/disk2.ex65 $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/controller
        cp 16sector/software/dos330/dos.ex65 $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/software/dos330
        cp 16sector/software/dos331/dos.ex65 $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/software/dos331
        cp 16sector/software/dos332/dos.ex65 $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/software/dos332

        cp $(VPATH)/disks/README $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks
        cp $(VPATH)/disks/dos330/*.do $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos330
        cp $(VPATH)/disks/dos330/*.nib $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos330
        cp 16sector/disks/dos330/clean330.do $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos330
        cp 16sector/disks/dos330/clean330.nib $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos330
        cp $(VPATH)/disks/dos331/*.do $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos331
        cp $(VPATH)/disks/dos331/*.nib $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos331
        cp 16sector/disks/dos331/clean331.do $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos331
        cp 16sector/disks/dos331/clean331.nib $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos331
        cp $(VPATH)/disks/dos332/*.do $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos332
        cp $(VPATH)/disks/dos332/*.nib $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos332
        cp 16sector/disks/dos332/clean332.do $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos332
        cp 16sector/disks/dos332/clean332.nib $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos332

