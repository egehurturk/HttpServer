<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Thanks again! Now go create something AMAZING! :D
***
***
***
*** To avoid retyping too much info. Do a search and replace for the following:
*** github_username, repo_name, twitter_handle, email, project_title, project_description
-->



<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![GitHub contributors](https://img.shields.io/github/contributors/Naereen/StrapDown.js.svg)](https://GitHub.com/egehurturk/HttpServer/graphs/contributors/)
[![Forks](https://img.shields.io/github/forks/Naereen/StrapDown.js.svg?style=social&label=Fork&maxAge=2592000)](https://GitHub.com/Naereen/StrapDown.js/network/)
[![Stargazers](https://img.shields.io/github/stars/Naereen/StrapDown.js.svg?style=social&label=Star&maxAge=2592000)](https://GitHub.com/egehurturk/HttpServer/stargazers/)
[![Issues](https://img.shields.io/github/issues/Naereen/StrapDown.js.svg)](https://GitHub.com/egehurturk/HttpServer/issues/)



<!-- PROJECT LOGO -->
<br />
<p align="center">

  <h2 align="center">Banzai Server (A HTTP Server)</h3>

  <p align="center">
    A non-blocking, event-driven Http server from scratch, using plain Java. No additional dependencies (look at [here](#built-with) is needed. 
    <br />
    <a href="https://github.com/egehurturk/HttpServer"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/egehurturk/HttpServer/issues">Report Bug</a>
    ·
    <a href="https://github.com/egehurturk/HttpServer/issues">Request Feature</a>
  </p>
</p>



<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary><h2 style="display: inline-block">Table of Contents</h2></summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
        <li><a href="#configuration">Installation</a></li>
        <li><a href="#deployment">Deployment</a></li>
      </ul>
    </li>
    <li><a href="#contributing">Contributing</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project
Understanding HTTP in web development is essential. To strengthen my Java and web skills (also a school project), I decided to create my own http server
which is capable of parsing, writing, logging, etc. 

### Built With

* [Apache Maven](https://github.com/apache/maven)
* [Apache Logging-Log4J2](https://github.com/apache/logging-log4j2)
* [JUnit](https://github.com/junit-team/junit4)


<!-- GETTING STARTED -->
## Getting Started


These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.


### Prerequisites

[Apache Maven](https://github.com/apache/maven) should be installed on your system and the `JAVA_HOME` environment variable should point to JDK home. Look at [this](https://maven.apache.org/install.html) to install maven


### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/egehurturk/HttpServer.git
   ```
2. Run bash scripts to start the server
   ```sh
   banzai run http -p 8080 -h localhost -b 50 --name Hello 
   ```
   See [#configuration] for details on configuring the server
   
### Configuration
Configuration can happen in two ways. One is to pass in CLA (Command Line Arguments) while executing or running the server:

1. CLA (Command Line Arguments)
  ```sh
  banzai run http -p 9090 -h 127.0.0.1 -b 50 --name AwesomeServer
  ```
  This creates a server running on port `9090`, host `127.0.0.1`, a backlog of `50`, and a name of `AwesomeServer`
  
  All command line arguments:
  <ol>
  <li> -p: port numer that the server is running on</li>
    <li> -h: host</li>
    <li> -b: backlog (maximum number of Http threads in queue)</li>
    <li> --name: name of the server</li>
    <li> -w: web root of server</li>
  </ol>
  
2. Using a `properties` file:
  One can also configure the server with using a `properties` file located in `root/src/main/resources/server.properties` directory. Every
  argument is passed as a key=value pair. Server reads all keys and values from the file and sets values accordingly. Same arguments are present.

### Deployment
Deployment on docker. 

<!-- ROADMAP -->
## Roadmap

See the [open issues](https://github.com/egehurturk/HttpServer/issues) for a list of proposed features (and known issues).



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request


