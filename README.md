# Prueba técnica - API Pólizas

API REST en Spring Boot para gestión de pólizas y riesgos de seguro.

## Requisitos previos

- Java 21
- Maven (o usar el wrapper incluido)

## Cómo correrlo

```bash
./mvnw spring-boot:run
```

Queda corriendo en el puerto 8080.

## Autenticación

Todos los endpoints necesitan el header `x-api-key: 123456`, si no retorna 401.

## Endpoints disponibles

**Pólizas**
- `GET /polizas` — lista todas, acepta filtros `?tipo=` y `?estado=`
- `GET /polizas/{id}/riesgos` — riesgos de una póliza
- `POST /polizas/{id}/renovar` — renueva la póliza (ajusta canon y prima con el IPC)
- `POST /polizas/{id}/cancelar` — cancela la póliza y sus riesgos
- `POST /polizas/{id}/riesgos` — agrega un riesgo (Nada más sirve con pólizas COLECTIVA)

**Riesgos**
- `POST /riesgos/{id}/cancelar` — Cancela la póliza y todos sus riesgos 

**Otros**
- `POST /core-mock/evento` — simula notificación al core

## Ejemplos rápidos

Listar pólizas:
```bash
curl -H "x-api-key: 123456" http://localhost:8080/polizas
```
o
```bash
curl -H "x-api-key: 123456" http://localhost:8080/polizas?tipo=INDIVIDUAL&estado=RENOVADA
```

Renovar póliza con id 1:
```bash
curl -X POST -H "x-api-key: 123456" http://localhost:8080/polizas/1/renovar
```

Agregar riesgo a póliza COLECTIVA (No permite a individuales):
```bash
curl -X POST -H "x-api-key: 123456" -H "Content-Type: application/json" \
  -d '{"descripcion":"Incendio bodega","asegurado":"Pedro Gomez"}' \
  http://localhost:8080/polizas/2/riesgos
```
## Pruebas con Postman
Para probar los endpoints, importa los archivos que están en ``postman/``

Importa la colección (ApiPoliza.postman_collection.json)
Importa el entorno (Polizas.postman_environment.json)
En Postman selecciona el entorno Polizas en la esquina superior derecha de disparar las peticiones.


## Reglas de negocio importantes

- Solo se agregan riesgos a pólizas COLECTIVA
- No se puede renovar una póliza que ya esté cancelada
- Cancelar una póliza cancela también todos los riesgos asociados
- En pólizas INDIVIDUAL no se puede cancelar el único riesgo activo, hay que cancelar la póliza completa
- El IPC está configurado en 5% (se puede cambiar en application.properties)

## Base de datos

Usa H2 en memoria, al reiniciar se pierden los cambios. Trae datos de prueba cargados en `data.sql`.

Consola H2: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:polizasdb`
- User: sa / sin contraseña

## Estructura

```
src/main/java/com/segurosbolivar/polizasapp/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
├── config/
└── exception/
```
## Nota adicional

#### Valores que acepta la API
- **tipo** de póliza: `INDIVIDUAL` o `COLECTIVA`
- **estado** de póliza: `ACTIVA`, `RENOVADA`, `CANCELADA`
- **estado** de riesgo: `ACTIVO`, `CANCELADO`
