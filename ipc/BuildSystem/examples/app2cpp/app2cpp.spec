Summary: simple C++ application
Name: %{component}
Version: %{VERS}
Release: 1
License: Proprietary
Group: Application
AutoReqProv: no
Requires: xyz

%description
%{SCM_BASELINE}
A little C++ application that links against a shared library.

%install
%ipc_make_install

%files
%{_bindir}/*
