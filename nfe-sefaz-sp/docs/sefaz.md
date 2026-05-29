# SEFAZ

O sistema separa homologacao e producao. Novas empresas iniciam em homologacao. Numeracao, XMLs, logs e endpoints nao devem ser misturados entre ambientes.

Servicos previstos:

- Consulta status.
- Autorizacao NF-e.
- Consulta recibo.
- Consulta protocolo.
- Evento de cancelamento.
- Carta de Correcao Eletronica.
- Inutilizacao.

Os endpoints ficam centralizados em `backend/src/main/resources/sefaz/endpoints.yml`. Os valores atuais sao placeholders. Antes de transmissao real, atualizar a partir da documentacao oficial vigente do Portal Nacional da NF-e e SEFAZ-SP.
