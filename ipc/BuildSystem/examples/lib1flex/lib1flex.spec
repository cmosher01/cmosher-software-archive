Summary: a flex library
Name: %{component}
Version: %{VERS}
Release: 1
License: Proprietary
Group: Application

%description
%{SCM_BASELINE}
lib1flex library (swc file)

%install
%ipc_make_install

%files
%{_libdir}/%{name}
