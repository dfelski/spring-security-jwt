# Spring Security using JSON Web Tokens (JWT)

Simple [Spring Boot](https://spring.io/projects/spring-boot) application using the [Java-JWT](https://github.com/auth0/java-jwt) library from [Auth0](https://auth0.com).

## Run
It's a simple Spring Boot application ;)

### Run with Docker
Build and start container with
`docker build -t jwtexample . && docker run --rm -it -p 8080:8080 jwtexample`

### Run with Gradle
Start the application with `gradle clean bootRun`

## Structure
The application provides three HTTP endpoints

`http://localhost:8080/api/public` is public and accessible without authentication.

To access `http://localhost:8080/api/private` you have to authenticate first.

The `http://localhost:8080/api/login` endpoint provides functionality to authenticate using request parameters (*omg! don't do this at home! never!*)

## Authentication
Just send a GET request t `localhost:8080/api/login?username=user&password=password` and grep the 'Authorization' response header and put it in every further request.

```
curl -v --silent 'localhost:8080/api/login?username=user&password=password' 2>&1 | grep Authorization
```
The output should look like
```
< Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiaXNzIjoianVzdG1lIiwiZXhwIjoxNTU3MzI1NzYzLCJyb2wiOlsiUk9MRV9VU0VSIl19.qGe0skux5-7l_DM7XQJSkQ01bMKrs_z2-Xslh5i9QIo
```
Now use the token to access the restricted endpoint
```
curl -H 'Authorization: Bearer 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiaXNzIjoianVzdG1lIiwiZXhwIjoxNTU3MzI1NzYzLCJyb2wiOlsiUk9MRV9VU0VSIl19.qGe0skux5-7l_DM7XQJSkQ01bMKrs_z2-Xslh5i9QIo' localhost:8080/api/private/
```

Or just take a look into the integration test.
