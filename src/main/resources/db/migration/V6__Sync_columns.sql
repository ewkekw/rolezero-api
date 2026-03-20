-- V6__Sync_columns.sql
-- Adiciona colunas que estavam faltando no schema devido à dessincronização com o estado inicial do Supabase.

-- 1. Adicionar endereco_legivel à tabela role_evento
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'role_evento' AND column_name = 'endereco_legivel') THEN
        ALTER TABLE role_evento ADD COLUMN endereco_legivel VARCHAR(255);
    END IF;
END $$;
