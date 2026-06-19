-- PostGIS: soporte geoespacial requerido por el Bounded Context de Logística
-- (consultas por radio, ST_DWithin, distancias Haversine sobre coordenadas GPS).
CREATE EXTENSION IF NOT EXISTS postgis;

-- Generación de UUID en el lado del servidor (gen_random_uuid()).
CREATE EXTENSION IF NOT EXISTS pgcrypto;
