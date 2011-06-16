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
cd ../../VPATH
../BUILD/%{name}-%{version}/configure -p %{_prefix}
make

%install
cd ../../VPATH
make DESTDIR=%{buildroot} install

%files
%{_libdir}/apple2/dos3x
