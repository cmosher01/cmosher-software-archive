Summary: some java library
Name: %{component}
Version: %{VERS}
Release: 1
License: Proprietary
Group: Application

%description
%{SCM_BASELINE}
a java library (jar file) with some stuff in it

%install
%ipc_make_install

%files
%{_libdir}/%{name}
