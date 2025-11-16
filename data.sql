-- Estados para Solicitudes y Tramos
INSERT INTO estados (descripcion) VALUES ('BORRADOR') ON CONFLICT (descripcion) DO NOTHING;
INSERT INTO estados (descripcion) VALUES ('PENDIENTE') ON CONFLICT (descripcion) DO NOTHING;
INSERT INTO estados (descripcion) VALUES ('ENTREGADA') ON CONFLICT (descripcion) DO NOTHING;
INSERT INTO estados (descripcion) VALUES ('CANCELADA') ON CONFLICT (descripcion) DO NOTHING;
INSERT INTO estados (descripcion) VALUES ('ESTIMADO') ON CONFLICT (descripcion) DO NOTHING;
INSERT INTO estados (descripcion) VALUES ('ASIGNADO') ON CONFLICT (descripcion) DO NOTHING;
INSERT INTO estados (descripcion) VALUES ('INICIADO') ON CONFLICT (descripcion) DO NOTHING;
INSERT INTO estados (descripcion) VALUES ('FINALIZADO') ON CONFLICT (descripcion) DO NOTHING;