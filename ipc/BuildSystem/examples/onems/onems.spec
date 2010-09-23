Summary: OneMS Product
Name: %{component}
Version: %{VERS}
Release: 1
License: Proprietary
Group: Application
Requires: abc app1ci app1cpp app1flex app2cpp cmdqued lib1flex lib1java lib1php lib2java onems sys1scripts xyz

%description
%{SCM_BASELINE}
This is a pseudo-package that represents the OneMS product.
It "Requires" all the product's components.

%install
%ipc_make_install

%files
%{_bindir}
%{_sbindir}
%{_libdir}
%{_libexecdir}
%{_includedir}
%{_docdir}
%{_datadir}
%{_localedir}
%{_mandir}
%{_htdocsdir}
%{_sysconfdir}
%{_localstatedir}
%{_sharedstatedir}
