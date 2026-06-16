@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM Apache Maven Wrapper startup batch script, version 3.3.2

@IF "%__MVNW_ARG0_NAME__%"=="" (SET "MAVEN_JAVA_EXE=%JAVA_HOME%\bin\java.exe") ELSE (SET "MAVEN_JAVA_EXE=%JAVA_HOME%\bin\%__MVNW_ARG0_NAME__%")
@SET WRAPPER_DIR=%~dp0.mvn\wrapper
@SET WRAPPER_PROPERTIES=%WRAPPER_DIR%\maven-wrapper.properties

@FOR /F "usebackq tokens=1,* delims==" %%A IN ("%WRAPPER_PROPERTIES%") DO (
  @IF "%%A"=="distributionUrl" SET DISTRIBUTION_URL=%%B
)

@IF "%MAVEN_USER_HOME%"=="" SET "MAVEN_USER_HOME=%USERPROFILE%\.m2"

@SET DISTRIBUTION_NAME=%DISTRIBUTION_URL:~0%
@FOR %%I IN ("%DISTRIBUTION_URL%") DO SET "DISTRIBUTION_FILENAME=%%~nxI"
@SET "DISTRIBUTION_DIR_NAME=%DISTRIBUTION_FILENAME:.zip=%"
@SET "DISTRIBUTION_DIR_NAME=%DISTRIBUTION_DIR_NAME:-bin=%"
@SET "DISTRIBUTION_PATH=%MAVEN_USER_HOME%\wrapper\dists\%DISTRIBUTION_DIR_NAME%"

@IF EXIST "%DISTRIBUTION_PATH%\" (
  @FOR /D %%D IN ("%DISTRIBUTION_PATH%\*") DO SET "DISTRIBUTION_HOME=%%D"
) ELSE (
  ECHO Downloading Maven...
  MKDIR "%DISTRIBUTION_PATH%"
  powershell -Command "Invoke-WebRequest -Uri '%DISTRIBUTION_URL%' -OutFile '%DISTRIBUTION_PATH%\download.zip'"
  powershell -Command "Expand-Archive '%DISTRIBUTION_PATH%\download.zip' -DestinationPath '%DISTRIBUTION_PATH%'"
  DEL "%DISTRIBUTION_PATH%\download.zip"
  @FOR /D %%D IN ("%DISTRIBUTION_PATH%\*") DO SET "DISTRIBUTION_HOME=%%D"
)

@"%DISTRIBUTION_HOME%\bin\mvn.cmd" %*
