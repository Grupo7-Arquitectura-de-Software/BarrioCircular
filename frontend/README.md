## Barrio Circular

### Logs de todos los servicios

```
docker compose logs -f
```

### Logs exclusivos del frontend

```
docker compose logs -f frontend-dev
```

### Logs exclusivos de la base de datos

```
docker compose logs -f db
```

### Detener el entorno

Cuando termines tu jornada o la sesión de desarrollo, puedes apagar los contenedores de forma segura con:

```
docker compose down
```

### Ejecutar el frontend en modo desarrollo

Si instalas una nueva dependencia o actualizas tu package.json, es recomendable reconstruir el contenedor
para que el volumen anónimo de node_modules refleje los cambios:

```
docker compose build --no-cache frontend-dev
```

```
docker compose up frontend-dev
```
