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

insert into usuarios (login_name, name, certificate, password, gid, salt) values ("user01@inf1416.puc-rio.br", "Usuario 01", "-----BEGIN CERTIFICATE-----
MIID9DCCAtygAwIBAgIBBzANBgkqhkiG9w0BAQsFADCBhDELMAkGA1UEBhMCQlIx
CzAJBgNVBAgMAlJKMQwwCgYDVQQHDANSaW8xDDAKBgNVBAoMA1BVQzEQMA4GA1UE
CwwHSU5GMTQxNjETMBEGA1UEAwwKQUMgSU5GMTQxNjElMCMGCSqGSIb3DQEJARYW
Y2FAZ3JhZC5pbmYucHVjLXJpby5icjAeFw0yMjA0MjkxNDAyNDlaFw0yNTA0Mjgx
NDAyNDlaMHkxCzAJBgNVBAYTAkJSMQswCQYDVQQIDAJSSjEMMAoGA1UECgwDUFVD
MRAwDgYDVQQLDAdJTkYxNDE2MRMwEQYDVQQDDApVc3VhcmlvIDAxMSgwJgYJKoZI
hvcNAQkBFhl1c2VyMDFAaW5mMTQxNi5wdWMtcmlvLmJyMIIBIjANBgkqhkiG9w0B
AQEFAAOCAQ8AMIIBCgKCAQEAvzUNGfNciZ6QYwqcHcy7oYe5C6QeLGPeb1gxHeN/
Z+JeWlf5/N21XQeXaWg8Ii/X610IjJ4NYb87/6gA6OWitR/h/DgOAQbqZAL9q7ln
Nt2xqF4PBbW3W4vm+IrgXnTYMtUnYZdgvPICT12cmsVKvP+7LIp8aK+GcxSyu0zI
fhhSrMfJh3SmSWSDod1+no3oxv/iGb4Zs92wB92U3LHtf/XJrOoTwZtvof0WtJhU
FbtY/6EG//+GvHFyzln6WHBcRv++/6QQLWlajyE+/3035eUBVNiGVeevLi7vT0ZP
np4U+TqpRlHWVfu02WrKc4FHV+skblwrwm370/rjsbC2EwIDAQABo3sweTAJBgNV
HRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZp
Y2F0ZTAdBgNVHQ4EFgQU5iOOnQ8g9xGbDhTiNx/P5q9axdcwHwYDVR0jBBgwFoAU
I4E7wxkrlZnZyuo7a/4cJHPCgo4wDQYJKoZIhvcNAQELBQADggEBADs/OUs4kOU+
g8PXKL1EdoyC95/+s1dqnjRugs7FwqJKqXaL/Lnay2qmrS4dpj+26HiWVpyjugb2
Ave+y2zkVGFKJnepG5vZnv4k4W6S09f6SHCfdyXRfn9PA2om++aBv9WOsR2L06au
0uxBSZKlAKxraISSKEg1iYDmxfWmAV0MIweMu227ucjPNyoU9BKw7+Kqbsl27rkx
c42YKRLtVcJ4pN54OLfMDuDnzy4dVwC3LnYwlddggPUAJNsxLFspJ4p6z6pehOFR
vIIxafaopt4K2FeWuNdnY81Yww12iEjKnqrIZZX7FJZAeSnd0ZDezveyF0drmkBb
JnGWzx4dSy4=
-----END CERTIFICATE-----", "6bdaef0f3b548c85a3946bdd007b7fbb1d0a1f65", 2, "bj1Zi7kWcL");

insert into mensagens (code, message) values(1001 ,"Sistema iniciado.");
insert into mensagens (code, message) values(1002 ,"Sistema encerrado.");
insert into mensagens (code, message) values(2001 ,"Autentica????o etapa 1 iniciada.");
insert into mensagens (code, message) values(2002 ,"Autentica????o etapa 1 encerrada.");
insert into mensagens (code, message) values(2003 ,"Login name <login_name> identificado com acesso liberado.");
insert into mensagens (code, message) values(2004 ,"Login name <login_name> identificado com acesso bloqueado.");
insert into mensagens (code, message) values(2005 ,"Login name <login_name> n??o identificado.");
insert into mensagens (code, message) values(3001 ,"Autentica????o etapa 2 iniciada para <login_name>.");
insert into mensagens (code, message) values(3002 ,"Autentica????o etapa 2 encerrada para <login_name>.");
insert into mensagens (code, message) values(3003 ,"Senha pessoal verificada positivamente para <login_name>.");
insert into mensagens (code, message) values(3004 ,"Primeiro erro da senha pessoal contabilizado para <login_name>.");
insert into mensagens (code, message) values(3005 ,"Segundo erro da senha pessoal contabilizado para <login_name>.");
insert into mensagens (code, message) values(3006 ,"Terceiro erro da senha pessoal contabilizado para <login_name>.");
insert into mensagens (code, message) values(3007 ,"Acesso do usuario <login_name> bloqueado pela autentica????o etapa 2.");
insert into mensagens (code, message) values(4001 ,"Autentica????o etapa 3 iniciada para <login_name>.");
insert into mensagens (code, message) values(4002 ,"Autentica????o etapa 3 encerrada para <login_name>.");
insert into mensagens (code, message) values(4003 ,"Chave privada verificada positivamente para <login_name>.");
insert into mensagens (code, message) values(4004 ,"Chave privada verificada negativamente para <login_name> (caminho inv??lido).");
insert into mensagens (code, message) values(4005 ,"Chave privada verificada negativamente para <login_name> (frase secreta inv??lida).");
insert into mensagens (code, message) values(4006 ,"Chave privada verificada negativamente para <login_name> (assinatura digital inv??lida).");
insert into mensagens (code, message) values(4007 ,"Acesso do usuario <login_name> bloqueado pela autentica????o etapa 3.");
insert into mensagens (code, message) values(5001 ,"Tela principal apresentada para <login_name>.");
insert into mensagens (code, message) values(5002 ,"Op????o 1 do menu principal selecionada por <login_name>.");
insert into mensagens (code, message) values(5003 ,"Op????o 2 do menu principal selecionada por <login_name>.");
insert into mensagens (code, message) values(5004 ,"Op????o 3 do menu principal selecionada por <login_name>.");
insert into mensagens (code, message) values(5005 ,"Op????o 4 do menu principal selecionada por <login_name>.");
insert into mensagens (code, message) values(6001 ,"Tela de cadastro apresentada para <login_name>.");
insert into mensagens (code, message) values(6002 ,"Bot??o cadastrar pressionado por <login_name>.");
insert into mensagens (code, message) values(6003 ,"Senha pessoal inv??lida fornecida por <login_name>.");
insert into mensagens (code, message) values(6004 ,"Caminho do certificado digital inv??lido fornecido por <login_name>.");
insert into mensagens (code, message) values(6005 ,"Confirma????o de dados aceita por <login_name>.");
insert into mensagens (code, message) values(6006 ,"Confirma????o de dados rejeitada por <login_name>.");
insert into mensagens (code, message) values(6007 ,"Bot??o voltar de cadastro para o menu principal pressionado por <login_name>.");
insert into mensagens (code, message) values(7001 ,"Tela de altera????o da senha pessoal e certificado apresentada para <login_name>.");
insert into mensagens (code, message) values(7002 ,"Senha pessoal inv??lida fornecida por <login_name>.");
insert into mensagens (code, message) values(7003 ,"Caminho do certificado digital inv??lido fornecido por <login_name>.");
insert into mensagens (code, message) values(7004 ,"Confirma????o de dados aceita por <login_name>.");
insert into mensagens (code, message) values(7005 ,"Confirma????o de dados rejeitada por <login_name>.");
insert into mensagens (code, message) values(7006 ,"Bot??o voltar de carregamento para o menu principal pressionado por <login_name>.");
insert into mensagens (code, message) values(8001 ,"Tela de consulta de arquivos secretos apresentada para <login_name>.");
insert into mensagens (code, message) values(8002 ,"Bot??o voltar de consulta para o menu principal pressionado por <login_name>.");
insert into mensagens (code, message) values(8003 ,"Bot??o Listar de consulta pressionado por <login_name>.");
insert into mensagens (code, message) values(8004 ,"Caminho de pasta inv??lido fornecido por <login_name>.");
insert into mensagens (code, message) values(8005 ,"Arquivo de ??ndice decriptado com sucesso para <login_name>.");
insert into mensagens (code, message) values(8006 ,"Arquivo de ??ndice verificado (integridade e autenticidade) com sucesso para <login_name>.");
insert into mensagens (code, message) values(8007 ,"Falha na decripta????o do arquivo de ??ndice para <login_name>.");
insert into mensagens (code, message) values(8008 ,"Falha na verifica????o (integridade e autenticidade) do arquivo de ??ndice para <login_name>.");
insert into mensagens (code, message) values(8009 ,"Lista de arquivos presentes no ??ndice apresentada para <login_name>.");
insert into mensagens (code, message) values(8010 ,"Arquivo <arq_name> selecionado por <login_name> para decripta????o.");
insert into mensagens (code, message) values(8011 ,"Acesso permitido ao arquivo <arq_name> para <login_name>.");
insert into mensagens (code, message) values(8012 ,"Acesso negado ao arquivo <arq_name> para <login_name>.");
insert into mensagens (code, message) values(8013 ,"Arquivo <arq_name> decriptado com sucesso para <login_name>.");
insert into mensagens (code, message) values(8014 ,"Arquivo <arq_name> verificado (integridade e autenticidade) com sucesso para <login_name>.");
insert into mensagens (code, message) values(8015 ,"Falha na decripta????o do arquivo <arq_name> para <login_name>.");
insert into mensagens (code, message) values(8016 ,"Falha na verifica????o (integridade e autenticidade) do arquivo <arq_name> para <login_name>.");
insert into mensagens (code, message) values(9001 ,"Tela de sa??da apresentada para <login_name>.");
insert into mensagens (code, message) values(9002 ,"Sa??da n??o liberada por falta de one-time password para <login_name>.");
insert into mensagens (code, message) values(9003 ,"Bot??o sair pressionado por <login_name>.");
insert into mensagens (code, message) values(9004 ,"Bot??o voltar de sair para o menu principal pressionado por <login_name>.");