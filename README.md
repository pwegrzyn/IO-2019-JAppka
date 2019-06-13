# IO-2019-JAppka

[![Build Status](https://travis-ci.com/pwegrzyn/IO-2019-JAppka.svg?branch=master)](https://travis-ci.com/pwegrzyn/IO-2019-JAppka)
[![codebeat badge](https://codebeat.co/badges/bfcae917-5d7b-4abf-842a-f1c7198a8c88)](https://codebeat.co/projects/github-com-pwegrzyn-io-2019-jappka-master)

Project for Software Engineering class at AGH-UST

**Group:** C2 Thursday 11:15-12:45

SonarQube
=============
### Prerequisites
* [SonarQube](http://www.sonarqube.org/downloads/) 7.7+
* A gradle wrapper is included that bundles gradle. All other required plugins will be pulled by gradle as needed.

### Usage
* Start the local instance of the SonarQube server (`...\sonarqube-7.7\bin\windows-x86-64\StartSonar.bat`)
* Run the following command in the project root:
  * On Unix-like systems:
    `./gradlew -Dsonar.host.url=http://localhost:9000 sonarqube`
  * On Windows:
    `.\gradle.bat -Dsonar.host.url=http://localhost:9000 sonarqube`
* Or you can just use the default host.url:
    * On Windows:
        `.\gradle.bat sonarqube`
        
Visit `http://localhost:9000/projects?sort=-analysis_date` to view the report

Known Issues
=============
* Currently it's not possible to add a new app while having the Graph Customization Menu open and having
edited the ListView there.
* Occasionally a day may be skipped when switching days in the main view
* Need to reproduce the specific error the client has experienced when adding new apps after loading configuration and fix it
* Configuration loading does not remove apps already listed in the main view which are not present in the config file (probably intended?)
* The build process error is probably a Travis-side error (see [this](https://travis-ci.community/t/install-of-oraclejdk11-is-failing/1856/2) for more details)