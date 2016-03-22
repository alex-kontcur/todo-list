SET JAVA_OPTS=-Xmx512M -Done-jar.silent=true
SET CP="%~dp0todo-list.jar;%~dp0conf"

start javaw %JAVA_OPTS% -classpath %CP% OneJar -CMETA-INF/spring/todolist-context.xml