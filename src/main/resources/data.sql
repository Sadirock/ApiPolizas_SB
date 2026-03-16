INSERT INTO polizas (nombre, tipo, estado, canon, prima, fecha_inicio, fecha_fin)
VALUES
    ('Póliza Individual Salud', 'INDIVIDUAL', 'ACTIVA',   150000.00, 12000.00, '2026-03-13', '2027-01-01'),
    ('Póliza Colectiva Empresa', 'COLECTIVA', 'ACTIVA',   500000.00, 45000.00, '2026-03-13', '2027-03-01'),    
    ('Póliza Individual Vida',   'INDIVIDUAL', 'RENOVADA', 200000.00, 18000.00, '2026-03-13', '2029-06-01'),
    ('Póliza Colectiva Pymes',   'COLECTIVA', 'CANCELADA', 300000.00, 25000.00, '2026-03-13', '2028-01-01');

-- Riesgos de prueba
INSERT INTO riesgos (descripcion, asegurado, estado, poliza_id)
VALUES
    ('Cobertura médica básica',     'Kevin El Papacho',    'ACTIVO',    1),
    ('Incendio sede principal',     'La Colectiva Élite',  'ACTIVO',    2),
    ('Robo y hurto',                'La Colectiva Élite',  'ACTIVO',    2),
    ('Cobertura vida total',        'Ana Gómez',     'ACTIVO',    3),
    ('Responsabilidad civil',       'Pyme Ejemplo',  'CANCELADO', 4);