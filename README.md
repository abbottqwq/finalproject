# finalproject
## test the project
### using docker

- Dockerfile for prod (disable the test URLs)
- Dockerfile.dev for dev (enable the test URLs)

### using sbt

- sbt run -Dconfig.file=conf/prod.conf for prod (disable the test URLs)
- sbt run for dev (enable the test URLs)

## dev mode
### test api:
- run base connect `GET /test`
```json
  {"success": "1"}
````
- test connect `GET /test/testconnect`
```json
  {"success": "1"}
````
- test spark `GET /test/testspark`
  - success
    ```json
    {"success": "1"}
    ````
  - fail
    ```json
    {"success": "1", "Error": "AppName error", "AppName": "AppName of the spark session now running"} 
    ````  
    if the appname is incorrect. try to use colse spark to fix
    ```json
    {"success": "0", "Error": "connection fail"} 
    ```
- test close spark `GET /test/closespark`
    ```json
    {"success": "1"}
    ````
    ```json
    {"success": "0"}
    ````
- other
  - Code: `404 Not Found`
