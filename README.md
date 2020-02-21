# Counters API

## Requirements

For building and running the application you need:

- [Spring Boot 2.2.4](https://spring.io/blog/2020/01/20/spring-boot-2-2-4-released)
- [JDK 11](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)
- [Maven 3](https://maven.apache.org)
- Embedded Mongo DB

## Running the application locally
you can run on CLI using the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like:

```shell
mvn spring-boot:run
```
The server to starts on port `8080`
The application is also deployed to heroku at `https://mrkotte-counterapp.herokuapp.com`

## REST API
In this API, each Counter represents a resource.

## Get a counter by name

If the counter with the name is found then the name and value are returned.
If the counter with the given name is not found `404 not found`  is returned.

```http
GET /counters/{name}
```

| Parameter | Type | Description |
| :--- | :--- | :--- |
| `name` | `string` | **Required**. The counter that should be returned|

```javascript
{
    "name": "375aa472-3cb3-4ebf-a18c-9d970ab5316c",
    "value": 123
}
```

## Get all available counters

List of all available counters

```http
GET /counters/
```

Sample response

```javascript
[
    {
        "name": "375aa472-3cb3-4ebf-a18c-9d970ab5316c",
        "value": 123
    },
    {
        "name": "75ef2288-39ec-436f-9954-8106c9dec5f1",
        "value": 456
    }
]
```


## Create a counter

Create a counter with the given value and return the created resource

```http
POST /counters
```

| Json Body | Type | Description |
| :--- | :--- | :--- |
| `value` | `long` | **Required**. The initial value of the counter |

Sample request

```javascript
{
    "value": 123
}
```

Sample response

```javascript
{
    "name": "375aa472-3cb3-4ebf-a18c-9d970ab5316c",
    "value": 123
}
```


## Increment a counter value by 1

Increments a counter and returns the updated resource

```http
PATCH /counters/{name}/increment
```

| Parameter | Type | Description |
| :--- | :--- | :--- |
| `name` | `string` | **Required**. The counter that should be incremented|


```javascript
{
    "name": "375aa472-3cb3-4ebf-a18c-9d970ab5316c",
    "value": 124
}
```

## Error Responses

Many API endpoints return the JSON representation of error
```javascript
{
    "status" : int,
    "reason" : string,
    "message" : string
  
}
```

The `status` attribute contains the http status code

The `reason` attribute describes short information regarding the error

The `message` attribute contains detailed error message

## Status Codes

The following status codes are returned in the API:

| Status Code | Description |
| :--- | :--- |
| 200 | `OK` |
| 400 | `BAD REQUEST` |
| 404 | `NOT FOUND` |
| 500 | `INTERNAL SERVER ERROR` |