# Getting Started

## How to

* create `DynamoDB` table `Work` in your AWS account with partition key `id` (String) and sort key `username` (String)
* set environment variables `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY`
* `./mvnw spring-boot:run`
* or run via your preferred IDE
* issue HTTP request (see examples below)

### With cURL:
```shell
curl -X GET --location "http://localhost:8080/api/work/item"
```

```shell
curl -X GET --location "http://localhost:8080/api/work/item?archived=true"
```

```shell
curl -X PUT --location "http://localhost:8080/api/work/item/d2af1e4b-9d01-48d2-8e73-e475b58555be:Jordan:archive"
```

```shell
curl -X DELETE --location "http://localhost:8080/api/work/item/d2af1e4b-9d01-48d2-8e73-e475b58555be:Jordan:delete"
```

```shell
curl -X POST --location "http://localhost:8080/api/work/item" \
-H "Content-Type: application/json" \
-d "{
\"name\": \"Jordan\",
\"guide\": \"Basketball\",
\"description\": \"Win a championship\",
\"status\": \"Done\",
\"archived\": 1
}"
```

### With Intellij HTTP client

```shell
###
GET http://localhost:8080/api/work/item
```

```shell
###
GET http://localhost:8080/api/work/item?archived=true
```

```shell
###
PUT http://localhost:8080/api/work/item/d2af1e4b-9d01-48d2-8e73-e475b58555be:Jordan:archive
```

```shell
###
DELETE http://localhost:8080/api/work/item/d2af1e4b-9d01-48d2-8e73-e475b58555be:Jordan:delete
```

```shell
###
POST http://localhost:8080/api/work/item
Content-Type: application/json

{
"name": "Jordan",
"guide": "Basketball",
"description": "Win a championship",
"status": "Done",
"archived": 1
}
```
