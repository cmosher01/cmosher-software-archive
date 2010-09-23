Summary: some php library
Name: %{component}
Version: %{VERS}
Release: 1
License: Proprietary
Group: Application

%description
%{SCM_BASELINE}
A php library with some things in it.

%install
%ipc_make_install

%files
%defattr(0444,apache,apache,0555)
%{_htdocsdir}/%{name}
