Name: ipc-service_uda
Version: %PRODUCT_VERSION
Release: 1
Summary: UDA Services
Group: UDA
License: Commercial



%description
UDA service .jar and .war files.



%install
rm -rf $RPM_BUILD_ROOT
mkdir -p $RPM_BUILD_ROOT/opt/ipc/service_uda
mkdir -p $RPM_BUILD_ROOT/etc/opt/ipc/service_uda
cp $RPM_BUILD_DIR/*.jar $RPM_BUILD_ROOT/opt/ipc/service_uda
cp $RPM_BUILD_DIR/*.war $RPM_BUILD_ROOT/opt/ipc/service_uda



%post
if [ "$1" -eq 1 ]
then
	ln -sf /opt/ipc/service_uda/service_uda_diag.war /opt/ipc/dunkin_ear/service_uda_diag.war
	ln -sf /opt/ipc/service_uda/service_uda_web.war /opt/ipc/dunkin_ear/service_uda_web.war
	ln -sf /opt/ipc/service_uda/service_uda_ejb.jar /opt/ipc/dunkin_ear/service_uda_ejb.jar
	ln -sf /opt/ipc/service_uda/service_uda_utils.jar /opt/ipc/dunkin_ear/APP-INF/lib/service_uda_utils.jar
	ln -sf /opt/ipc/service_uda/service_uda_message_types.jar /opt/ipc/dunkin_ear/APP-INF/lib/service_uda_message_types.jar
	ln -sf /opt/ipc/service_uda/service_uda_exec_framework.jar /opt/ipc/dunkin_ear/APP-INF/lib/service_uda_exec_framework.jar
	ln -sf /opt/ipc/service_uda/service_uda_event_framework.jar /opt/ipc/dunkin_ear/APP-INF/lib/service_uda_event_framework.jar
	ln -sf /opt/ipc/service_uda/service_uda_ejb_client.jar /opt/ipc/dunkin_ear/APP-INF/lib/service_uda_ejb_client.jar
	ln -sf /opt/ipc/service_uda/service_uda_core.jar /opt/ipc/dunkin_ear/APP-INF/lib/service_uda_core.jar
fi



%postun
if [ "$1" -eq 0 ]
then
	rm -f /opt/ipc/dunkin_ear/service_uda_diag.war
	rm -f /opt/ipc/dunkin_ear/service_uda_web.war
	rm -f /opt/ipc/dunkin_ear/service_uda_ejb.jar
	rm -f /opt/ipc/dunkin_ear/APP-INF/lib/service_uda_utils.jar
	rm -f /opt/ipc/dunkin_ear/APP-INF/lib/service_uda_message_types.jar
	rm -f /opt/ipc/dunkin_ear/APP-INF/lib/service_uda_exec_framework.jar
	rm -f /opt/ipc/dunkin_ear/APP-INF/lib/service_uda_event_framework.jar
	rm -f /opt/ipc/dunkin_ear/APP-INF/lib/service_uda_ejb_client.jar
	rm -f /opt/ipc/dunkin_ear/APP-INF/lib/service_uda_core.jar
fi



%files
%defattr(0444,root,root,0554)
%attr(0550,root,root) /opt/ipc/service_uda
%attr(0644,root,root) /etc/opt/ipc/service_uda
