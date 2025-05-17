
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


## ENVIRONEMENT VARIABLES:

```
    urlMapping = [
        { id = "user", url = "xxxx"},
        { npType = "websocket", "url"="xxx"}

    ]

    port = 5602
    otelServiceName = "pre-prod-ondc-observability"
    otelExporterTracesEndpoint = "http://localhost:4317"
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
* [ ] Override default error message for request body validation error.
* [ ] JWT token generation and verification
* [ ] Custom command generation (Migration + JWT token geneation)
* [ ] City Fetch API.
* [ ] Develop application release Script.
* [ ] Develop application restart Script.

## MILESTONE 2 (OPTIONAL):
* [ ] Category Attribute Fetch API.