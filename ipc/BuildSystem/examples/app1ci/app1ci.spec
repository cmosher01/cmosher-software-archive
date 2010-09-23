Summary: app1ci application
Name: %{component}
Version: %{VERS}
Release: 1
License: Proprietary
Group: Application
AutoReqProv: no
Requires: codeigniter lib1php

%description
%{SCM_BASELINE}
Example CodeIgniter application

%install
%ipc_make_install

%files
%defattr(0444,apache,apache,0555)
%{_htdocsdir}/%{name}

%pre
if [ "$1" -eq 1 ]
then
	mkdir -p /var/www/html
	ln -s %{_htdocsdir}/%{name} /var/www/html
fi

%postun
if [ "$1" -eq 0 ]
then
	rm /var/www/html/%{name}
fi
