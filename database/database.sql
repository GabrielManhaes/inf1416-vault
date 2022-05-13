CREATE DATABASE t4;

USE t4;

create table mensagens(
    message varchar(255),
    code int not null unique,
    
    primary key (code)
);

create table registros(
	id int not null auto_increment,
    timestamp datetime not null DEFAULT CURRENT_TIMESTAMP(),
    code int,
	login_name varchar(255),
    file_name varchar(255),
    
    primary key (id),
    foreign key (code) REFERENCES mensagens(code)
);

create table grupos(
	id int not null,
    name varchar(100),
    
    primary key (id)
);

create table usuarios (
	id int not null auto_increment,
    login_name varchar(255) unique not null,
    name varchar(255) not null,
	certificate varchar(4000),
    password varchar(255) not null,
	salt varchar(10) not null,
    gid int not null,
    block datetime not null DEFAULT CURRENT_TIMESTAMP(),
    
    primary key (id),
    foreign key (gid) REFERENCES grupos(id)
);

insert into grupos (name, id) values ("administrador", "1");

insert into grupos (name, id) values ("usuario", "2");

insert into usuarios (login_name, name, certificate, password, gid, salt) values ("admin@inf1416.puc-rio.br", "Administrador", "-----BEGIN CERTIFICATE-----
MIID9jCCAt6gAwIBAgIBBjANBgkqhkiG9w0BAQsFADCBhDELMAkGA1UEBhMCQlIx
CzAJBgNVBAgMAlJKMQwwCgYDVQQHDANSaW8xDDAKBgNVBAoMA1BVQzEQMA4GA1UE
CwwHSU5GMTQxNjETMBEGA1UEAwwKQUMgSU5GMTQxNjElMCMGCSqGSIb3DQEJARYW
Y2FAZ3JhZC5pbmYucHVjLXJpby5icjAeFw0yMjA0MjkxNDAxMzBaFw0yNTA0Mjgx
NDAxMzBaMHsxCzAJBgNVBAYTAkJSMQswCQYDVQQIDAJSSjEMMAoGA1UECgwDUFVD
MRAwDgYDVQQLDAdJTkYxNDE2MRYwFAYDVQQDDA1BZG1pbmlzdHJhdG9yMScwJQYJ
KoZIhvcNAQkBFhhhZG1pbkBpbmYxNDE2LnB1Yy1yaW8uYnIwggEiMA0GCSqGSIb3
DQEBAQUAA4IBDwAwggEKAoIBAQDDnq2WpTioReNQ3EapxCdmUt9khsS2BHf/YB7t
jGILCzQegnV1swvcH+xfd9FUjR7pORFSNvrfWKt93t3l2Dc0kCvVffh5BSnXIwwb
W94O+E1Yp6pvpyflj8YI+VLy0dNCiszHAF5ux6lRZYcrM4KiJndqeFRnqRP8zWI5
O1kJJMXzCqIXwmXtfqVjWiwXTnjU97xfQqKkmAt8Z+uxJaQxdZJBczmo/jQAIz1g
x+SXA4TshU5Ra4sQYLo5+FgAfA2vswHGXA6ba3N52wydZ2IYUJL2/YmTyfxzRnsy
uqbL+hcOw6bm+g0OEIIC7JduKpinz3BieiO15vameAJlqpedAgMBAAGjezB5MAkG
A1UdEwQCMAAwLAYJYIZIAYb4QgENBB8WHU9wZW5TU0wgR2VuZXJhdGVkIENlcnRp
ZmljYXRlMB0GA1UdDgQWBBSeUNmquC0OBxDLGpUaDNxe1t2EADAfBgNVHSMEGDAW
gBQjgTvDGSuVmdnK6jtr/hwkc8KCjjANBgkqhkiG9w0BAQsFAAOCAQEAeF1GIuFA
XbRsfuSYaeIOEIBRS88qhXJOTi/lFE4nb4uBni4/Ec9KPZ9jrMyvIhoOl/H07tkw
umNmuA3E+jY3kut+LCbd5JjNtrcKBz0PdlqIy/cfY6tmsXAvP4b/gR5B1vVLSGaJ
7MG6lIkzURiWXsqGG815dADvMCWR/x+mPg5+tw4SBLwGwcwyJvF7TL2lxdp2hXW9
TBEZ0kn0EyhO3Xg527FKUXGJpQyw8czDFxSHrZFeDm+uLjG77PvtXRi1YmKPqCjd
hsCYj0uCajrjnZrSZp6qCEU5F6avQC7dPnguWL5HK8LgIXbFxr6ZJlbf/Q7gykQD
tPewQWWhMFFTug==
-----END CERTIFICATE-----", "e02a411c89f4b3bbe87dd32e3eba6c03f44fee68", 1, "fp5wE2X9we");

insert into mensagens (code, message) values(1001 ,"Sistema iniciado.");
insert into mensagens (code, message) values(1002 ,"Sistema encerrado.");
insert into mensagens (code, message) values(2001 ,"Autenticação etapa 1 iniciada.");
insert into mensagens (code, message) values(2002 ,"Autenticação etapa 1 encerrada.");
insert into mensagens (code, message) values(2003 ,"Login name <login_name> identificado com acesso liberado.");
insert into mensagens (code, message) values(2004 ,"Login name <login_name> identificado com acesso bloqueado.");
insert into mensagens (code, message) values(2005 ,"Login name <login_name> não identificado.");
insert into mensagens (code, message) values(3001 ,"Autenticação etapa 2 iniciada para <login_name>.");
insert into mensagens (code, message) values(3002 ,"Autenticação etapa 2 encerrada para <login_name>.");
insert into mensagens (code, message) values(3003 ,"Senha pessoal verificada positivamente para <login_name>.");
insert into mensagens (code, message) values(3004 ,"Primeiro erro da senha pessoal contabilizado para <login_name>.");
insert into mensagens (code, message) values(3005 ,"Segundo erro da senha pessoal contabilizado para <login_name>.");
insert into mensagens (code, message) values(3006 ,"Terceiro erro da senha pessoal contabilizado para <login_name>.");
insert into mensagens (code, message) values(3007 ,"Acesso do usuario <login_name> bloqueado pela autenticação etapa 2.");
insert into mensagens (code, message) values(4001 ,"Autenticação etapa 3 iniciada para <login_name>.");
insert into mensagens (code, message) values(4002 ,"Autenticação etapa 3 encerrada para <login_name>.");
insert into mensagens (code, message) values(4003 ,"Chave privada verificada positivamente para <login_name>.");
insert into mensagens (code, message) values(4004 ,"Chave privada verificada negativamente para <login_name> (caminho inválido).");
insert into mensagens (code, message) values(4005 ,"Chave privada verificada negativamente para <login_name> (frase secreta inválida).");
insert into mensagens (code, message) values(4006 ,"Chave privada verificada negativamente para <login_name> (assinatura digital inválida).");
insert into mensagens (code, message) values(4007 ,"Acesso do usuario <login_name> bloqueado pela autenticação etapa 3.");
insert into mensagens (code, message) values(5001 ,"Tela principal apresentada para <login_name>.");
insert into mensagens (code, message) values(5002 ,"Opção 1 do menu principal selecionada por <login_name>.");
insert into mensagens (code, message) values(5003 ,"Opção 2 do menu principal selecionada por <login_name>.");
insert into mensagens (code, message) values(5004 ,"Opção 3 do menu principal selecionada por <login_name>.");
insert into mensagens (code, message) values(5005 ,"Opção 4 do menu principal selecionada por <login_name>.");
insert into mensagens (code, message) values(6001 ,"Tela de cadastro apresentada para <login_name>.");
insert into mensagens (code, message) values(6002 ,"Botão cadastrar pressionado por <login_name>.");
insert into mensagens (code, message) values(6003 ,"Senha pessoal inválida fornecida por <login_name>.");
insert into mensagens (code, message) values(6004 ,"Caminho do certificado digital inválido fornecido por <login_name>.");
insert into mensagens (code, message) values(6005 ,"Confirmação de dados aceita por <login_name>.");
insert into mensagens (code, message) values(6006 ,"Confirmação de dados rejeitada por <login_name>.");
insert into mensagens (code, message) values(6007 ,"Botão voltar de cadastro para o menu principal pressionado por <login_name>.");
insert into mensagens (code, message) values(7001 ,"Tela de alteração da senha pessoal e certificado apresentada para <login_name>.");
insert into mensagens (code, message) values(7002 ,"Senha pessoal inválida fornecida por <login_name>.");
insert into mensagens (code, message) values(7003 ,"Caminho do certificado digital inválido fornecido por <login_name>.");
insert into mensagens (code, message) values(7004 ,"Confirmação de dados aceita por <login_name>.");
insert into mensagens (code, message) values(7005 ,"Confirmação de dados rejeitada por <login_name>.");
insert into mensagens (code, message) values(7006 ,"Botão voltar de carregamento para o menu principal pressionado por <login_name>.");
insert into mensagens (code, message) values(8001 ,"Tela de consulta de arquivos secretos apresentada para <login_name>.");
insert into mensagens (code, message) values(8002 ,"Botão voltar de consulta para o menu principal pressionado por <login_name>.");
insert into mensagens (code, message) values(8003 ,"Botão Listar de consulta pressionado por <login_name>.");
insert into mensagens (code, message) values(8004 ,"Caminho de pasta inválido fornecido por <login_name>.");
insert into mensagens (code, message) values(8005 ,"Arquivo de índice decriptado com sucesso para <login_name>.");
insert into mensagens (code, message) values(8006 ,"Arquivo de índice verificado (integridade e autenticidade) com sucesso para <login_name>.");
insert into mensagens (code, message) values(8007 ,"Falha na decriptação do arquivo de índice para <login_name>.");
insert into mensagens (code, message) values(8008 ,"Falha na verificação (integridade e autenticidade) do arquivo de índice para <login_name>.");
insert into mensagens (code, message) values(8009 ,"Lista de arquivos presentes no índice apresentada para <login_name>.");
insert into mensagens (code, message) values(8010 ,"Arquivo <arq_name> selecionado por <login_name> para decriptação.");
insert into mensagens (code, message) values(8011 ,"Acesso permitido ao arquivo <arq_name> para <login_name>.");
insert into mensagens (code, message) values(8012 ,"Acesso negado ao arquivo <arq_name> para <login_name>.");
insert into mensagens (code, message) values(8013 ,"Arquivo <arq_name> decriptado com sucesso para <login_name>.");
insert into mensagens (code, message) values(8014 ,"Arquivo <arq_name> verificado (integridade e autenticidade) com sucesso para <login_name>.");
insert into mensagens (code, message) values(8015 ,"Falha na decriptação do arquivo <arq_name> para <login_name>.");
insert into mensagens (code, message) values(8016 ,"Falha na verificação (integridade e autenticidade) do arquivo <arq_name> para <login_name>.");
insert into mensagens (code, message) values(9001 ,"Tela de saída apresentada para <login_name>.");
insert into mensagens (code, message) values(9002 ,"Saída não liberada por falta de one-time password para <login_name>.");
insert into mensagens (code, message) values(9003 ,"Botão sair pressionado por <login_name>.");
insert into mensagens (code, message) values(9004 ,"Botão voltar de sair para o menu principal pressionado por <login_name>.");