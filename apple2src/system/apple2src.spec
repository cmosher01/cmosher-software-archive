Summary: Apple ][ ROM
Name: apple2src
Version: 1.0
Release: 1
Source: %{name}-%{version}.tar.gz
License: Copyright Apple Computer, Inc., and others
Group: System/Emulators/Other
BuildArch: noarch
BuildPrereq: cc65
BuildPrereq: cc65-apple2

%description
This package contains ROM code from the Apple ][ and Apple ][ plus computers,
build from sources. It includes Applesoft BASIC, Integer BASIC, and two
versions of the System Monitor utility, the "old" monitor and the "autostart"
monitor.

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
%{_libdir}/apple2/intbasic/intbasic.ex65
%{_libdir}/apple2/other/other.ex65
%{_libdir}/apple2/applesoft/applesoft.ex65
%{_libdir}/apple2/monitor/apple2/monitor.ex65
%{_libdir}/apple2/monitor/apple2plus/monitor.ex65
