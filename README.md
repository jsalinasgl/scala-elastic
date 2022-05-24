## Scala-elastic: database search endpoint implementation sample using MariaDB, ElasticSearch and Logstash

When it comes to intensive text search over a huge amount of data, relational databases do not perform really well. 

This project uses the ELK stack as an alternative to index the data from a MariaDB table into an ElasticSearch instance using Logstash, providing also an endpoint built using the Scala AKKA HTTP framework to execute search queries over it.

### Docker-Compose

This folder contains the configuration used to run MariaDB, Elastic and Logstash docker containers.

####MariaDB

`docker-compose -f ./docker-compose/docker-compose.yml up mariadb`

It creates the database on startup and inserts the content of the file `FIFA22_official_data_edited.csv` in a new table players.
This file contains a short version of 16K footballers data used by the popular video game FIFA (credits to: https://www.kaggle.com/datasets/bryanb/fifa-player-stats-database)

####Elastic Search

`docker-compose -f ./docker-compose/docker-compose.yml up elasticsearch`

####Logstash

`docker-compose -f ./docker-compose/docker-compose.yml up logstash`

The file logstash/input.conf contains the connection parameters to our MariaDB instance. It runs a sql query every two minutes and the results will be indexed in ES.
A datetime column, last_modified is used in the players table. Logstash keeps track of the last date indexed so in this way only modified or new data added in the database will be retrieved each time the query is run.

This is based on the following parameters in the input.conf file: the cron expression, the tracking column and the query containing the date filtering.

`schedule => "*/2 * * * *`

`tracking_column => "last_modified"`

`statement => "SELECT id,name,age,nationality,overall,club,value,foot,number,position,last_modified FROM players WHERE last_modified>:sql_last_value AND last_modified<NOW()ORDER BY last_modified"`

The file logstash/output.conf contains the connection url to the Elastic instance and the index name (players) to be used.

####Scala Application

SBT under the root folder scala-elastic can be used to run the application. To customize the endpoint and the ElasticSearch URL modify the /src/main/resources/application.conf file.

`scala-elastic $ sbt run`

`Server online at at http://127.0.0.1:18000/`

Requests to ES have been implemented using the elastic4s - Elasticseach Scala Client library
(https://github.com/sksamuel/elastic4s)

## Here We Go

Now it is time for football geeks! Lets see some sample requests to our endpoint:

How many players called Kroos are in active? (note the use of * as wildcard in the search parameter)

```json
$ curl -XGET 'http://localhost:18000/players?name=*kroos*'
[
  {
    "age": 31,
    "club": "Real Madrid CF",
    "foot": "Right",
    "id": 117,
    "name": "T. Kroos",
    "nationality": "Germany",
    "number": 8,
    "overall": 88,
    "position": "CM",
    "value": "75M"
  },
  {
    "age": 29,
    "club": "Eintracht Braunschweig",
    "foot": "Right",
    "id": 2561,
    "name": "21 F. Kroos",
    "nationality": "Germany",
    "number": 18,
    "overall": 68,
    "position": "CM",
    "value": "1.2M"
  }
]
```

As a standard REST API it is possible to get a resource by id

```json
curl -XGET 'http://localhost:18000/players/75'
{
  "age": 31,
  "club": "Real Madrid CF",
  "foot": "Left",
  "id": 75,
  "name": "G. Bale",
  "nationality": "Wales",
  "number": 18,
  "overall": 82,
  "position": "RM",
  "value": "25M"
}
```

Who are the Real Madrid players with an overall greater or equal than 85?

```json
$ curl -XGET 'http://localhost:18000/players?club=Real%20Madrid&overall=85'
[
  {
    "age": 29,
    "club": "Real Madrid CF",
    "foot": "Left",
    "id": 14938,
    "name": "T. Courtois",
    "nationality": "Belgium",
    "number": 1,
    "overall": 89,
    "position": "GK",
    "value": "85.5M"
  },
  {
    "age": 31,
    "club": "Real Madrid CF",
    "foot": "Right",
    "id": 117,
    "name": "T. Kroos",
    "nationality": "Germany",
    "number": 8,
    "overall": 88,
    "position": "CM",
    "value": "75M"
  },
  {
    "age": 29,
    "club": "Real Madrid CF",
    "foot": "Right",
    "id": 29,
    "name": "Casemiro",
    "nationality": "Brazil",
    "number": 14,
    "overall": 89,
    "position": "CDM",
    "value": "88M"
  },
  {
    "age": 33,
    "club": "Real Madrid CF",
    "foot": "Right",
    "id": 202,
    "name": "K. Benzema",
    "nationality": "France",
    "number": 9,
    "overall": 89,
    "position": "CF",
    "value": "66M"
  },
  {
    "age": 35,
    "club": "Real Madrid CF",
    "foot": "Right",
    "id": 11,
    "name": "L. Modrić",
    "nationality": "Croatia",
    "number": 10,
    "overall": 87,
    "position": "CM",
    "value": "32M"
  },
  {
    "age": 29,
    "club": "Real Madrid CF",
    "foot": "Right",
    "id": 185,
    "name": "Carvajal",
    "nationality": "Spain",
    "number": 2,
    "overall": 85,
    "position": "RB",
    "value": "47.5M"
  },
  {
    "age": 30,
    "club": "Real Madrid CF",
    "foot": "Right",
    "id": 487,
    "name": "E. Hazard",
    "nationality": "Belgium",
    "number": 7,
    "overall": 85,
    "position": "LW",
    "value": "52M"
  }
]
```

