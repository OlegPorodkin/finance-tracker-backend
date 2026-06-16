#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.2
# ----------------------------------------------------------------------------

set -e

findBaseDir() {
  BASEDIR="$(pwd)"
  BASEDIR="$( dirname "$0")"
  iter=0
  while [ "$iter" -lt 10 ]; do
    if [ -f "$BASEDIR/.mvn/wrapper/maven-wrapper.properties" ]; then
      break
    fi
    BASEDIR="$(cd "$BASEDIR/.." && pwd)"
    iter=$((iter + 1))
  done
  if [ ! -f "$BASEDIR/.mvn/wrapper/maven-wrapper.properties" ]; then
    echo "maven-wrapper.properties not found" >&2
    exit 1
  fi
}
findBaseDir

WRAPPER_DIR="$BASEDIR/.mvn/wrapper"
WRAPPER_PROPERTIES="$WRAPPER_DIR/maven-wrapper.properties"

# Read the distributionUrl
distributionUrl=$(grep "^distributionUrl" "$WRAPPER_PROPERTIES" | cut -d= -f2-)
distributionUrl=$(echo "$distributionUrl" | tr -d '\r')

DISTRIBUTION_NAME=$(basename "$distributionUrl")
DISTRIBUTION_NAME_MAIN="${DISTRIBUTION_NAME%.zip}"
DISTRIBUTION_NAME_MAIN="${DISTRIBUTION_NAME_MAIN%-bin}"

if [ -z "$MAVEN_USER_HOME" ]; then
  MAVEN_USER_HOME="$HOME/.m2"
fi

DISTRIBUTION_PATH="$MAVEN_USER_HOME/wrapper/dists/$DISTRIBUTION_NAME_MAIN"
DISTRIBUTION_HOME="$DISTRIBUTION_PATH/$(ls "$DISTRIBUTION_PATH" 2>/dev/null | head -1)"

if [ ! -f "$DISTRIBUTION_HOME/bin/mvn" ]; then
  echo "Downloading Maven..."
  mkdir -p "$DISTRIBUTION_PATH"
  TMP_DOWNLOAD="$DISTRIBUTION_PATH/tmp_download_$$"
  if command -v curl > /dev/null 2>&1; then
    curl -fsSL "$distributionUrl" -o "$TMP_DOWNLOAD.zip"
  elif command -v wget > /dev/null 2>&1; then
    wget -q "$distributionUrl" -O "$TMP_DOWNLOAD.zip"
  else
    echo "curl or wget required" >&2; exit 1
  fi
  unzip -q "$TMP_DOWNLOAD.zip" -d "$DISTRIBUTION_PATH"
  rm -f "$TMP_DOWNLOAD.zip"
  DISTRIBUTION_HOME="$DISTRIBUTION_PATH/$(ls "$DISTRIBUTION_PATH" | head -1)"
fi

exec "$DISTRIBUTION_HOME/bin/mvn" "$@"
