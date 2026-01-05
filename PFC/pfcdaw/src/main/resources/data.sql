-- Script de datos de proba para panadería PFC
-- Executar MANUALMENTE cando queiramos datos de proba no phpmyadmin de xampp
-- por consola: # mysql -u root -p panaderiaPFC < PFC/pfcdaw/src/main/resources/data-sample.sql
-- ============================================
-- INSERTAR CLIENTES/USUARIOS DE PRUEBA (si non existen)
-- ADMIN: admin@panaderia.com (password: admin123/ñ)
-- USER: juan.perez@example.com (password: user123/ñ)
-- NOTA: Contraseñas hasheadas con BCrypt
INSERT IGNORE INTO clientes (nombre, apellido, email, direccion, nombre_empresa, telefono, password, role)
VALUES 
('Admin', 'Panadería', 'admin@panaderia.com', 'Rúa Principal 1, Noia', 'Panadería PFC', '666000000', '$2a$10$.4LXWf6h7Drmb2BtjLL4ZO78juPCDxQoICkrW3uJ3miZtGeOdm9eu', 'ADMIN'),
('Forza', 'Depor', 'forza.Depor@example.com', 'Rúa de Noia 1', 'Empresa Deportivo', '666666666', '$2a$10$DgCgSUmnp/EG7SQP/0KJveGvb9OafJlszphAGWogfbP3uwfqmy5.e', 'USER');

-- ============================================
-- INSERTAR PRODUCTOS DE PRUEBA (si non existen)
INSERT IGNORE INTO productos (nombre, descripcion, precio, stock,imagen_url)
VALUES 
('Pan artesanal', 'Pan artesano con masa madre', 1.40, 500, '/assets/images/uploads/panArt.jpg'),
('Croissant', 'Croissant de mantequilla recién horneado', 1.20, 300, '/assets/images/uploads/cruasan.jpg'),
('Empanada millo de zamburiñas', 'Empanada de millo con zamburiñas tradicional gallega', 12.50, 150, '/assets/images/uploads/EmpMillo.jpg'),
('Rosca de Reyes', 'Rosca tradicional con frutas confitadas', 8.50, 100, '/assets/images/uploads/redonda.jpg');

-- ============================================
-- NOTA: Pedidos NON se insertan aquí van co endpoint POST /pedidos
-- para que o stock se reduza correctamente

-- limpiar todo e despois executar o data-sample si queremos ter todo limpo de 0
-- TRUNCATE TABLE lineas_pedido;
-- TRUNCATE TABLE pedidos;
-- TRUNCATE TABLE productos;
-- TRUNCATE TABLE clientes;