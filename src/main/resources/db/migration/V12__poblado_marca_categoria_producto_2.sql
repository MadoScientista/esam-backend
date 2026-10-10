
-- Poblado Marca
INSERT INTO `marca` (`nombre`) VALUES
    ('Artel'),
    ('Van Gogh');


-- Poblado Producto
INSERT INTO `producto` (`sku`, `nombre`, `descripcion`, `precio`, `stock`, `id_marca`) VALUES
    ('BLOCK1','Block para colorear Aves y Flora de Chile','12 ilustraciones - 25x32 cm. Papel acuarela 200 gms. Ilustrado por @franmendez.cl.',5990,6,7),
    ('BLOCK2','Block para colorear Flores de Estación','12 ilustraciones - 25x32 cm. Papel acuarela 200 gms. Ilustrado por @franmendez.cl.',5990,6,7),
    ('BLOCK3','Block Para Colorear Artel 26 Hojas 21X21 Cms','Colorear es una actividad que combina concentración, creatividad y relajación, y tener el soporte adecuado hace toda la diferencia. Este block de Artel está pensado para quienes disfrutan del coloreado como pasatiempo o práctica artística, tanto adultos que buscan un momento de desconexión como niños y jóvenes que exploran el dibujo y el color.',2690,12,7),
    ('BLOCK4','Block para colorear Mysticus','Que el universo confabule a tu favor y decretes en cada trazo de lápiz, rodearte de amor y tranquilidad. Con estas ilustraciones canaliza tus sentimientos y alinea tu ser interno. Usa este libro como base para llenarlo de magia con tus colores, abriendo el portal infinito y llénate de paz mientras coloreas. -Dani BlanQ',4790,8,7),
    ('OLEO1', 'Óleo Van Gogh 20ml','Óleo Van Gogh 20ml ofrece una pintura al óleo de alta calidad con una textura rica y un secado uniforme, Su fórmula concentrada garantiza colores vibrantes y duraderos, ideales para artistas que buscan precisión y profundidad en sus obras, El envase de 20ml es práctico para proyectos detallados y permite un manejo experto del material, Perfecto para técnicas mixtas y acabado profesional,',4690,9,8),
    ('OLEO2','Óleo 22 ml Artel','Producido con excelentes pigmentos y aglutinantes que dan fuerza y estabilidad a la pintura, Mayor resistencia a la luz.',1190,9,7);

-- Poblado CategoriaProducto
INSERT INTO `producto_categoria` (`id_producto`, `id_categoria`) VALUES 
    (20,3),(21,3),(22,3),(23,3),(24,9),(25,9);


-- Update imagen de categoria
UPDATE `categoria` SET 
    `imagen_url` = "https://res.cloudinary.com/yj8w8ucz/image/upload/v1791664345/categorias/3/imagen.webp", 
    `imagen_id_publico` = "categorias/3/imagen" 
WHERE `id_categoria` = 3;