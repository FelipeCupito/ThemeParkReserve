# ThemeParkReserve - Grupo 8

## Build Project

`mvn install`

## Clients

### Prepare
After installing, run the following to extract the clients:

`mkdir client-builds`

`tar -xzf client/target/tpe1-g8-client-1.0-bin.tar.gz -C client-builds --strip-components=1`

`cd client-builds`

### Run
Inside `./client-builds`, run:

`./admin-cli #arguments`

`./book-cli #arguments`

`./query-cli #arguments`

`./notif-cli #arguments`

## Server

### Run

To run, use the following command from the project root:
`mvn exec:java -pl server -Dexec.mainClass="ar.edu.itba.pod.server.Server"`