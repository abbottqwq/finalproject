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

## API
make sure to run the init data api first to insert data
- init data `POST /initdata`
    ```json
    {"success": "1"}
    ```
note: offset and limit are optional, but they must show up together
- select all `POST /selectAll`

    request body
    ```json
    {
        "offset": 1,
        "limit": 2
    }
    ```
    return
    ```json
    {
        "Success": "1",
        "Data": [
            {
                "tweets": "version",
                "freq": "4"
            },
            {
                "tweets": "phone",
                "freq": "4"
            }
        ]
    }
    ```
- select keywords by company name `POST /selectByComp`

    request body
    ```json 
    {
        "name": "AppleSupport",
        "offset": 1,
        "limit": 2
    }
    ```
    return
    ```json
    {
        "Success": "1",
        "Data": [
            {
                "tweets": "ios",
                "freq": "4"
            },
            {
                "tweets": "latest",
                "freq": "3"
            }
        ]
    }
    ```
- select keywords by time period `POST /selectByTime`

    request body
    ```json 
    {
        "start": "2017-10-10",
        "end": "2017-10-11",
        "offset": 1,
        "limit": 2
    }
    ```
    return
    ```json
    {
        "Success": "1",
        "Data": [
            {
                "tweets": "version",
                "freq": "4"
            },
            {
                "tweets": "phone",
                "freq": "4"
            }
        ]
    }
    ```
- select keywords by time period and company `POST /selectByTimeAndComp`

    request body
    ```json 
    {
        "name": "AppleSupport"
        "start": "2017-10-10",
        "end": "2017-10-11",
        "offset": 1,
        "limit": 2
    }
    ```
    return
    ```json
    {
        "Success": "1",
        "Data": [
            {
                "tweets": "version",
                "freq": "4"
            },
            {
                "tweets": "phone",
                "freq": "4"
            }
        ]
    }
    ```
- select company names `POST /selectCompanyName`
    ```json
    {
        "Success": "1",
        "Data": [
            {
                "author_id": "AppleSupport",
                "freq": "91"
            },
            {
                "author_id": "SpotifyCares",
                "freq": "61"
            },
            {
                "author_id": "Tesco",
                "freq": "45"
            },
            {
                "author_id": "VirginTrains",
                "freq": "42"
            },
            {
                "author_id": "Ask_Spectrum",
                "freq": "10"
            },
            {
                "author_id": "British_Airways",
                "freq": "10"
            },
            {
                "author_id": "SouthwestAir",
                "freq": "2"
            }
        ]
    }           
    ```
- select time periods `POST /selectTimePeriod`
    ```json
    {
        "Success": "1",
        "Start_Date": "2017-10-10",
        "End_Date": "2017-10-12"
    }
    ```

