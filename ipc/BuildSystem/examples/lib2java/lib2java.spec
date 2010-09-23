Summary: another java library
Name: %{component}
Version: %{VERS}
Release: 1
License: Proprietary
Group: Application

%description
%{SCM_BASELINE}
lib2java library (jar file) that depends on lib1java library

%install
%ipc_make_install

%files
%{_libdir}/%{name}
