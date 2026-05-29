# Arquitetura

O projeto usa monorepo com backend, frontend, banco, infraestrutura, scripts e documentacao no mesmo repositorio.

O backend Spring Boot concentra regras de negocio, seguranca, multiempresa, validacoes, geracao de XML, assinatura, armazenamento fiscal, logs e gateway SEFAZ. O pacote `sefaz` isola webservices e endpoints para permitir atualizacao de URLs, schemas e notas tecnicas.

O frontend Angular oferece login, selecao de empresa emissora, dashboard, cadastros, fluxo de NF-e, logs e auditoria.

Todos os dados operacionais devem ser filtrados por `company_id`. Clientes, produtos, regras fiscais, NF-e, XMLs, DANFEs, logs e auditoria pertencem ao contexto da empresa selecionada.
