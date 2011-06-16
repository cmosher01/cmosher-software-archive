Summary: EPPLE ][ Cards
Name: epple2cards
Version: 1.0
Release: 1
Source: %{name}-%{version}.tar.gz
License: GPL
Group: System/Emulators/Other
BuildArch: noarch
BuildPrereq: cc65
BuildPrereq: cc65-apple2

%description
ROM for stdio, stdout, and clock cards, for the EPPLE ][ emulator.

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
%{_libdir}/epple2/cards
