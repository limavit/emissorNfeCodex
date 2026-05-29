# NF-e SEFAZ-SP

Sistema web em monorepo para emissao e gestao de NF-e modelo 55 para empresas de Sao Paulo, com multiempresa, login JWT, cadastros fiscais, fluxo de NF-e, XML, assinatura, DANFE, logs e auditoria.

## Stack

- Backend: Java 21, Spring Boot, Spring Security, JPA, Flyway, PostgreSQL, JWT.
- Frontend: Angular.
- Banco: PostgreSQL com migrations Flyway.
- Infra: Docker Compose, Nginx para frontend, scripts e Makefile.

## Monorepo

- `backend/`: API e servicos fiscais.
- `frontend/`: aplicacao web.
- `database/`: documentacao de migrations e seeds ficticios.
- `infrastructure/`: Docker/Nginx auxiliares.
- `docs/`: arquitetura, requisitos, fluxo, SEFAZ, seguranca e regras fiscais.
- `scripts/`: comandos de desenvolvimento.
- `storage/`: XMLs, DANFEs e artefatos fiscais locais. Apenas `.gitkeep` deve ser versionado.

## Configuracao

```bash
cp .env.example .env
```

Troque todos os placeholders. Nunca use segredos reais no repositório.

## Docker

```bash
docker compose up -d
```

Com Makefile:

```bash
make dev
make build
make test
make logs
make down
```

## Backend local

```bash
cd backend
mvn spring-boot:run
```

O Flyway executa as migrations automaticamente.

## Frontend local

```bash
cd frontend
npm install
npm start
```

## Fluxo inicial

1. Cadastre usuario.
2. Faca login.
3. Cadastre empresa emissora.
4. Selecione o CNPJ.
5. Cadastre certificado A1, clientes, produtos e regras fiscais.
6. Crie NF-e em rascunho.
7. Valide, gere XML, assine e transmita em homologacao.
8. Consulte logs, baixe XML e DANFE.

## Cuidados fiscais

NF-e modelo 55 e voltada a circulacao de mercadorias e operacoes especificas. Servicos sujeitos a NFS-e municipal nao devem ser emitidos como NF-e sem validacao fiscal. O sistema nao inventa tributacao: CFOP, CST, CSOSN, ICMS, IPI, PIS e COFINS devem ser definidos por regras fiscais revisadas por contador, principalmente em producao.

Antes de habilitar transmissao real, consulte a documentacao oficial vigente do Portal Nacional da NF-e e da SEFAZ-SP: MOC, notas tecnicas, schemas XML, webservices, regras de validacao, homologacao e producao.

## Seguranca

Nao versionar `.env`, certificados `.pfx/.p12`, XMLs reais, DANFEs reais, logs sensiveis ou dados reais de empresas, clientes e notas. Senhas de certificado nao podem aparecer em logs, respostas de API ou banco em texto puro.
