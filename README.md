# Centralized Calculator Application 

## Description

A Client-Server application that uses TCP connection management to handle a centralized server that multiple clients can connect to. Each client will send a mathematical equation to the server, which will calculate and send back the result to the client. The server will log each client's request and other information, such as time joined and time exited.

## Getting Started

### Prerequisites

* Java Version 21

### Running the Application

#### Without a Makefile

* Compile the script using 'javac *.java'
In one terminal: 'java Server'
In another terminal: 'java Client <Name>'

#### With a Makefile

* Run the makefile using 'make'. You will need to install GNU make if you are on a Windows platform
* In one terminal: 'make run-server'
* In another terminal: 'make run-client NAME=&lt;Name&gt;'
* To remove the class files: run 'make clean'

## License

MIT License

## Acknowledgements

* Brian Pham
* Nick Tollinger
* Akshaan Singh
