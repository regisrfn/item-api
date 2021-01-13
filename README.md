# ITEM - API
Spring boot rest api

## PREREQUISITES
- Java
- Docker | https://docs.docker.com/engine/install/ubuntu/ 

## DOCKER
- RUNNING POSTGRES
- create:
docker run --name postgres -p 5432:5432 -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=userdb -d postgres:alpine
- stop:
docker stop postgres
- start:
docker start postgres

## DATABASE DESCRIPTION

|       Items           |
|--------------------   |
|itemId: UUID (PK)      |
|itemName: String       |
|itemPrice: Double      |
|itemQuantity:Integer   |
|itemDescription: String|
|itemCode:Integer       |         
|itemDiscount:Double    |                
|orderId:UUID           |    
|productId:UUID         |    
