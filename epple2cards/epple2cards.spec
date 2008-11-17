Summary: EPPLE ][ Cards
Name: epple2cards
Version: 1.0
Release: 1
Source: %{name}-%{version}.tar.gz
License: GPL
Group: System/Emulators/Other
BuildArch: noarch

%description
ROM for stdio, stdout, and clock cards, for the EPPLE ][ emulator.

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
%{_libdir}/epple2/cards/clock.ex65
%{_libdir}/epple2/cards/stdout.ex65
%{_libdir}/epple2/cards/stdin.ex65
