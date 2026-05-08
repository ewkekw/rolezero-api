-- V2__Add_auth_credentials.sql
-- Adiciona coluna de senha (hash BCrypt) e índice de busca por email.
-- Esta migration é segura para bancos já existentes: usa IF NOT EXISTS.

ALTER TABLE public.role_usuario
    ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255);

-- Garante que buscas por email em login (O(log n)) sejam eficientes
CREATE UNIQUE INDEX IF NOT EXISTS idx_usuario_email ON public.role_usuario(email);
