# Seguranca

- Autenticacao JWT.
- Senhas com BCrypt.
- Certificado A1 criptografado.
- Chave de criptografia via variavel de ambiente.
- Senha do certificado nunca em texto puro.
- Logs sem JWT, senha ou certificado descriptografado.
- Auditoria para login, empresa, certificado, NF-e, XML, transmissao, downloads e producao.
- Protecao contra acesso cruzado por `company_id`.

Ambiente de producao exige revisao de segredos, CORS, backups, politicas de acesso e verificacao fiscal.
