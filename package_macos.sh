#!/bin/bash

set -e

PACKAGER=/usr/local/Cellar/jdk.packager-osx/jpackager
INSTALLER_TYPE=pkg
INPUT=jar
OUTPUT=target
JAR=memristor-discovery-0.0.9.jar
VERSION=0.0.9
APP_ICON=_exe/icons.icns

${PACKAGER} \
  create-installer ${INSTALLER_TYPE} \
  --verbose \
  --echo-mode \
  --input "${INPUT}" \
  --output "${OUTPUT}" \
  --name Memristor-Discovery \
  --main-jar ${JAR} \
  --version ${VERSION} \
  --add-modules java.base,java.datatransfer,java.desktop,java.logging,java.prefs,java.xml,jdk.xml.dom \
  --icon $APP_ICON \
  --jvm-args '-Xmx2048m' \
  --copyright "Knowm Inc."