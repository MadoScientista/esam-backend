
INSERT INTO rol_usuario (nombre) VALUE 
('admin'),
('vendedor'),
('cliente');

INSERT INTO usuario (
    nombres,
    a_paterno,
    a_materno,
    nombre_usuario,
    rut,
    dv,
    direccion,
    fecha_nacimiento,
    telefono,
    password,
    correo,
    id_rol_usuario,
    id_region,
    id_comuna) VALUES
('Carlos', 'Soto', 'Mendoza', 'admin@duoc.cl', 12345678, 'K',
 'Av. Providencia 1200, Providencia', '1985-03-14', 56912345678,
 'admin123', 'admin@duoc.cl', 1, 7, 1),
('Valentina', 'Rojas', 'Pérez', 'vendedores@gmail.cl', 23456789, '1',
 'Las Condes 4560, Las Condes', '1992-07-22', 56923456789,
 'vendedor123', 'vendedores@gmail.cl', 2, 7, 2),
('Matías', 'González', 'Díaz', 'cliente@duoc.com', 34567890, '7',
 'La Dehesa 300, Lo Barnechea', '1998-11-05', 56934567890,
 'cliente123', 'cliente@duoc.com', 3, 7, 3);