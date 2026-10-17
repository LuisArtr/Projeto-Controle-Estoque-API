# Controle de Estoque para Padaria

Estou construindo uma API para controlar o estoque de uma padaria. A ideia é resolver
problemas que padaria tem de verdade: ingrediente que vence, receita que gasta vários
insumos de uma vez e desperdício que ninguém registra.

Este é um projeto de estudo, feito para aprender e praticar back-end com Java.
Ainda está em construção, e o que já funciona está marcado abaixo.

## O que a API faz (ou vai fazer)

- [x] Cadastrar e consultar insumos (farinha, açúcar, fermento...)
- [x] Cadastrar fornecedores
- [x] Registrar a entrada de mercadoria, com validade e custo
- [x] Cadastrar produtos e suas receitas
- [x] Dar baixa nos insumos automaticamente quando algo é produzido
- [x] Registrar perdas e avisar quando um lote está perto de vencer
- [ ] Login e permissões por tipo de usuário

## Tecnologias

Java 21, Spring Boot, SQL Server, Flyway, Docker.

## Como rodar

Você precisa ter Java 21, Docker Desktop e Git instalados.

```bash
git clone https://github.com/LuisArtr/Projeto-Controle-Estoque-API.git
cd Projeto-Controle-Estoque-API

docker compose up -d
```

Espere uns 30 segundos e crie o banco (só na primeira vez):

```bash
docker exec -it bakery-db /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "Bakery@12345" -C -Q "CREATE DATABASE bakery"
```

Depois, rode a aplicação:

```bash
./mvnw spring-boot:run
```

No Windows, use `.\mvnw.cmd spring-boot:run`. A API fica em `http://localhost:8080`.

## Exemplo de uso

```bash
curl -X POST http://localhost:8080/ingredients \
  -H "Content-Type: application/json" \
  -d '{"name":"Farinha de trigo","baseUnit":"g","minimumStock":5000}'
```

## Sobre

Feito por Luis Artur. Estou começando na área e usando este projeto para aprender
na prática. Sugestões para o Aprendizado são bem-vindas!   