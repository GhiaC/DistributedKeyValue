#!/bin/bash
args=$@
SBT_OPTS="-Xms512M -Xmx8G -Xss4M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=8G -Dsbt.repository.config=repositories -Dsbt.override.build.repos=true"
echo 'You can use "df" for debug from first, "dm" to debug in the middle, "pr" to use proxy by SOCKSSERVER'
for var in "$@"
do
    if [[ $var == "df" ]]; then
	SBT_OPTS=$SBT_OPTS" -DuseCinnamon=false -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005 " 
	args=${args#$var}
    fi
    if [[ $var == "dm" ]]; then
	SBT_OPTS=$SBT_OPTS" -DuseCinnamon=false -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 " 
	args=${args#$var}
    fi
    if [[ $var == "pr" ]]; then
	SBT_OPTS=$SBT_OPTS" -DsocksProxyHost=SOCKSSERVER -DsocksProxyPort=1080 " 
	args=${args#$var}
    fi

done
java $SBT_OPTS -jar `dirname $0`/sbt-launch.jar "$args"
