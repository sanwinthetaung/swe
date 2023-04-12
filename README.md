# Backend

This project was using Java 1.8, Spring Boot 2.4.4

# Steps to run
1. Build the project using `mvn clean install`
2. Run using `mvn spring-boot:run`
3. Exposed Endpoints are as follows:
    ```
    POST http://localhost:8080/api/upload
    GET http://localhost:8080/api/users
    GET http://localhost:8080/api/users/{id}
    POST http://localhost:8080/api/user
    PUT http://localhost:8080/api/users/{id}
    DELETE http://localhost:8080/api/users/{id}
    ```
   
### Upload csv 
```
POST /api/upload HTTP/1.1
Host: localhost:8080
Content-Length: 212
Content-Type: multipart/form-data;
Content-Disposition: form-data; name="file"; filename="file_path_to_csv"
Content-Type: text/csv
```

### Search  with criteria
```
GET /api/users?minSalary=2000&maxSalary=&offset=0&limit=30&sort=%2Dname HTTP/1.1
Host: localhost:8080
```

### Get By id
```
GET /api/users/d474 HTTP/1.1
Host: localhost:8080
```

### Create
```
POST /api/user HTTP/1.1
Host: localhost:8080
Content-Type: application/json
{
    "employeeId": "d799",
    "login": "aa",
    "name": "aaa",
    "salary": 1000
}
```

### Update
```
PATCH /api/users/d799 HTTP/1.1
Host: localhost:8080
Content-Type: application/json
{
    "employeeId": "d799",
    "login": "ddddd",
    "name": "ddd",
    "salary": 10.3
}
```

### Delete
```
DELETE /api/users/d799 HTTP/1.1
Host: localhost:8080
```

## API documentation
You can view api info, browse to `http://localhost:8080/swagger-ui.html`

## Default profile is using H2 in-memory database
To query or view database, browse to `http://localhost:8080/h2-console`.
Default url is `jdbc:h2:mem:test`, username is `sa` and password is `password`.
If you want to use mysql db and dont want to install mysql,
you can run 
1. `cd docker`
2. `docker-compose -f docker/mysql.yml up`

## Docker build and run
To build and run automatically docker-compose for backend service, db service, and frontend service, you can use 
1. `cd docker`
2. `docker-compose -f app.yml up`
3. frontend  `http://localhost:8081/`
4. backend `http://localhost:8080`

