#!/bin/bash

set -e

PACKAGER=/usr/local/bin/jpackager/jpackager
INSTALLER_TYPE=deb
INPUT=jar
OUTPUT=target
JAR=memristor-discovery-2.0.0.jar
VERSION=2.0.0
APP_ICON=_exe/icon.png

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