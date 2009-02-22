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
make DESTDIR=%{buildroot} install

%files
%{_libdir}/epple2/cards
