
# ONDC OBSERVABILITY SERVICE
 
Service for sending observability data to ONDC endpoint.
The progress can be tracked here: [milestones](#MILESTONES)

## Tech Stack
| Type | Technologies |
|---|---|
| Client | None |
| Server | Scala, Bash |
| Database | PostgreSQL |
| API Documention | OpenAPI Swagger |
| Messaging System | Apache Kafka |


## NEW PROJECT SETUP COMMAND:

- Run: `sbt new zio/zio-http.g8`

## HOT RELAD:

```
sbt ~reStart

```


## ENVIRONEMENT VARIABLES:

```
    keyMapping = [
        { npType = "BuyerApp", token = "xxxx", networkId = "xx.co"},
        { npType = "SellerApp", token = "xxxx", networkId = "xx.rapidor.co"}

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
* [ ] Integrate Kafka Consumer and Producer.
* [ ] Develop Observability Producer API.
* [ ] Develop JWT token generation command.
* [ ] Develop application release Script.
* [ ] Develop application restart Script.
* [ ] Middleware to save the request and response to tracing.