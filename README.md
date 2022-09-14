# 配置文件

```properties
scim_key = scim key
scim_url = scim server url
ldap_url = ldap://127.0.0.1:389
ldap_username = username
ldap_password = password
ldap_searchbase = ou=hangzhou,dc=landingzone,dc=cc
ldap_searchfilter = (objectClass=user)

# 这里是ldap字段和scim字段的对应关系
scim_attr_givenname = givenName
scim_attr_familyname = sn
scim_attr_email = userPrincipalName
scim_attr_externalid = distinguishedName
scim_attr_displayname = sAMAccountName
scim_attr_username = sAMAccountName
```

#### 读取顺序
- 运行时参数,示例: java -jar -DconfigPath=/home/test/ldap2cloudsso.properties ldap2cloudsso.jar  
- ldap2cloudsso.jar同目录下的ldap2cloudsso.properties
- 用户home/config/目录下的ldap2cloudsso.properties

#### scim_key&scim_url

![screenshot1](image/cloudsso_key.png)



# 启动(springboot)
代码启动: main class: `ldap2cloudsso.Ldap2cloudssoApplication`

jar包启动: java -jar ldap2cloudsso.jar





# 打包

`mvn clean package -Dmaven.test.skip=true`



# LDAP搜索

![screenshot1](image/screenshot1.png)

LDAP的搜索语法可以[参考这里](https://www.cnblogs.com/dreamer-fish/p/5832735.html)