-- ============================================================================
-- Sistema de Biblioteca UES - Script de base de datos
-- Motor: MySQL 8
-- Ejecutar una sola vez (crea la DB y los datos de prueba)
-- ============================================================================

CREATE DATABASE IF NOT EXISTS biblioteca_ues
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE biblioteca_ues;

CREATE TABLE IF NOT EXISTS autor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    nacionalidad VARCHAR(80) NOT NULL
);

CREATE TABLE IF NOT EXISTS libro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    anio_publicacion INT NOT NULL,
    editorial VARCHAR(100) NOT NULL,
    disponible BOOLEAN NOT NULL DEFAULT TRUE,
    autor_id INT NOT NULL,
    CONSTRAINT fk_libro_autor
        FOREIGN KEY (autor_id)
        REFERENCES autor(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS socio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    numero_socio VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS prestamo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion DATE NULL,
    devuelto BOOLEAN NOT NULL DEFAULT FALSE,
    libro_id INT NOT NULL,
    socio_id INT NOT NULL,
    CONSTRAINT fk_prestamo_libro
        FOREIGN KEY (libro_id)
        REFERENCES libro(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT fk_prestamo_socio
        FOREIGN KEY (socio_id)
        REFERENCES socio(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- ----------------------------------------------------------------------------
-- Datos de prueba: autores
-- ----------------------------------------------------------------------------
INSERT INTO autor (nombre, nacionalidad) VALUES
('Gabriel Garcia Marquez', 'Colombiana'),
('George Orwell', 'Britanica'),
('J.R.R. Tolkien', 'Britanica'),
('Isabel Allende', 'Chilena'),
('Isaac Asimov', 'Estadounidense'),
('Mario Vargas Llosa', 'Peruana');

-- ----------------------------------------------------------------------------
-- Datos de prueba: libros
-- ----------------------------------------------------------------------------
INSERT INTO libro (titulo, isbn, anio_publicacion, editorial, disponible, autor_id) VALUES
('Cien años de soledad', '978-0307474728', 1967, 'Editorial Sudamericana', TRUE, 1),
('El amor en los tiempos del cólera', '978-0307387264', 1985, 'Editorial Sudamericana', TRUE, 1),
('1984', '978-0451524935', 1949, 'Secker & Warburg', TRUE, 2),
('Rebelión en la granja', '978-0451526342', 1945, 'Secker & Warburg', TRUE, 2),
('El Señor de los Anillos: La Comunidad del Anillo', '978-0544003415', 1954, 'Allen & Unwin', TRUE, 3),
('El Hobbit', '978-0547928227', 1937, 'Allen & Unwin', TRUE, 3),
('La casa de los espíritus', '978-1501117015', 1982, 'Plaza & Janés', TRUE, 4),
('Largo pétalo de mar', '978-1984899170', 2019, 'Plaza & Janés', TRUE, 4),
('Fundación', '978-0553293357', 1951, 'Gnome Press', TRUE, 5),
('Yo, Robot', '978-0553294385', 1950, 'Gnome Press', TRUE, 5),
('La ciudad y los perros', '978-8420471830', 1963, 'Editorial Seix Barral', TRUE, 6),
('La fiesta del Chivo', '978-8466332149', 2000, 'Alfaguara', TRUE, 6);

-- ----------------------------------------------------------------------------
-- Datos de prueba: socios
-- ----------------------------------------------------------------------------
INSERT INTO socio (nombre, numero_socio, email) VALUES
('Kevin Alexander Santos Recinos', 'SR23008', 'sr23008@ues.edu.sv'),
('Luis Enrique Aguilar Arevalo', 'AA23081', 'aa23081@ues.edu.sv'),
('Jorge Luis Monge Cardona', 'MC24104', 'mc24104@ues.edu.sv'),
('Alexander Rigoberto Lopez Alfaro', 'LA17014', 'la17014@ues.edu.sv');
