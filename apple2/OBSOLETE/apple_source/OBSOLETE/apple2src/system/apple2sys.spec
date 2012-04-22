Summary: Apple ][ System Software (ROM)
Name: apple2sys
Version: 1.0
Release: 1
Source: %{name}-%{version}.tar.gz
License: Copyright Apple Computer, Inc., and others
Group: System/Emulators/Other
BuildArch: noarch
BuildPrereq: cc65
BuildPrereq: cc65-apple2

%description
This package contains System Software (ROM code) from the Apple ][ and Apple ][ plus computers,
as built from assembly-language sources. It includes Applesoft BASIC, Integer BASIC, and two
versions of the System Monitor utility, the "old" monitor and the "autostart"
monitor.

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
%{_libdir}/apple2/system
