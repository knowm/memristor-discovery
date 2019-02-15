set INPUT=jar
set OUTPUT=_exe
set JAR=memristor-discovery-0.0.8.jar
set VERSION=0.0.8
set APP_ICON=_exe/icons.ico

call "%JAVA_HOME%\bin\java.exe" ^
    -Xmx512M ^
    --module-path "%JAVA_HOME%\jmods" ^
    --add-opens jdk.jlink/jdk.tools.jlink.internal.packager=jdk.packager ^
    -m jdk.packager/jdk.packager.Main ^
    create-image ^
    --verbose ^
    --echo-mode ^
    --add-modules "java.base,java.datatransfer,java.desktop,java.logging,java.prefs,java.xml,jdk.xml.dom" ^
    --input "%INPUT%" ^
    --output "%OUTPUT%" ^
    --name "Memristor-Discovery" ^
    --main-jar "%JAR%" ^
    --version "%VERSION%" ^
    --jvm-args "--add-opens javafx.base/com.sun.javafx.reflect=ALL-UNNAMED" ^
    --icon "%APP_ICON%" ^
    --copyright "Knowm Inc."
