Summary: EPPLE ][ System ROM
Name: epple2sys
Version: 1.0
Release: 1
Source: %{name}-%{version}.tar.gz
License: GPL
Group: System/Emulators/Other
BuildArch: noarch
BuildPrereq: cc65
BuildPrereq: cc65-apple2

%description
Demo free ROMs for EPPLE ][ emulator.
Not compatible with any Apple ][ software.
Just some free (GPLv3) ROM code to demo the emulator.

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
%{_libdir}/epple2/system
