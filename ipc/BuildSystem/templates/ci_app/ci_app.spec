Summary: ???SHORT SUMMARY???
Name: %{component}
Version: %{VERS}
Release: 1
Source: %{name}-%{version}.tgz
License: Propretary
Group: Application
AutoReqProv: no
Requires: codeigniter_1.6.3

%description
%{SCM_BASELINE}
???LONG DESCRIPTION???

%prep
%setup -q

%build
cd ../../..
make -f rpm/BUILD/%{name}-%{version}/Makefile

%install
cd ../../..
make -f rpm/BUILD/%{name}-%{version}/Makefile install DESTDIR=%{buildroot}

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
