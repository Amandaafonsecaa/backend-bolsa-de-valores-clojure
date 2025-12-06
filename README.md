# Backend - Bolsa de Valores

Backend desenvolvido em Clojure para gerenciamento de transações de ações na bolsa de valores. O sistema permite comprar e vender ações, consultar cotações, calcular saldos e patrimônio, além de gerar extratos de transações.

## 🚀 Tecnologias

- **Clojure** - Linguagem de programação funcional
- **Ring** - Framework web para Clojure
- **Compojure** - Roteamento HTTP
- **Jetty** - Servidor HTTP embutido
- **clj-http** - Cliente HTTP para integração com APIs externas
- **Brapi API** - API de cotações da bolsa de valores brasileira

## 📋 Funcionalidades

- ✅ Compra de ações com preço histórico ou atual
- ✅ Venda de ações com validação de saldo
- ✅ Consulta de cotações em tempo real
- ✅ Consulta de cotações históricas por data
- ✅ Extrato de transações com filtro por período
- ✅ Saldo por ativo
- ✅ Valor total investido
- ✅ Patrimônio líquido (valor de mercado atual)

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas:

```
HTTP Request
    ↓
[Core] → Middlewares (CORS, JSON)
    ↓
[Routes] → Definição de endpoints
    ↓
[Controllers] → Validação HTTP, tratamento de erros
    ↓
[Services] → Lógica de negócio
    ↓
[Repositories] → Persistência (em memória)
[External] → Integração com APIs externas
```

### Estrutura de Diretórios

```
src/bolsa_de_valores/
├── core.clj                    # Ponto de entrada e configuração do servidor
├── config.clj                  # Configurações (URLs, variáveis de ambiente)
├── routes.clj                  # Definição de rotas HTTP
├── controllers/
│   └── transacao_controller.clj # Handlers HTTP
├── services/
│   ├── transacao_service.clj   # Lógica de compra/venda
│   ├── carteira_service.clj     # Cálculos de carteira
│   └── cotacao_service.clj     # Abstração de cotações
├── repositories/
│   └── transacao_repository.clj # Persistência em memória
└── external/
    └── brapi_external.clj       # Integração com API Brapi
```

## 🔧 Pré-requisitos

- [Leiningen](https://leiningen.org/) 2.x ou superior
- Java 8 ou superior

## 📦 Instalação

1. Clone o repositório:
```bash
git clone https://github.com/Amandaafonsecaa/backend-bolsa-de-valores-clojure.git
cd backend-bolsa-de-valores-clojure
```

2. Instale as dependências:
```bash
lein deps
```

## 🚀 Como Executar

Para iniciar o servidor:

```bash
lein run
```

O servidor estará disponível em `http://localhost:3000`

## 📡 Endpoints da API

### Transações

#### POST `/transacoes/compra`
Registra uma compra de ações.

**Body:**
```json
{
  "ticker": "PETR4",
  "quantidade": 10,
  "data": "2025-01-15T10:00:00"
}
```

**Resposta (201):**
```json
{
  "mensagem": "Compra registrada com sucesso.",
  "transacao": {
    "ticker": "PETR4",
    "tipo": "compra",
    "quantidade": 10,
    "preco": 25.50,
    "total": 255.00,
    "data": "2025-01-15T10:00:00"
  }
}
```

#### POST `/transacoes/venda`
Registra uma venda de ações.

**Body:**
```json
{
  "ticker": "PETR4",
  "quantidade": 5,
  "data": "2025-01-15T14:00:00"
}
```

**Resposta (201):**
```json
{
  "mensagem": "Venda registrada com sucesso.",
  "transacao": {
    "ticker": "PETR4",
    "tipo": "venda",
    "quantidade": 5,
    "preco": 26.00,
    "total": 130.00,
    "data": "2025-01-15T14:00:00"
  }
}
```

### Carteira

#### GET `/carteira/extrato`
Lista todas as transações. Aceita filtro opcional por período.

**Query Params (opcionais):**
- `data_inicio`: Data inicial do período (formato: `YYYY-MM-DDTHH:MM:SS`)
- `data_fim`: Data final do período (formato: `YYYY-MM-DDTHH:MM:SS`)

**Exemplo:**
```
GET /carteira/extrato?data_inicio=2025-01-01T00:00:00&data_fim=2025-01-31T23:59:59
```

#### GET `/carteira/saldo`
Retorna o saldo (quantidade) de cada ativo na carteira.

**Resposta:**
```json
{
  "PETR4": 10,
  "VALE3": 5
}
```

#### GET `/carteira/investido`
Retorna o valor total investido (soma de todas as compras).

**Resposta:**
```json
{
  "valor_total_investido": 1250.50
}
```

#### GET `/carteira/patrimonio`
Retorna o patrimônio líquido (valor de mercado atual de todos os ativos).

**Resposta:**
```json
{
  "patrimonio_liquido": 1350.75
}
```

### Cotações

#### GET `/cotacao/:ticker`
Consulta detalhes de uma ação.

**Exemplo:**
```
GET /cotacao/PETR4
```

**Resposta:**
```json
{
  "nome": "Petróleo Brasileiro S.A. - Petrobras",
  "nome-curto": "Petrobras PN",
  "moeda": "BRL",
  "ultimo-preco": 25.50,
  "preco-maximo": 26.00,
  "preco-minimo": 25.00,
  "preco-abertura": 25.25,
  "preco-fechamento": 25.30,
  "hora-cotacao": 1640000000
}
```

## 🎯 Características Funcionais

O código segue princípios de programação funcional:

- ✅ **Funções puras**: `filtrar-por-periodo`, `soma-saldo`, `formatar-data`
- ✅ **Funções de alta ordem**: `filter`, `map`, `reduce`, `group-by`
- ✅ **Imutabilidade**: Estruturas de dados imutáveis
- ✅ **Composição**: Uso de `->` (thread-first) e `->>` (thread-last)
- ✅ **Sem loops imperativos**: Não utiliza `loop`, `while`, `for`, `doseq` ou `dotimes`

## 🔄 Fluxo de uma Compra

1. Frontend envia `POST /transacoes/compra` com `ticker`, `quantidade` e `data`
2. Controller valida os parâmetros
3. Service busca o preço (histórico se houver data, atual caso contrário)
4. Service calcula o total e cria a transação
5. Repository persiste a transação em memória
6. Controller retorna HTTP 201 com a transação criada

## 📝 Notas Importantes

- **Persistência**: Os dados são armazenados em memória usando `atom`. Ao reiniciar o servidor, os dados são perdidos.
- **Preço Histórico**: Se uma data for fornecida, o sistema tenta buscar o preço histórico daquela data. Caso contrário, usa o preço atual.
- **Validação de Saldo**: A venda só é permitida se houver saldo suficiente do ativo na data informada.

## 👥 Desenvolvedores

<div align="center">

<table>
<tr>
<td align="center">
<a href="https://github.com/Amandaafonsecaa">
<img src="https://github.com/Amandaafonsecaa.png?size=100" width="100px;" alt="Amanda Fonsêca" style="border-radius: 50%;"/>
<br />
<sub><b>Amanda Fonsêca</b></sub>
</a>
<br />
<a href="https://github.com/Amandaafonsecaa">GitHub</a> • 
<a href="https://www.linkedin.com/in/amanda-fonseca-b4189426b">LinkedIn</a>
</td>
<td align="center">
<a href="https://github.com/lumab23">
<img src="https://github.com/lumab23.png?size=100" width="100px;" alt="Luma Brandão" style="border-radius: 50%;"/>
<br />
<sub><b>Luma Brandão</b></sub>
</a>
<br />
<a href="https://github.com/lumab23">GitHub</a> • 
<a href="https://www.linkedin.com/in/lbca23">LinkedIn</a>
</td>
</tr>
</table>

</div>



