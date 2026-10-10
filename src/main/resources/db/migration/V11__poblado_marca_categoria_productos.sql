
-- Poblado Marca
INSERT INTO `marca` (`nombre`) VALUES
    ('Alotek'),
    ('ALOColor');

-- Poblado Categoria
UPDATE `categoria` SET `nombre` = 'Cuadernos', `slug` = 'cuadernos',
    `imagen_url` = 'https://res.cloudinary.com/yj8w8ucz/image/upload/v1791483167/categorias/1/imagen.jpg',
    `imagen_id_publico` = 'categorias/1/imagen'
    WHERE `slug` = 'cuaderno';
UPDATE `categoria` SET `nombre` = 'Cartulinas', `slug` = 'cartulinas',
    `imagen_url` = 'https://res.cloudinary.com/yj8w8ucz/image/upload/v1791483221/categorias/2/imagen.jpg',
    `imagen_id_publico` = 'categorias/2/imagen'
    WHERE `slug` = 'cartulina';
UPDATE `categoria` SET `nombre` = 'Blocks', `slug` = 'blocks'
    WHERE `slug` = 'block';

INSERT INTO `categoria` (`nombre`, `slug`, `imagen_url`, `imagen_id_publico`) VALUES
    ('Plumones','plumones','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791483392/categorias/4/imagen.jpg','categorias/4/imagen'),
    ('Adhesivos','adhesivos',NULL,NULL),
    ('Lápices','lapices',NULL,NULL),
    ('Destacadores','destacadores',NULL,NULL),
    ('Pastas','pastas',NULL,NULL),
    ('Pinturas','pinturas',NULL,NULL),
    ('Témperas','temperas',NULL,NULL);

UPDATE `categoria` `c` JOIN `categoria` `p` ON `p`.`slug` = 'plumones' SET `c`.`padre_id` = `p`.`id_categoria` WHERE `c`.`slug` = 'destacadores';
UPDATE `categoria` `c` JOIN `categoria` `p` ON `p`.`slug` = 'lapices' SET `c`.`padre_id` = `p`.`id_categoria` WHERE `c`.`slug` = 'pastas';
UPDATE `categoria` `c` JOIN `categoria` `p` ON `p`.`slug` = 'pinturas' SET `c`.`padre_id` = `p`.`id_categoria` WHERE `c`.`slug` = 'temperas';


-- Poblado producto
INSERT INTO `producto` (`sku`, `nombre`, `descripcion`, `precio`, `stock`, `id_marca`) VALUES
    ('CUAD1','Cuaderno universitario 7mm 100 hojas Lotus Torre surtido','Cuaderno universitario ideal para tomar apuntes con estilo y precisión.',2490,20,(SELECT `id_marca` FROM `marca` WHERE `nombre` = 'Torre')),
    ('CUAD2','Cuaderno book 7mm 120hj natura Colon','Cuaderno tamaño book ideal para tomar apuntes con estilo y comodidad.',3490,20,(SELECT `id_marca` FROM `marca` WHERE `nombre` = 'Colón')),
    ('CUAD3','Cuaderno top 7mm 150hj sports','Cuaderno tamaño carta ideal para tomar apuntes. Perfecto para los jóvenes amantes de los deportes extremos.',4790,10,(SELECT `id_marca` FROM `marca` WHERE `nombre` = 'Colón')),
    ('CART1','Bolson cartulina espanola 10 colores','Bolson de cartulina española con 10 pliegos en 10 colores diferentes, tamaño 25 x 32,5 cm.',2690,15,(SELECT `id_marca` FROM `marca` WHERE `nombre` = 'Torre')),
    ('CART2','Bolson cartulina pintada 16 hojas','Bolson de cartulina pintada con 18 pliegos en 14 colores diferentes, tamaño 26,5 x 37,5 cm. Perfecto para proyectos escolares y manualidades.',2490,12,(SELECT `id_marca` FROM `marca` WHERE `nombre` = 'Torre')),
    ('CART3','Bolsón cartulina flúor 26x38cm 6 hojas 6 colores','Set de cartulinas fluorescentes ideal para manualidades y proyectos creativos.',2290,8,(SELECT `id_marca` FROM `marca` WHERE `nombre` = 'Torre')),
    ('PLUM1','Plumón Polycolor 326 20 colores','Descubre el plumón Policolor de Staedtler, ideal para colorear y crear obras de arte vibrantes.',10490,18,(SELECT `id_marca` FROM `marca` WHERE `nombre` = 'Staedtler')),
    ('ADH1','Adhesivo en barra 10gr','Ideal para pegar papel, cartón, fotos, etiquetas y telas. Lavable en la mayoría de los tejidos.',950,20,(SELECT `id_marca` FROM `marca` WHERE `nombre` = 'Staedtler')),
    ('LAP1','Lápiz grafito hb triangular jumbo 119','Útiles para dibujar, escribir o bosquejar líneas suaves con tonos claros y delgados.',2490,38,(SELECT `id_marca` FROM `marca` WHERE `nombre` = 'Staedtler')),
    ('PLUM2','Plumón permanente 352 Lumo punta redonda 2.0mm verde','Para escribir y dibujar en casi todo tipo de superficies con facilidad y precisión.',1990,6,(SELECT `id_marca` FROM `marca` WHERE `nombre` = 'Staedtler')),
    ('DEST1','Destacador Textsurfer 364-2 rojo','Ideal para resaltar escrituras sobre papel, fax y fotocopias sin manchar',2290,29,(SELECT `id_marca` FROM `marca` WHERE `nombre` = 'Staedtler')),
    ('PLUM3','Plumón 0.7mm pigment liner 308','Ideal para escribir, dibujar y colorear',5290,8,(SELECT `id_marca` FROM `marca` WHERE `nombre` = 'Staedtler')),
    ('PAST1','Bolígrafo triangular 1.0mm azul','Especial para escribir o subrayar frases o párrafos de interés.',630,36,(SELECT `id_marca` FROM `marca` WHERE `nombre` = 'Staedtler')),
    ('PAST2','Bolígrafo Stick 430 punta 1.0 mm azul','Escribe sobre todo tipo de papeles, ideal para oficina, colegio, universidades y hogar.',750,50,(SELECT `id_marca` FROM `marca` WHERE `nombre` = 'Staedtler')),
    ('CUAD4','Cuaderno universitario 7MM 100 hojas Lito Colon surtido','Cuaderno formato universitario 25 x 20,2 cm. Tapa de cartulina. Espiral doble recubierto. Barniz uv brillante. Papel 50 gr.',1790,33,(SELECT `id_marca` FROM `marca` WHERE `nombre` = 'Colón')),
    ('CUAD5','Cuaderno college 7MM 80 hojas liso color azul','Diseño liso de 80 hojas para que puedas escribir tus materias. Para uso pre escolar y básica',1250,18,(SELECT `id_marca` FROM `marca` WHERE `nombre` = 'Alotek')),
    ('CUAD6','Cuaderno universitario croquis 100 hojas tapa blanda colores surtido','Cuaderno para tomar apuntes tamaño universitario',1790,31,(SELECT `id_marca` FROM `marca` WHERE `nombre` = 'Alotek')),
    ('TEMP1','Tempera 12 colores 15cc','La témpera escolar de 12 colores y 15cc de ALOColor es perfecta para estimular la creatividad de los niños. Con pigmentos orgánicos, está libre de materiales pesados como el plomo, garantizando seguridad en su uso. Sus colores brillantes y fluorescentes son mezclables entre sí, permitiendo una amplia gama de tonalidades para proyectos artísticos.',2690,18,(SELECT `id_marca` FROM `marca` WHERE `nombre` = 'ALOColor')),
    ('LAP2','Estuche lápices de color Faber-Castell 24 col','Los lápices de color Faber-Castell son una referencia clásica para quienes valoran la calidad en cada trazo. Este estuche de 24 colores en formato largo está pensado tanto para escolares que comienzan a explorar el dibujo y la pintura, como para adultos que buscan una herramienta confiable para colorear o hacer apuntes visuales. La mina resistente y los colores bien pigmentados hacen que este set se destaque frente a opciones genéricas, entregando resultados uniformes y duraderos sobre papel. Es una compra que responde bien al uso cotidiano en la sala de clases o en casa.',6990,35,(SELECT `id_marca` FROM `marca` WHERE `nombre` = 'Faber-Castell'));


-- Poblado ImagenProducto
INSERT INTO `imagen_producto` (`id_publico`, `url`, `orden`, `principal`, `producto_id`) VALUES
    ('productos/1/kggpfwiacomivawekxyv','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791482440/productos/1/kggpfwiacomivawekxyv.jpg',1,0,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'CUAD1')),
    ('productos/1/bukhn4lc6invwmdx30jn','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791482441/productos/1/bukhn4lc6invwmdx30jn.jpg',0,1,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'CUAD1')),
    ('productos/2/pscotmr1jejf56dqedmy','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791482468/productos/2/pscotmr1jejf56dqedmy.jpg',0,1,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'CUAD2')),
    ('productos/3/odzqpztlcrtxuwxb1bul','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791482504/productos/3/odzqpztlcrtxuwxb1bul.jpg',0,1,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'CUAD3')),
    ('productos/4/brj8kxgezjtptiq8jykh','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791482525/productos/4/brj8kxgezjtptiq8jykh.jpg',0,1,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'CART1')),
    ('productos/5/fhskzxrfhrxlivnzoub0','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791482546/productos/5/fhskzxrfhrxlivnzoub0.jpg',0,1,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'CART2')),
    ('productos/6/tkklowahvxypuvurfsfk','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791482573/productos/6/tkklowahvxypuvurfsfk.jpg',0,1,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'CART3')),
    ('productos/7/m71wjwnitd4dpt7ynxnt','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791482591/productos/7/m71wjwnitd4dpt7ynxnt.jpg',0,1,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'PLUM1')),
    ('productos/8/c0nnomlymosfbwtari6t','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791482610/productos/8/c0nnomlymosfbwtari6t.jpg',0,1,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'ADH1')),
    ('productos/9/nj6lsgaajantuhbkt3pz','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791482669/productos/9/nj6lsgaajantuhbkt3pz.jpg',0,1,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'LAP1')),
    ('productos/10/lcnlkfdxxgzm1ndseuui','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791482687/productos/10/lcnlkfdxxgzm1ndseuui.jpg',0,1,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'PLUM2')),
    ('productos/11/ohhlg8faqoks7pfq3vn0','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791482709/productos/11/ohhlg8faqoks7pfq3vn0.jpg',0,1,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'DEST1')),
    ('productos/12/nug7vp4ujbuj9dqpn7ca','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791482848/productos/12/nug7vp4ujbuj9dqpn7ca.jpg',0,1,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'PLUM3')),
    ('productos/13/nwtdh3feiykb8wysi8uj','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791482869/productos/13/nwtdh3feiykb8wysi8uj.jpg',0,1,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'PAST1')),
    ('productos/14/eaf5pwwlck65blwd4mqf','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791482892/productos/14/eaf5pwwlck65blwd4mqf.jpg',0,1,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'PAST2')),
    ('productos/15/ssxiveedpyysrevrc1cl','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791482922/productos/15/ssxiveedpyysrevrc1cl.jpg',0,1,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'CUAD4')),
    ('productos/15/ywxgg7ljlommgsdjutab','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791482924/productos/15/ywxgg7ljlommgsdjutab.jpg',1,0,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'CUAD4')),
    ('productos/16/gwhjmcdlzoaiwpvvr3la','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791482945/productos/16/gwhjmcdlzoaiwpvvr3la.jpg',0,1,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'CUAD5')),
    ('productos/17/prm6gvcbnwkohx255lw2','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791482975/productos/17/prm6gvcbnwkohx255lw2.jpg',0,1,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'CUAD6')),
    ('productos/17/jbgasidlybjohqkg7m2m','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791482976/productos/17/jbgasidlybjohqkg7m2m.jpg',1,0,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'CUAD6')),
    ('productos/18/ohiil0v0cmywymaryl12','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791482998/productos/18/ohiil0v0cmywymaryl12.jpg',0,1,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'TEMP1')),
    ('productos/19/jk1t6bud4nwtlz5gptqt','https://res.cloudinary.com/yj8w8ucz/image/upload/v1791483059/productos/19/jk1t6bud4nwtlz5gptqt.webp',0,1,(SELECT `id_producto` FROM `producto` WHERE `sku` = 'LAP2'));

-- Poblado ProductoCategoria
INSERT INTO `producto_categoria` (`id_producto`, `id_categoria`)
    SELECT `p`.`id_producto`, `c`.`id_categoria` FROM `producto` `p` JOIN `categoria` `c` ON `c`.`slug` = 'cuadernos' WHERE `p`.`sku` = 'CUAD1';
INSERT INTO `producto_categoria` (`id_producto`, `id_categoria`)
    SELECT `p`.`id_producto`, `c`.`id_categoria` FROM `producto` `p` JOIN `categoria` `c` ON `c`.`slug` = 'cuadernos' WHERE `p`.`sku` = 'CUAD2';
INSERT INTO `producto_categoria` (`id_producto`, `id_categoria`)
    SELECT `p`.`id_producto`, `c`.`id_categoria` FROM `producto` `p` JOIN `categoria` `c` ON `c`.`slug` = 'cuadernos' WHERE `p`.`sku` = 'CUAD3';
INSERT INTO `producto_categoria` (`id_producto`, `id_categoria`)
    SELECT `p`.`id_producto`, `c`.`id_categoria` FROM `producto` `p` JOIN `categoria` `c` ON `c`.`slug` = 'cuadernos' WHERE `p`.`sku` = 'CUAD4';
INSERT INTO `producto_categoria` (`id_producto`, `id_categoria`)
    SELECT `p`.`id_producto`, `c`.`id_categoria` FROM `producto` `p` JOIN `categoria` `c` ON `c`.`slug` = 'cuadernos' WHERE `p`.`sku` = 'CUAD5';
INSERT INTO `producto_categoria` (`id_producto`, `id_categoria`)
    SELECT `p`.`id_producto`, `c`.`id_categoria` FROM `producto` `p` JOIN `categoria` `c` ON `c`.`slug` = 'cuadernos' WHERE `p`.`sku` = 'CUAD6';
INSERT INTO `producto_categoria` (`id_producto`, `id_categoria`)
    SELECT `p`.`id_producto`, `c`.`id_categoria` FROM `producto` `p` JOIN `categoria` `c` ON `c`.`slug` = 'cartulinas' WHERE `p`.`sku` = 'CART1';
INSERT INTO `producto_categoria` (`id_producto`, `id_categoria`)
    SELECT `p`.`id_producto`, `c`.`id_categoria` FROM `producto` `p` JOIN `categoria` `c` ON `c`.`slug` = 'cartulinas' WHERE `p`.`sku` = 'CART2';
INSERT INTO `producto_categoria` (`id_producto`, `id_categoria`)
    SELECT `p`.`id_producto`, `c`.`id_categoria` FROM `producto` `p` JOIN `categoria` `c` ON `c`.`slug` = 'cartulinas' WHERE `p`.`sku` = 'CART3';
INSERT INTO `producto_categoria` (`id_producto`, `id_categoria`)
    SELECT `p`.`id_producto`, `c`.`id_categoria` FROM `producto` `p` JOIN `categoria` `c` ON `c`.`slug` = 'plumones' WHERE `p`.`sku` = 'PLUM1';
INSERT INTO `producto_categoria` (`id_producto`, `id_categoria`)
    SELECT `p`.`id_producto`, `c`.`id_categoria` FROM `producto` `p` JOIN `categoria` `c` ON `c`.`slug` = 'plumones' WHERE `p`.`sku` = 'PLUM2';
INSERT INTO `producto_categoria` (`id_producto`, `id_categoria`)
    SELECT `p`.`id_producto`, `c`.`id_categoria` FROM `producto` `p` JOIN `categoria` `c` ON `c`.`slug` = 'plumones' WHERE `p`.`sku` = 'PLUM3';
INSERT INTO `producto_categoria` (`id_producto`, `id_categoria`)
    SELECT `p`.`id_producto`, `c`.`id_categoria` FROM `producto` `p` JOIN `categoria` `c` ON `c`.`slug` = 'adhesivos' WHERE `p`.`sku` = 'ADH1';
INSERT INTO `producto_categoria` (`id_producto`, `id_categoria`)
    SELECT `p`.`id_producto`, `c`.`id_categoria` FROM `producto` `p` JOIN `categoria` `c` ON `c`.`slug` = 'lapices' WHERE `p`.`sku` = 'LAP1';
INSERT INTO `producto_categoria` (`id_producto`, `id_categoria`)
    SELECT `p`.`id_producto`, `c`.`id_categoria` FROM `producto` `p` JOIN `categoria` `c` ON `c`.`slug` = 'lapices' WHERE `p`.`sku` = 'LAP2';
INSERT INTO `producto_categoria` (`id_producto`, `id_categoria`)
    SELECT `p`.`id_producto`, `c`.`id_categoria` FROM `producto` `p` JOIN `categoria` `c` ON `c`.`slug` = 'destacadores' WHERE `p`.`sku` = 'DEST1';
INSERT INTO `producto_categoria` (`id_producto`, `id_categoria`)
    SELECT `p`.`id_producto`, `c`.`id_categoria` FROM `producto` `p` JOIN `categoria` `c` ON `c`.`slug` = 'pastas' WHERE `p`.`sku` = 'PAST1';
INSERT INTO `producto_categoria` (`id_producto`, `id_categoria`)
    SELECT `p`.`id_producto`, `c`.`id_categoria` FROM `producto` `p` JOIN `categoria` `c` ON `c`.`slug` = 'pastas' WHERE `p`.`sku` = 'PAST2';
INSERT INTO `producto_categoria` (`id_producto`, `id_categoria`)
    SELECT `p`.`id_producto`, `c`.`id_categoria` FROM `producto` `p` JOIN `categoria` `c` ON `c`.`slug` = 'temperas' WHERE `p`.`sku` = 'TEMP1';
