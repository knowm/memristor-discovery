#!/bin/bash

set -e

PACKAGER=/usr/local/Cellar/jdk.packager-osx/jpackager
INSTALLER_TYPE=pkg
INPUT=jar
OUTPUT=_exe
JAR=memristor-discovery-0.0.8-SNAPSHOT.jar
VERSION=0.0.8
APP_ICON=_exe/icons.icns
#MODULE_PATH=${3}
#FILE_ASSOCIATIONS=${8}
#EXTRA_BUNDLER_ARGUMENTS=${10}

${PACKAGER} \
  create-installer ${INSTALLER_TYPE} \
  --verbose \
  --echo-mode \
  --input "${INPUT}" \
  --output "${OUTPUT}" \
  --name Memristor-Discovery \
  --main-jar ${JAR} \
  --version ${VERSION} \
  --add-modules java.base,java.datatransfer,java.desktop,java.scripting,java.xml,jdk.jsobject,jdk.unsupported,jdk.unsupported.desktop,jdk.xml.dom,java.naming,jdk.charsets \
  --icon $APP_ICON \
  --jvm-args '-Xmx2048m' \
  --copyright "Knowm Inc."
#  --install-dir /Applications
#  --no-relocate
#  --mac-sign
#  --module-path ${MODULE_PATH} \
#  --file-associations ${FILE_ASSOCIATIONS} \
#  $EXTRA_BUNDLER_ARGUMENTS \
#  --class io.github.santulator.gui.main.SantulatorGuiExecutable