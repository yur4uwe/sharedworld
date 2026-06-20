#!/usr/bin/env bash
set -e
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export GRADLE_USER_HOME=/tmp/sharedworld-gradle
GRADLE="bash $(dirname "$0")/../../gradlew --no-daemon -p $(dirname "$0")"

# Prime the dev-helper jar on first/clean build, then do the real build.
if [ ! -f "$(dirname "$0")/build/libs/sharedworld-0.1.1-dev-helper.jar" ]; then
  $GRADLE -PsharedworldSkipDevHelperRuntime=true devHelperJar
fi
$GRADLE remapJar
