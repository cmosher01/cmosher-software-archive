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
%configure
make

%install
rm -Rf %{buildroot}/*
%makeinstall

%clean
rm -Rf %{buildroot}/*

%files
%{_bindir}/%{name}
%{_sysconfdir}/%{name}/%{name}.conf
%{_sysconfdir}/%{name}/%{name}.conf.rev0bare
%{_sysconfdir}/%{name}/%{name}.conf.a2bare
%{_sysconfdir}/%{name}/%{name}.conf.a2dos31
%{_sysconfdir}/%{name}/%{name}.conf.a2dos33
%{_sysconfdir}/%{name}/%{name}.conf.a2loaded
%{_sysconfdir}/%{name}/%{name}.conf.a2pbare
%{_sysconfdir}/%{name}/%{name}.conf.a2pdos33
%{_sysconfdir}/%{name}/%{name}.conf.a2ploaded

