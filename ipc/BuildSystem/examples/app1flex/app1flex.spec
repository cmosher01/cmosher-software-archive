Summary: a flex application
Name: %{component}
Version: %{VERS}
Release: 1
License: Proprietary
Group: Application

%description
%{SCM_BASELINE}
some flex application (swf file)

%install
%ipc_make_install

%files
%{_libdir}/%{name}
