Summary: A simple static library example
Name: %{component}
Version: %{VERS}
Release: 1
License: Proprietary
Group: Application
AutoReqProv: no

%description
%{SCM_BASELINE}
A simple example of a static library.

%install
%ipc_make_install

%files
%{_libdir}/*
