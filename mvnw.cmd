@REM Maven Wrapper startup batch script
@REM Licensed to the Apache Software Foundation (ASF)

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __ 2>NUL
@SETLOCAL

@SET MAVEN_PROJECTBASEDIR=%~dp0
@IF "%MAVEN_PROJECTBASEDIR:~-1%"=="\" SET MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

@SET MAVEN_WRAPPER_PROPERTIES=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties

@SET DISTRIBUTION_URL=
@FOR /F "usebackq tokens=1,* delims==" %%A IN ("%MAVEN_WRAPPER_PROPERTIES%") DO (
    @IF "%%A"=="distributionUrl" SET DISTRIBUTION_URL=%%B
)

@IF "%MAVEN_USER_HOME%"=="" (
    SET MAVEN_USER_HOME=%USERPROFILE%\.m2
)

@SET MAVEN_WRAPPER_CACHE=%MAVEN_USER_HOME%\wrapper

@REM Extract Maven version from URL (e.g. apache-maven-3.9.6-bin.zip -> 3.9.6)
@FOR /F "tokens=3 delims=-" %%i IN ("%DISTRIBUTION_URL:~0,-8%") DO SET MAVEN_VERSION=%%i
@REM Fallback: parse last segment before -bin
@FOR %%f IN ("%DISTRIBUTION_URL%") DO SET DIST_FILENAME=%%~nxf
@FOR /F "tokens=3 delims=-" %%i IN ("%DIST_FILENAME%") DO SET MAVEN_VERSION=%%i

@SET MAVEN_DIST_DIR=%MAVEN_WRAPPER_CACHE%\dists\apache-maven-%MAVEN_VERSION%
@SET MAVEN_HOME=%MAVEN_DIST_DIR%\apache-maven-%MAVEN_VERSION%
@SET MAVEN_CMD=%MAVEN_HOME%\bin\mvn.cmd

@IF NOT EXIST "%MAVEN_HOME%" (
    @ECHO Downloading Apache Maven %MAVEN_VERSION%...
    @MKDIR "%MAVEN_DIST_DIR%" 2>NUL
    @SET TMP_ZIP=%MAVEN_DIST_DIR%\maven.zip
    @powershell -Command "Invoke-WebRequest -Uri '%DISTRIBUTION_URL%' -OutFile '%MAVEN_DIST_DIR%\maven.zip'"
    @powershell -Command "Expand-Archive -Path '%MAVEN_DIST_DIR%\maven.zip' -DestinationPath '%MAVEN_DIST_DIR%'"
    @DEL /Q "%MAVEN_DIST_DIR%\maven.zip"
)

@IF NOT EXIST "%MAVEN_CMD%" (
    @ECHO ERROR: Could not find Maven at %MAVEN_CMD% >&2
    @EXIT /B 1
)

@"%MAVEN_CMD%" %*

