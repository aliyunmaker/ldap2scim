# 环境准备

### 数据库
database: dreamweb
create sql: src/main/resources/mysql/database_dreamweb.sql

### 单点登录证书生成

```shell
#create the keypair
openssl req -new -x509 -days 3652 -nodes -out saml.crt -keyout saml.pem

#convert the private key to pkcs8 format
openssl pkcs8 -topk8 -inform PEM -outform DER -in saml.pem -out saml.pkcs8 -nocrypt
```

将生成的saml.crt saml.pem  saml.pkcs8 三个文件放在src/main/resources/ssocert/ 目录下

### 启动类(springboot)
cc.landingzone.dreamweb.DreamwebApplication

### 文档
[通过token实现自动登录](docs/auto_login_by_token.md)

### 配置更新
2021.08.02：新增配置项dreamweb.idp_entityid=<your_idp_entityid>