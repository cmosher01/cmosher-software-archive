Summary: a couple of scripts
Name: %{component}
Version: %{VERS}
Release: 1
License: Proprietary
Group: Application

%description
%{SCM_BASELINE}
sys1scripts: just some scripts

%install
%ipc_make_install

%files
%{_bindir}/*
