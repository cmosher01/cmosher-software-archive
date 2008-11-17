Summary: Apple ][ Disk Operating System Software (DOS 3.3)
Name: apple2dos
Version: 1.0
Release: 1
Source: %{name}-%{version}.tar.gz
License: Copyright Apple Computer, Inc., and others
Group: System/Emulators/Other
BuildArch: noarch
BuildPrereq: cc65
BuildPrereq: cc65-apple2

%description
This package contains Disk Operating System Software (DOS 3.3) from the Apple ][ series computers,
as built from assembly-language sources. It includes all released versions of DOS, and
a full set of disk images: slave disks, master disks, and System Master disks.

%prep
%setup -q

%build
./configure -p %{_prefix}
make

%install
rm -Rf %{buildroot}/*
make DESTDIR=%{buildroot} install

%clean
rm -Rf %{buildroot}/*

%files
%{_libdir}/apple2/dos3x/13sector/controller/disk2.ex65
%{_libdir}/apple2/dos3x/13sector/disks/README
%{_libdir}/apple2/dos3x/13sector/disks/dos310/clean310.d13
%{_libdir}/apple2/dos3x/13sector/disks/dos310/clean310.nib
%{_libdir}/apple2/dos3x/13sector/disks/dos310/clean31sysmas_stock_rawdos.d13
%{_libdir}/apple2/dos3x/13sector/disks/dos310/clean31sysmas_stock_rawdos.nib
%{_libdir}/apple2/dos3x/13sector/disks/dos310/original31sysmas.d13
%{_libdir}/apple2/dos3x/13sector/disks/dos310/original31sysmas.nib
%{_libdir}/apple2/dos3x/13sector/disks/dos310/stock31init.d13
%{_libdir}/apple2/dos3x/13sector/disks/dos310/stock31init.nib
%{_libdir}/apple2/dos3x/13sector/disks/dos310/stock31mastercreated.d13
%{_libdir}/apple2/dos3x/13sector/disks/dos310/stock31mastercreated.nib
%{_libdir}/apple2/dos3x/13sector/disks/dos320/clean320.d13
%{_libdir}/apple2/dos3x/13sector/disks/dos320/clean320.nib
%{_libdir}/apple2/dos3x/13sector/disks/dos320/clean32sysmaspls.d13
%{_libdir}/apple2/dos3x/13sector/disks/dos320/clean32sysmaspls.nib
%{_libdir}/apple2/dos3x/13sector/disks/dos320/clean32sysmasstd.d13
%{_libdir}/apple2/dos3x/13sector/disks/dos320/clean32sysmasstd.nib
%{_libdir}/apple2/dos3x/13sector/disks/dos320/original32sysmaspls.d13
%{_libdir}/apple2/dos3x/13sector/disks/dos320/original32sysmaspls.nib
%{_libdir}/apple2/dos3x/13sector/disks/dos320/original32sysmasstd.d13
%{_libdir}/apple2/dos3x/13sector/disks/dos320/original32sysmasstd.nib
%{_libdir}/apple2/dos3x/13sector/disks/dos320/stock32init.d13
%{_libdir}/apple2/dos3x/13sector/disks/dos320/stock32init.nib
%{_libdir}/apple2/dos3x/13sector/disks/dos320/stock32mastercreated.d13
%{_libdir}/apple2/dos3x/13sector/disks/dos320/stock32mastercreated.nib
%{_libdir}/apple2/dos3x/13sector/disks/dos321/clean321.d13
%{_libdir}/apple2/dos3x/13sector/disks/dos321/clean321.nib
%{_libdir}/apple2/dos3x/13sector/disks/dos321/clean321sysmaspls.d13
%{_libdir}/apple2/dos3x/13sector/disks/dos321/clean321sysmaspls.nib
%{_libdir}/apple2/dos3x/13sector/disks/dos321/clean321sysmasstd.d13
%{_libdir}/apple2/dos3x/13sector/disks/dos321/clean321sysmasstd.nib
%{_libdir}/apple2/dos3x/13sector/disks/dos321/original321sysmaspls.d13
%{_libdir}/apple2/dos3x/13sector/disks/dos321/original321sysmaspls.nib
%{_libdir}/apple2/dos3x/13sector/disks/dos321/original321sysmasstd.d13
%{_libdir}/apple2/dos3x/13sector/disks/dos321/original321sysmasstd.nib
%{_libdir}/apple2/dos3x/13sector/disks/dos321/stock321init.d13
%{_libdir}/apple2/dos3x/13sector/disks/dos321/stock321init.nib
%{_libdir}/apple2/dos3x/13sector/disks/dos321/stock321mastercreated.d13
%{_libdir}/apple2/dos3x/13sector/disks/dos321/stock321mastercreated.nib
%{_libdir}/apple2/dos3x/13sector/software/dos310/dos.ex65
%{_libdir}/apple2/dos3x/13sector/software/dos320/dos.ex65
%{_libdir}/apple2/dos3x/13sector/software/dos321/dos.ex65
%{_libdir}/apple2/dos3x/16sector/controller/disk2.ex65
%{_libdir}/apple2/dos3x/16sector/disks/README
%{_libdir}/apple2/dos3x/16sector/disks/dos330/clean330.do
%{_libdir}/apple2/dos3x/16sector/disks/dos330/clean330.nib
%{_libdir}/apple2/dos3x/16sector/disks/dos330/clean330sysmas.do
%{_libdir}/apple2/dos3x/16sector/disks/dos330/clean330sysmas.nib
%{_libdir}/apple2/dos3x/16sector/disks/dos330/original330sysmas.do
%{_libdir}/apple2/dos3x/16sector/disks/dos330/original330sysmas.nib
%{_libdir}/apple2/dos3x/16sector/disks/dos330/stock330init.do
%{_libdir}/apple2/dos3x/16sector/disks/dos330/stock330init.nib
%{_libdir}/apple2/dos3x/16sector/disks/dos330/stock330mastercreated.do
%{_libdir}/apple2/dos3x/16sector/disks/dos330/stock330mastercreated.nib
%{_libdir}/apple2/dos3x/16sector/disks/dos331/clean331.do
%{_libdir}/apple2/dos3x/16sector/disks/dos331/clean331.nib
%{_libdir}/apple2/dos3x/16sector/disks/dos331/clean331sysmas.do
%{_libdir}/apple2/dos3x/16sector/disks/dos331/clean331sysmas.nib
%{_libdir}/apple2/dos3x/16sector/disks/dos331/original331sysmas.do
%{_libdir}/apple2/dos3x/16sector/disks/dos331/original331sysmas.nib
%{_libdir}/apple2/dos3x/16sector/disks/dos331/stock331init.do
%{_libdir}/apple2/dos3x/16sector/disks/dos331/stock331init.nib
%{_libdir}/apple2/dos3x/16sector/disks/dos331/stock331mastercreated.do
%{_libdir}/apple2/dos3x/16sector/disks/dos331/stock331mastercreated.nib
%{_libdir}/apple2/dos3x/16sector/disks/dos332/clean332.do
%{_libdir}/apple2/dos3x/16sector/disks/dos332/clean332.nib
%{_libdir}/apple2/dos3x/16sector/disks/dos332/clean332sysmas.do
%{_libdir}/apple2/dos3x/16sector/disks/dos332/clean332sysmas.nib
%{_libdir}/apple2/dos3x/16sector/disks/dos332/original332sysmas.do
%{_libdir}/apple2/dos3x/16sector/disks/dos332/original332sysmas.nib
%{_libdir}/apple2/dos3x/16sector/disks/dos332/stock332init.do
%{_libdir}/apple2/dos3x/16sector/disks/dos332/stock332init.nib
%{_libdir}/apple2/dos3x/16sector/disks/dos332/stock332mastercreated.do
%{_libdir}/apple2/dos3x/16sector/disks/dos332/stock332mastercreated.nib
%{_libdir}/apple2/dos3x/16sector/software/dos330/dos.ex65
%{_libdir}/apple2/dos3x/16sector/software/dos331/dos.ex65
%{_libdir}/apple2/dos3x/16sector/software/dos332/dos.ex65
