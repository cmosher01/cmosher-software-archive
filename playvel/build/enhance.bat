set WEBINF=..\web\WEB-INF
set BIN=%WEBINF%\classes
set SHARED_LIB=H:\tomcat\jakarta-tomcat-base\shared\lib
set JDO=%BIN%\nu\mine\mosher\playvel\package.jdo
set CLASSPATH=%BIN%
set CLASSPATH=%CLASSPATH%;%SHARED_LIB%\jdo.jar
set CLASSPATH=%CLASSPATH%;%SHARED_LIB%\jpox-1.0.0-beta-3.jar
set CLASSPATH=%CLASSPATH%;%SHARED_LIB%\jdori.jar
set CLASSPATH=%CLASSPATH%;%SHARED_LIB%\log4j-1.2.8.jar
set SRC=%BIN%
set SRC=%SRC%;%SHARED_LIB%\jdo.jar
C:\Progra~1\Java\j2sdk1.4.2\bin\java -cp %CLASSPATH% org.jpox.enhance.SunReferenceEnhancer -v -s %SRC% %JDO% -d %BIN%
