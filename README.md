# Extractor service

## Requirements

Java 11 is required to build and run this application.  
Moreover, the service depends at runtime on MongoDB and Kafka/Redpanda.  
A simpler way to set up these dependencies would be to run the already supplied
docker-compose. For further info please refer to solution README.

## How to build

To build the so-called fat jar artifact, run `./mvnw clean package`

## Execution

To run the service, just type `java -jar target/extractor-0.0.1-SNAPSHOT.jar`

## How it works

This service relies on data provided by upstream services that push sensor info on a certain topic. 
By configuration, default topic is `ingestion`. 
A listener consumes a list of sensor data and extracts increasing sequences grouped by `sensorId` and persists data to 
the repository (by default MongoDB). Each consumer belongs to the same consumer group, to avoid messages replication.  
After performing extractions, sensor data will be sent to a downstream topic, which is configured to be `postprocessing`; 
in our scenario, a file called `longestsequence.log`, which will contain the longest increasing sequence so far, regardless 
of the sensorId, will be eventually saved.  
To provide a simple interface for querying data, an endpoint that returns all overlapping sequences is exposed. An example call will be:
```
curl -L -X POST 'http://localhost:28888/sequence'
-H 'Content-Type: application/json' \
--data-raw '{
    "sensorId": 1,
    "dateFrom": 1661278877026,
    "dateTo": 1661278881079
}'
```
Please note that `sensorId` parameter is not mandatory, while `DateFrom` and `DateTo` are. 

## Implementation considerations
### Modularization

To increase service modularization (e.g.: to provide easy interchangeable components such as repository and message broker), 
There have been two different approaches:
1. About persistence, a classic design using interfaces and Spring Data capabilities and IoC. Changing repository (e.g.: moving
from MongoDB to a SQL like database) will consist in adding dependencies, implementing entity classes and 
writing a class that implements `SequenceRepository` interface.
2. For message broker, the choice is to leverage the power and ease of use of Spring Cloud Functions. Switching from Kafka 
to RabbitMQ would mean to add dependencies and change a few configurations. Business logic relies on Java streams/functions 
without strong dependencies such as `KStreams`.

### Performances, reliability and scalability

First, this service has been designed to have many instances running.
Concurrency is implemented "by design" consuming messages from queue using the same consumer group.  
Extracted sequences will be persisted reliably using database transactions and sessions, moreover MongoDB provides a lot 
of mechanisms out of the box to ensure the desired behaviors (see `ReadConcern`, `WriteConcern`).
Finally, writing a file with the longest sequence is not "scalable" so the only solution is to replicate the postprocessing 
logic for each running instance, so the same file is present on each instance.