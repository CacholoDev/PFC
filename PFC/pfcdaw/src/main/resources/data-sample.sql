-- Script de datos de proba para panadería PFC
-- Executar MANUALMENTE cando queiramos datos de proba no phpmyadmin de xampp
-- por consola: # mysql -u root -p panaderiaPFC < PFC/pfcdaw/src/main/resources/data-sample.sql
-- ============================================
-- INSERTAR CLIENTES DE PRUEBA (si non existen)
INSERT IGNORE INTO clientes (nombre, apellido, email, direccion, telefono)
VALUES 
('Juan', 'Pérez', 'juan.perez@example.com', 'Rúa Principal 1, Noia', '666666666'),
('María', 'García', 'maria.garcia@example.com', 'Avenida Galicia 1, Santiago', '981820000'),
('Anxo', 'López', 'anxo.lopez@example.com', 'Praza da Coruña 1, Coruña', '606060606');

-- ============================================
-- INSERTAR PRODUCTOS DE PRUEBA (si non existen)
INSERT IGNORE INTO productos (nombre, descripcion, precio, stock)
VALUES 
('Pan de Centeno', 'Pan artesano de centeno con masa madre', 1.40, 500),
('Croissant', 'Croissant de mantequilla recién horneado', 1.20, 300),
('Empanada millo de zamburiñas', 'Empanada de millo con zamburiñas tradicional gallega', 12.50, 150),
('Rosca de Reyes', 'Rosca tradicional con frutas confitadas', 8.50, 100);

-- ============================================
-- NOTA: Pedidos NON se insertan aquí van co endpoint POST /pedidos
-- para que o stock se reduza correctamente

-- limpiar todo: e despois executar o data-sample si queremos ter todo limpo de 0
TRUNCATE TABLE lineas_pedido;
TRUNCATE TABLE pedidos;
TRUNCATE TABLE productos;
TRUNCATE TABLE clientes;