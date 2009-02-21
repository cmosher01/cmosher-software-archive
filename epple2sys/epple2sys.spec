Summary: EPPLE ][ System ROM
Name: epple2sys
Version: 1.0
Release: 1
Source: %{name}-%{version}.tar.gz
License: GPL
Group: System/Emulators/Other
BuildArch: noarch

%description
Demo free ROMs for EPPLE ][ emulator.
Not compatible with any Apple ][ software.
Just some free (GPLv3) ROM code to demo the emulator.

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
%{_libdir}/epple2/system
