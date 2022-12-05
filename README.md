# Calculator challenge

## Stack Herramientas
- Java 11
- Gradle 7.4
- Spring Boot
- Spring Security (JWT)
- Docker
- Postgres
- IntelliJ Community Edition

## Instrucciones para levantar app :

1. Tener instalado docker
2. parado en el directorio del proyecto ejecutar :
   - docker-compose up -d
3. Importar coleccion de postman (del directorio postman del proyecto)
4. Para probar el happy path ejecutar los requests en el siguiente orden :
   - "Create Mock OK"
   - "Signup"
   - "Login"
   - "GET Calculated Value"
   - "GET Percentage audit"