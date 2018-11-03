Summary: Testing
Name: testing
Version: %{VERS}
Release: 1
License: GPL
Group: Applications/Test
Source: foo

%description
Testing description.

%build
touch foo

%install
mkdir -pv $RPM_BUILD_ROOT/%{_bindir}/
cat foo
cp -v foo $RPM_BUILD_ROOT/%{_bindir}/
mkdir -p $RPM_BUILD_ROOT/%{_sharedstatedir}/%{name}/rpm/
echo "%{version}" >$RPM_BUILD_ROOT/%{_sharedstatedir}/%{name}/rpm/latest-version

%files
%{_bindir}/foo
%{_sharedstatedir}/%{name}

%preun
echo "%{name} %preun $@"
latest=`cat %{_sharedstatedir}/%{name}/rpm/latest-version`
echo "installing version $latest"
echo "removing version %{version}"
if [[ "%{version}" > "$latest" ]] ; then
  echo "WARNING: Installing an older version ($latest) than we are removing (%{version})"
  echo "Therefore, treating this as a ROLLBACK."
fi
