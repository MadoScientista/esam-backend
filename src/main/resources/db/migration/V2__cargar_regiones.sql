-- Carga de las 16 regiones de Chile (norte a sur).
-- Los IDs se asignan con AUTO_INCREMENT 1..16 en el orden de inserción.
USE `esam_db`;

INSERT INTO `region` (`nombre`) VALUES
('Región de Arica y Parinacota'),      -- 1
('Región de Tarapacá'),                -- 2
('Región de Antofagasta'),             -- 3
('Región de Atacama'),                 -- 4
('Región de Coquimbo'),                -- 5
('Región de Valparaíso'),              -- 6
('Región Metropolitana de Santiago'),  -- 7
('Región del Libertador General Bernardo O''Higgins'), -- 8
('Región del Maule'),                  -- 9
('Región de Ñuble'),                   -- 10
('Región del Biobío'),                 -- 11
('Región de la Araucanía'),            -- 12
('Región de los Ríos'),                -- 13
('Región de los Lagos'),               -- 14
('Región de Aysén del General Carlos Ibáñez del Campo'), -- 15
('Región de Magallanes y de la Antártica Chilena');     -- 16