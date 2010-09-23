Summary: cmdque daemon
Name: %{component}
Version: %{VERS}
Release: 1
License: Proprietary
Group: Application

%description
%{SCM_BASELINE}
A daemon that runs shell commands it reads from a pipe

%install
%ipc_make_install

%files
%{_sbindir}/*
