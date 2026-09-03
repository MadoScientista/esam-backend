
INSERT INTO rol_usuario (nombre) VALUE 
('admin'),
('usuario');

INSERT INTO usuario (
    nombres, 
    a_paterno, 
    nombre_usuario,
    correo,
    id_rol_usuario,
    id_region,
    id_comuna) VALUE
('Sam', 'Cortés', 'samadmin', 'samadmin@duoc.cl',1, 7, 1),
('Juan', 'Jara', 'juanjara', 'juja@duoc.cl',1, 7, 2);