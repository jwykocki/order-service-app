# order-service-app

Order service is a solution that exposes API endpoints which together with Product service allow customers create order with products and purchase it.  

## Content
**[How to run it](#how-to-run-it)**   
**[Workflow](#workflow)**  
- [Diagram](#diagram)
- [Creating an order](#creating-an-order-)
- [Processing the order](#processing-the-order)
- [Reading processed products](#reading-processed-products)
- [Finalizing order](#finalizing-order)
- [Checking order status](#checking-order-status)
- [Update stock: product-service](#update-stock-product-service)
  - [Diagram](#diagram-1)
- [Error handling](#error-handling)  

**[Bruno collection](#bruno-collection)**  
**[Tech Stack](#tech-stack)**

## How to run it

First of all, these repositories need to be downloaded:  
`git clone git@github.com:jwykocki/order-service-app.git`  
`git clone git@github.com:jwykocki/product-service-app.git`

Next, in order-service-app run docker-compose.yml file using  
`docker compose up` command. This will start order and product databases and creates tables for them.

To start RabbitMQ queue, this command can be used:  
`docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.13-management`

To check if RabbitMQ was started correctly, visit http://localhost:15672/#/ and login with username `guest` and password `guest`.

Next, create queues:  
`unprocessed-products`  
`finalized-products`  
`update-products`

Create exchanges:  

`unprocessed-orders` type: **topic**  

`unprocessed-products` type: **topic**  
add binding -> `unprocessed-products` queue  

`finalized-products`type: **topic**  
add binding -> `finalized-products` queue  


After that, start Order and Product applications by running main class in your IDE of choice.  
order-service-app will automatically start on **8091** port.  
product-service-app will automatically start on **8092** port.

Further steps are described more in Workflow section.


## Workflow

### Diagram
![img.png](https://github.com/jwykocki/order-service-app/blob/main/architecture/Order%20flow.jpg)

### Creating an order  
HTTP method: `POST`  
PATH: `/order`  
**Request**  
Content-Type: `application/json`   
Body:
```json
{
    "customerId": 3,
    "orderProducts": [
        {
            "productId": 1,
            "quantity": 2
        },
        {
            "productId": 2,
            "quantity": 4
        }
    ]
}
```
**Response**  
Status code: `200 OK`  
Content-Type: `application/json`  
Body:
```json
{
  "orderId": 14,
  "customerId": 3,
  "status": "UNPROCESSED",
  "orderProducts": [
    {
      "productId": 1,
      "quantity": 2,
      "status": "UNKNOWN"
    },
    {
      "productId": 2,
      "quantity": 4,
      "status": "UNKNOWN"
    }
  ]
}
```
* order-service receives order request
* saves it to the database 
* publish whole order on the `unprocessed-orders` queue
* as a response, service is sending order id and status `UNPROCESSED`. 

### Processing the order
* order-service read messages from `unprocessed-orders` queue
* splits the request per product, and send it to `unprocessed-products` queue
* product-service read messages from `unprocessed-products` queue
* check if product with given id is available, and accordingly saves request with productId, orderId, quantity and reserved flag on the `processed-products` queue

### Reading processed products
* order-service reads messages from `processed-products`
* update order-product-table with reserved value
* After every update check if every row with given productId has populated reserved value
* If yes, changes order status to `PROCESSED`
* If every product was successfully reserved, automatically finalize 

### Finalizing order
If some of the products were not available, user can partially finalize the order.
HTTP method: `POST`  
PATH: `order/finalize`  
**Request**  
Content-Type: `application/json`   
Body:
```json
{
    "orderId": 14,
    "customerId": 3,
    "orderProducts": [
        {
            "productId": 1,
            "reserved": 2,
            "finalize": 2
        },
        {
            "productId": 2,
            "reserved": 4,
            "finalize": 2
        }
    ]
}
```
Valid request must meet the conditions:
1) each OrderProduct must have been reserved in the past
2) "reserved" field must be exact like in request - Amount of reserved product by customer
3) "finalize" field must contain value less or equal than reserved - Amount of product which customer would like to finally purchase

* order-service receives request to finalize order and validates it
* sends every FinalizeProductsRequest on `finalize-products` queue
* product-service receives request and accordingly to requested amounts of products, updates products in database
* as a response order-service returns order with status `FINALIZED`

**Response**  
Content-Type: `application/json`   
Body:
```json
{
    "orderId": 14,
    "customerId": 3,
    "status": "FINALIZED",
    "orderProducts": [
        {
            "productId": 1,
            "finalized": 2
        },
        {
            "productId": 2,
            "finalized": 2
        }
    ]
}
```


### Checking order status

Client can check status of the order at every moment during the process 

HTTP method: `GET`  
PATH: `/order/{orderId}`  
**Request**  
Content-Type: `application/json`   
Body: empty  
**Response**  
Content-Type: `application/json`  
Body:  
```json
{
  "orderId": 14,
  "customerId": 3,
  "status": "UNPROCESSED",
  "orderProducts": [
    {
      "productId": 6,
      "quantity": 2,
      "status": "UNKNOWN"
    },
    {
      "productId": 7,
      "quantity": 4,
      "status": "UNKNOWN"
    }
  ]
}
```

### Update stock: product-service

Updating stock can be processed by product-service by reading text file and publishing UpdateProduct entries on the `update-products` queue.
Next, fixed number of concurrent threads read from the queue and update amount of available products.
This event can be triggered by sending request on `product-service` controller, previously inserting file in src/main/resources folder.  
**Request**
HTTP method: `POST`  
PATH: `/stock/{fileName}`   
Body: empty  
**Response**  
`Stock file read successfully`

#### Diagram:
![img.png](https://github.com/jwykocki/order-service-app/blob/main/architecture/Update%20stock.jpg)

### Error handling
In case of any error during order-service and product-service work, the error response is returned with proper HTTP status code.
```json
{
  "message": "Request body is not valid",
  "errors": [
    "customerId cannot be null"
  ]
}
```

## Bruno collection

[Bruno](https://docs.usebruno.com/introduction/what-is-bruno) is my tool of choice as HTTP client.
It is similar to Postman, but there are no charges to use Git.
To test application endpoints, Bruno collection with proper HTTP requests is prepared and can be found in [bruno](bruno) directory.

## Tech Stack

#### Java 17
Used as the core programming language to implement business logic and services with the latest features and improvements in the Java ecosystem.

#### Gradle/Maven
Used for project build automation, dependency management, and defining build configurations to ensure a consistent build process.

#### Spring
Utilized as the primary framework for building product-service-app application using plenty of tools offered by framework.

#### Quarkus
Used as order-service-app framework to build service with fast startup time and low memory footprint.

#### PostgreSQL
Used as a relational database management system with PanacheRepository and JpaRepository with locking and transactional isolation configuration as a programming interface with the database.

#### Docker
Employed to containerize the applications, queues and databases defined in docker-compose file.

#### Junit5, Mockito, Testcontainers
Junit5 and Mockito were utilized for unit testing and mocking dependencies. Testcontainers were used to run integration tests ensuring proper application flow.

#### Pact
Used for consumer-driven contract testing to ensure services communicate with each other correctly (see add-contract-tests branch).

#### RabbitMQ
Implemented as the message broker for asynchronous communication between services, allows to take advantage of created Event-Driven Architecture system.

#### Github Actions
Used to run tasks such as code formatting checks (`spotless:check`) and executing all tests (unit, integration, contract) on every commit and pull request validation.

