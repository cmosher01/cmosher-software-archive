Summary: simple C++ application
Name: %{component}
Version: %{VERS}
Release: 1
License: Proprietary
Group: Application
AutoReqProv: no
Requires: abc

%description
%{SCM_BASELINE}
A little C++ application that links against a static library.

%install
%ipc_make_install

%files
%{_bindir}/*
