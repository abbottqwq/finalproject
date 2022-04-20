[![Docker Compose CI](https://github.com/abbottqwq/finalproject/actions/workflows/dockercompose_test.yml/badge.svg)](https://github.com/abbottqwq/finalproject/actions/workflows/dockercompose_test.yml)
[![Scala CI](https://github.com/abbottqwq/finalproject/actions/workflows/scala.yml/badge.svg)](https://github.com/abbottqwq/finalproject/actions/workflows/scala.yml)
# finalproject
## test the project
> before running, remember to **RENAME .env.example to .env**
### using docker
- Dockerfile for prod (disable the test URLs)
- Dockerfile.dev for dev (enable the test URLs)

### using sbt
- sbt run -Dconfig.file=conf/prod.conf for prod (disable the test URLs)
- sbt run for dev (enable the test URLs)

### using docker-compose for dev mode (prod mode not included)
- `docker-compose up --build --force-recreate`

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
    {"success": "0", "Error": "connection fail", "Reason": "the error thrown out"} 
    ```
- test close spark `GET /test/closespark`
    ```json
    {"success": "1"}
    ```
    ```json
    {"success": "0", "Reason": "the error thrown out"}
    ```
- test database connection `GET /test/testdatabase`
    ```json
    {"success": "1"}
    ```     
    ```json
    {"success": "0","Error": "database connection fail", "Reason": "the error thrown out"}
    ```
- other
  - Code: `404 Not Found`
