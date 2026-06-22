#!/bin/sh
# Maven Wrapper Script
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.

set -e

# OS specific support.
cygwin=false
darwin=false
mingw=false
msys=false
case "$(uname)" in
  CYGWIN*) cygwin=true;;
  Darwin*) darwin=true;;
  MINGW*) mingw=true;;
  MSYS*) msys=true;;
esac

# Attempt to set APP_HOME
PRG="$0"
while [ -h "$PRG" ]; do
  ls=$(ls -ld "$PRG")
  link=$(expr "$ls" : '.*-> \(.*\)$')
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=$(dirname "$PRG")"/$link"
  fi
done
SAVED="$(pwd)"
cd "$(dirname "$PRG")/" >/dev/null
APP_HOME="$(pwd -P)"
cd "$SAVED" >/dev/null

APP_NAME="Maven"
APP_BASE_NAME=$(basename "$0")

MAVEN_WRAPPER_PROPERTIES_PATH="$APP_HOME/.mvn/wrapper/maven-wrapper.properties"

MAVEN_WRAPPER_JAR_PATH="$APP_HOME/.mvn/wrapper/maven-wrapper.jar"

# Default JVM options
DEFAULT_JVM_OPTS=""

# Use JAVA_HOME if set, otherwise try to find java on PATH
if [ -n "$JAVA_HOME" ]; then
  if [ -x "$JAVA_HOME/jre/sh/java" ]; then
    JAVACMD="$JAVA_HOME/jre/sh/java"
  else
    JAVACMD="$JAVA_HOME/bin/java"
  fi
  if [ ! -x "$JAVACMD" ]; then
    die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME"
  fi
else
  JAVACMD="java"
  which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found."
fi

# Read wrapper properties
DISTRIBUTION_URL=$(grep "distributionUrl" "$MAVEN_WRAPPER_PROPERTIES_PATH" | sed 's/distributionUrl=//')
MAVEN_USER_HOME="${MAVEN_USER_HOME:-${HOME}/.m2}"
MAVEN_WRAPPER_CACHE="${MAVEN_USER_HOME}/wrapper"

# Determine the Maven version from the URL
MAVEN_VERSION=$(echo "$DISTRIBUTION_URL" | sed 's/.*apache-maven-\([0-9.]*\)-bin.zip/\1/')
MAVEN_DIST_DIR="$MAVEN_WRAPPER_CACHE/dists/apache-maven-${MAVEN_VERSION}"

if [ ! -d "$MAVEN_DIST_DIR" ]; then
  echo "Downloading Apache Maven ${MAVEN_VERSION}..."
  mkdir -p "$MAVEN_DIST_DIR"
  TMP_ZIP="${MAVEN_DIST_DIR}/maven.zip"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$DISTRIBUTION_URL" -o "$TMP_ZIP"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$DISTRIBUTION_URL" -O "$TMP_ZIP"
  else
    echo "ERROR: Neither curl nor wget is available." >&2
    exit 1
  fi
  unzip -q "$TMP_ZIP" -d "$MAVEN_DIST_DIR"
  rm -f "$TMP_ZIP"
fi

MAVEN_HOME=$(find "$MAVEN_DIST_DIR" -maxdepth 1 -name "apache-maven-*" -type d | head -1)
MAVEN_CMD="${MAVEN_HOME}/bin/mvn"

exec "$MAVEN_CMD" "$@"

