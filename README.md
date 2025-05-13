
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

## MILESTONES
* [x] Set up basic scala project.
* [x] Integrate tracing.
* [x] Integrate OpenAPI swagger documentation.
* [x] Develop Health check API.
* [x] Add URL fetch API.
* [x] Add authorization middleware connecting to user micro-service.
* [x] Country Fetch API.
* [ ] Override default error message for request body validation error.
* [ ] City Fetch API.
* [ ] Category Fetch API.
* [ ] Domain Fetch API.
* [ ] Category Attribute Fetch API.
* [ ] Develop application release Script.
* [ ] Develop application restart Script.
