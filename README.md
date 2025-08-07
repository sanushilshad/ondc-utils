
# ONDC UTILS SERVICE
 
Service for fetching ONDC utility data.
The progress can be tracked here: [milestones](#MILESTONES)

## Tech Stack
| Type | Technologies |
|---|---|
| Client | None |
| Server | Scala, Bash |
| Database | PostgreSQL |
| API Documention | OpenAPI Swagger |


## NEW PROJECT SETUP COMMAND:

- Run: `sbt new zio/zio-http.g8`

## HOT RELAD:

```
sbt ~reStart
```

## BUILD RELEASE SCRIPT:

```
bash release.sh
```

## BUILD RESTART SCRIPT:

```
bash restart.sh
```


## CUSTOM COMMAND:

  ### JWT GENERATION:
  ```
  sbt "~reStart  generate_jwt_token"
  ```


## ENVIRONEMENT VARIABLES:

```

urlMapping = {
    "xxx.com": {url="xxx.com"}
}

application = {
    port = 5602
    serviceId = "3ed42655-898a-4b08-b497-25d4151b47d8"
}

tracing = {
    otelServiceName = "pre-prod-ondc-utils"
    otelExporterTracesEndpoint = "http://localhost:4317"
}

user = {
    token = "eyJ1eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ1.eyJzdWIiOiI5YTNjMDkwOS1zYzVkLTRhODQtOGZiNi03MTkyOGUyOGNiNWIiLCJleHAiOjQ4ODYwNzkyODB2.PLd84JGX6BbelD5WYkdVDoLGXwlXRdp2zQED7jj2qrU"
    url="http://localhost:8230"
}

database = {
    username = "postgres"
    password = "{{password}}"
    port = "{{port}}"
    host = "{{host}}"
    name= "ondc_utils"
}

secret = {
    jwt = {
        key = "df"
    }
}

```

## VSCODE

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "scala",
      "request": "launch",
      "name": "Run ZIO HTTP App",
      "mainClass": "com.placeorder.ondc_utils.MainApp",  
      "buildTarget": "root",                   
      "args": [],
    //   "jvmOptions": ["-Dconfig.resource=application.conf"]
    }
  ]
}
```

## MILESTONE 1:
* [x] Set up basic scala project.
* [x] Integrate tracing.
* [x] Integrate OpenAPI swagger documentation.
* [x] Develop Health check API.
* [x] Add URL fetch API.
* [x] Add authorization middleware connecting to user micro-service.
* [x] Country Fetch API.
* [x] Integrate database.
* [x] Category Fetch API.
* [x] Domain Fetch API.
* [x] City Fetch API.
* [x] State Fetch API.
* [x] Custom command for JWT token geneation.
* [x] Develop application release Script.
* [x] Develop application  restart Script.
* [x] JWT token verification middleware.
* [x] Custom command for Migration.


## MILESTONE 2:
* [ ] Add test cases.
* [ ] Category Attribute Fetch API.

## MILESTONE 3 (OPTIONAL):
* [ ] Override default error message for request body validation error.