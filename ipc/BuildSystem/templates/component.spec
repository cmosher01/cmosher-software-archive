Summary: ???SUMMARY???
Name: %{component}
Version: %{VERS}
Release: 1
License: Proprietary
Group: Application
AutoReqProv: no
# List required dependencies in Requires tag.
# Each will usually be just the name of the
# component that this component depends on.
# They may include a version number.
Requires: ???LIST OF COMPONENT DEPENDENCIES???

%description
%{SCM_BASELINE}
???LONG DESCRIPTION???

%install
%ipc_make_install

%files
#specify any default attributes, for example:
%defattr(0444,apache,apache,0555)
# List of directories or files to install.
# Use the predefined target directory macros
# (defined in directories.mk).
# See some examples below.
# Remember that anything listed here will be
# "owned" by this RPM, so don't put in something
# like "/bin" because other RPMS will surely by
# using that. Instead, use "/bin/*", for example,
# which will pick up only the *files* that this
# RPM is installing into /bin, and won't pick up
# /bin itself.
# Common examples:
# Simple executable files or scripts are in bin:
%{_bindir}/*
# Daemons are in sbin:
%{_sbindir}/*
# Shared libraries are in lib
%{_libdir}/*
# Other library files (.jar, .swc) are in lib/component
%{_libdir}/%{name}
# Web applications or html pages are in the
# share/htdocs/component directory
%{_htdocsdir}/%{name}
# config files are in /etc
%{_sysconfdir}/*
# or /etc/component
%{_sysconfdir}/%{name}



# Put any pre or post scripts here.
# Remember to check $1 if necessary to determine
# if this is the first installation, or just
# an upgrade of an already installed package, etc.
# Here are some examples that create/delete a symlink.
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
