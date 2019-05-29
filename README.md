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