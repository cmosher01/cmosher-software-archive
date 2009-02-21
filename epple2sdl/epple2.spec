Summary: EPPLE ][, The Emulated Apple ][
Name: epple2
Version: 1.0
Release: 1
Source: %{name}-%{version}.tar.gz
License: GPL
Group: System/Emulators/Other

%description
EPPLE ][ is an emulator of Apple ][ and Apple ][ plus computers.

%prep
%setup -q

%build
cd ../../VPATH
../BUILD/%{name}-%{version}/configure --prefix=%{_prefix}
make

%install
cd ../../VPATH
make install DESTDIR=%{buildroot}

%clean
#rm -Rf %{buildroot}/*

%files
%{_bindir}/%{name}
%{_sysconfdir}/%{name}
