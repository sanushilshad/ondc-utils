
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


## MILESTONES
* [x] Set up basic scala project.
* [x] Integrate tracing.
* [x] Integrate OpenAPI swagger documentation.
* [x] Develop Health check API.
* [ ] Override default error message for request body validation error.
* [ ] Add URL fetch API.
* [ ] Country Fetch API.
* [ ] City Fetch API.
* [ ] Category Fetch API.
* [ ] Domain Fetch API.
* [ ] Category Attribute Fetch API.
* [ ] Develop application release Script.
* [ ] Develop application restart Script.
