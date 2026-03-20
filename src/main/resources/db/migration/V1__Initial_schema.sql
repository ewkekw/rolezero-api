-- V1__Initial_schema.sql
-- Modelagem de Banco de Dados Consolidada para o Role0
-- Focada em performance, integridade e uso do PostGIS.
-- Inclui: Schema, Tabelas de Domínio, Índices, Triggers e RLS.

-- 1. Extensões e Schemas
-- NOTA: PostGIS já é instalado pelo Supabase no schema 'extensions'.
-- Tentativa de CREATE EXTENSION causa erro no PgBouncer/Render.
-- Tabelas abaixo usam tipos PostGIS (GEOMETRY) que já estão disponíveis via search_path.

-- 2. Tabela de Usuários (Agregação de Identidade e Autenticidade)
CREATE TABLE IF NOT EXISTS role_usuario (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    biometria_validada BOOLEAN NOT NULL DEFAULT FALSE,
    token_verificacao_biometrica VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_usuario_biometria ON role_usuario(biometria_validada);

-- Tabela associativa estrita exigida pelo @ElementCollection MapStruct das listas do Hibernate
CREATE TABLE IF NOT EXISTS usuario_vibe_tags (
    usuario_id UUID NOT NULL REFERENCES role_usuario(id) ON DELETE CASCADE,
    tag VARCHAR(50) NOT NULL,
    PRIMARY KEY (usuario_id, tag)
);

-- 3. Tabela de Reputação (Agregação de Reputação/Gamificação)
CREATE TABLE IF NOT EXISTS role_perfil_reputacao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL UNIQUE REFERENCES role_usuario(id) ON DELETE CASCADE,
    trust_score DECIMAL(3, 2) NOT NULL DEFAULT 0.00 CHECK (trust_score >= 0.00 AND trust_score <= 5.00),
    avaliacoes_totais INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. Tabela de Estabelecimentos (Agregação Módulo B2B)
CREATE TABLE IF NOT EXISTS role_estabelecimento (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(150) NOT NULL,
    localizacao GEOMETRY(Point, 4326) NOT NULL,
    parceiro_oficial BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_estabelecimento_localizacao ON role_estabelecimento USING GIST (localizacao);

-- 5. Tabela Principal: Evento (A Alma do Role0)
CREATE TABLE IF NOT EXISTS role_evento (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    host_id UUID NOT NULL REFERENCES role_usuario(id) ON DELETE RESTRICT,
    titulo VARCHAR(100) NOT NULL,
    descricao TEXT,
    capacidade_maxima INTEGER NOT NULL CHECK (capacidade_maxima > 0),
    status VARCHAR(30) NOT NULL DEFAULT 'CRIADO',
    localizacao GEOMETRY(Point, 4326) NOT NULL,
    endereco_legivel VARCHAR(255),
    horario_inicio TIMESTAMP WITH TIME ZONE NOT NULL,
    incidente_reportado BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_evento_localizacao_gist ON role_evento USING GIST (localizacao);
CREATE INDEX IF NOT EXISTS idx_evento_status_inicio ON role_evento(status, horario_inicio);

-- 6. Tabela de Relacionamento N:N (Participantes Aprovados no Evento)
CREATE TABLE IF NOT EXISTS role_participantes_aprovados (
    evento_id UUID NOT NULL REFERENCES role_evento(id) ON DELETE CASCADE,
    participante_id UUID NOT NULL REFERENCES role_usuario(id) ON DELETE CASCADE,
    aprovado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (evento_id, participante_id)
);

-- Tabela de elementos da Collection `participantesAprovados` exigida pelo Hibernate JPA
CREATE TABLE IF NOT EXISTS evento_participantes (
    evento_id UUID NOT NULL REFERENCES role_evento(id) ON DELETE CASCADE,
    participante_id UUID NOT NULL,
    PRIMARY KEY (evento_id, participante_id)
);

-- 7. Tabela de Solicitações de Vaga (Intenção de Participação)
CREATE TABLE IF NOT EXISTS role_solicitacao_participacao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    evento_id UUID NOT NULL REFERENCES role_evento(id) ON DELETE CASCADE,
    usuario_id UUID NOT NULL REFERENCES role_usuario(id) ON DELETE CASCADE,
    status_solicitacao VARCHAR(30) NOT NULL DEFAULT 'PENDENTE', -- PENDENTE, APROVADA, REJEITADA
    data_solicitacao TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (evento_id, usuario_id)
);

CREATE INDEX IF NOT EXISTS idx_solicitacao_evento ON role_solicitacao_participacao(evento_id, status_solicitacao);

-- 8. Tabela Tokens Revogados (Blacklist de JWT)
CREATE TABLE IF NOT EXISTS tokens_revogados (
    token_hash VARCHAR(64) PRIMARY KEY,
    data_revogacao TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 9. Tabela de Chat do Evento
CREATE TABLE IF NOT EXISTS role_mensagem_chat (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    evento_id UUID NOT NULL REFERENCES role_evento(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL REFERENCES role_usuario(id) ON DELETE CASCADE,
    conteudo TEXT NOT NULL,
    timestamp_envio TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    tipo VARCHAR(20) NOT NULL DEFAULT 'TEXT'
);
CREATE INDEX IF NOT EXISTS idx_mensagem_chat_evento ON role_mensagem_chat(evento_id, timestamp_envio DESC);

-- 10. Tabela de Avaliações de Usuário
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

-- 11. Triggers de Timestamp Automático
CREATE OR REPLACE FUNCTION public.update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE 'plpgsql' SET search_path = '';

CREATE TRIGGER trg_update_usuario_updated_at BEFORE UPDATE ON role_usuario FOR EACH ROW EXECUTE PROCEDURE update_modified_column();
CREATE TRIGGER trg_update_reputacao_updated_at BEFORE UPDATE ON role_perfil_reputacao FOR EACH ROW EXECUTE PROCEDURE update_modified_column();
CREATE TRIGGER trg_update_estabelecimento_updated_at BEFORE UPDATE ON role_estabelecimento FOR EACH ROW EXECUTE PROCEDURE update_modified_column();
CREATE TRIGGER trg_update_evento_updated_at BEFORE UPDATE ON role_evento FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

-- 12. RLS — Row Level Security (Deny-All via PostgREST, acesso via JDBC backend mantém privilégios)
ALTER TABLE public.role_usuario ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.usuario_vibe_tags ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.role_perfil_reputacao ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.role_estabelecimento ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.role_evento ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.role_participantes_aprovados ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.evento_participantes ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.role_solicitacao_participacao ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.tokens_revogados ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.role_mensagem_chat ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.role_avaliacao ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.role_estabelecimento ENABLE ROW LEVEL SECURITY;
-- spatial_ref_sys e flyway_schema_history são tabelas de sistema/extensão
ALTER TABLE IF EXISTS public.flyway_schema_history ENABLE ROW LEVEL SECURITY;
