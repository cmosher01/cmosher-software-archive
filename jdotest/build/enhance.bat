set W=..\web\WEB-INF
set LIB=%W%\lib
set BIN=%W%\classes
set CLASSPATH=%LIB%\jdo.jar
set CLASSPATH=%CLASSPATH%;%LIB%\jdori.jar
set CLASSPATH=%CLASSPATH%;%LIB%\jpox-1.0.0-beta-2.jar
set CLASSPATH=%CLASSPATH%;%LIB%\log4j-1.2.8.jar
set CLASSPATH=%CLASSPATH%;%BIN%
set SRC=%BIN%
set SRC=%SRC%;%LIB%\jdo.jar
set JDO=%BIN%\nu\mine\mosher\jdotest\package.jdo
"C:\Program Files\Java\j2sdk1.4.2\bin\java" -cp %CLASSPATH% org.jpox.enhance.SunReferenceEnhancer -v -s %SRC% %JDO% -d %BIN%
