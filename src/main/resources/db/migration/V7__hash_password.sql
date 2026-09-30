-- Re-hashear las contraseñas semilla con BCrypt (ya no se almacenan en texto plano)
UPDATE usuario SET password = '$2a$10$Qtna9aNOq00Sdy1e7FTfIOP18dDGeEhK4N623xgsdT/GkMIG7uHga'
WHERE nombre_usuario = 'admin@duoc.cl' AND password = 'admin123';

UPDATE usuario SET password = '$2a$10$qmuoB1VpEu/C76CMEmA0vOt0oDeRVCi3Qt5RhJ/xvD7qp58dCt.0O'
WHERE nombre_usuario = 'vendedores@gmail.cl' AND password = 'vendedor123';

UPDATE usuario SET password = '$2a$10$Z44fXZkyfaZ5/s2Pwpv7S.G6cmYPRIpqOT1vX4KYHgar75b0mK4Ua'
WHERE nombre_usuario = 'cliente@duoc.cl' AND password = 'cliente123';