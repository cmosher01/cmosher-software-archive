Summary: Xyz library
Name: %{component}
Version: %{VERS}
Release: 1
License: Proprietary
Group: Application
AutoReqProv: no

%description
%{SCM_BASELINE}
A simple shared-library example.

%install
%ipc_make_install

%files
%{_libdir}/*
