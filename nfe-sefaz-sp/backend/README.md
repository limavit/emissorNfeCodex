# Backend

API Spring Boot para autenticação, multiempresa, cadastros fiscais, NF-e, armazenamento fiscal e integração SEFAZ.

## Estrutura Java

Pacote raiz:

```text
br.com.nfesefassp
```

Camadas principais:

```text
controller   Controllers REST
model        Entidades, enums e requests simples
repository   Spring Data repositories
service      Regras de negocio, SEFAZ, certificado, storage e calculos
util         Validadores e utilitarios
exceptions   Tratamento global de excecoes
security     JWT, filtros e configuracao de seguranca
config       Beans e configuracoes tecnicas
```

## Rodar localmente

```bash
mvn spring-boot:run
```

Use PostgreSQL e as variáveis do `.env.example`. O Flyway cria as tabelas na subida.
