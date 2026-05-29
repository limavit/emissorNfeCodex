INSERT INTO sefaz_rejection_catalog (code, message, friendly_message, possible_cause, suggested_fix)
VALUES
('999', 'Rejeicao de exemplo para ambiente de desenvolvimento', 'Retorno simulado da SEFAZ.', 'Gateway SEFAZ ainda em modo stub.', 'Consultar a documentacao oficial vigente antes de habilitar transmissao real.')
ON CONFLICT (code) DO NOTHING;
