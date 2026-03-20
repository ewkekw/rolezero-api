-- V5__Missing_tables.sql
-- Adiciona tabelas que estavam faltando no schema devido à consolidação de migrations anterior.

-- 1. Tabela Tokens Revogados (Blacklist de JWT)
CREATE TABLE IF NOT EXISTS tokens_revogados (
    token_hash VARCHAR(64) PRIMARY KEY,
    data_revogacao TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabela de Chat do Evento
CREATE TABLE IF NOT EXISTS role_mensagem_chat (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    evento_id UUID NOT NULL REFERENCES role_evento(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL REFERENCES role_usuario(id) ON DELETE CASCADE,
    conteudo TEXT NOT NULL,
    timestamp_envio TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    tipo VARCHAR(20) NOT NULL DEFAULT 'TEXT'
);
CREATE INDEX IF NOT EXISTS idx_mensagem_chat_evento ON role_mensagem_chat(evento_id, timestamp_envio DESC);

-- 3. Tabela de Avaliações de Usuário
CREATE TABLE IF NOT EXISTS role_avaliacao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    avaliador_id UUID NOT NULL REFERENCES role_usuario(id) ON DELETE CASCADE,
    avaliado_id UUID NOT NULL REFERENCES role_usuario(id) ON DELETE CASCADE,
    evento_id UUID REFERENCES role_evento(id) ON DELETE SET NULL,
    nota INTEGER NOT NULL CHECK (nota >= 1 AND nota <= 5),
    comentario TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (avaliador_id, avaliado_id, evento_id)
);
CREATE INDEX IF NOT EXISTS idx_avaliacao_avaliado ON role_avaliacao(avaliado_id, created_at DESC);

-- Habilitar RLS (Seguindo o padrão do projeto)
ALTER TABLE IF EXISTS tokens_revogados ENABLE ROW LEVEL SECURITY;
ALTER TABLE IF EXISTS role_mensagem_chat ENABLE ROW LEVEL SECURITY;
ALTER TABLE IF EXISTS role_avaliacao ENABLE ROW LEVEL SECURITY;
