set SBT_OPTS=-Xmx1024M -Dinput.encoding=Cp1252 -Dfile.encoding=SJIS -XX:+CMSClassUnloadingEnabled

java %SBT_OPTS% -jar sbt-launch.jar %*
