# 简介
这是一个能独立部署的(LDAP->SCIM)同步工具  
例如: 把本地的Microsoft Active Directory(AD) 中的用户信息同步到阿里云的CloudSSO

## 优势
- 可本地独立部署，没有任何其他依赖
- 可视化查询&配置界面
- 支持用户，用户组的同步
- 支持灵活的定时任务配置
- 详细的日志输出，便于排查问题

## 应用场景
* 定时同步LDAP用户到SCIM服务端，支持对等同步，LDAP中用户删除，服务端也自动删除
* 支持特定场景下手动同步部分用户（⚠️注意和上述定时任务同时使用时需要配置好同步范围和同步条件，避免云上用户被误删）

## 名词解释
* 对等同步：保持 LDAP 用户和 SCIM 服务端用户在数量和属性上完全一致。即，如果 LDAP 搜索条件搜索出用户 A 和用户 B，此时 SCIM 服务端有用户 C，那么对等同步完成后，SCIM 服务端只会有用户 A 和用户 B 两个账号，用户 C 的账号会被删除。

# 配置文件

## 示例配置
```properties
# SCIM 连接信息
scim_key = scim key
scim_url = scim server url

# LDAP 连接信息
ldap_url = ldap://127.0.0.1:389
ldap_username = username
ldap_password = password

# LDAP 搜索条件，后续配置定时任务后，该搜索条件下的用户才会被同步到 SCIM 服务端
ldap_searchbase = ou=hangzhou,dc=landingzone,dc=cc
ldap_searchfilter = (objectClass=user)

# LDAP 和 SCIM 字段的对应关系
scim_attr_givenname = givenName
scim_attr_familyname = sn
scim_attr_email = userPrincipalName
# ️该字段用于唯一标识一个用户，一旦设定，不要修改
scim_attr_externalid = distinguishedName
scim_attr_displayname = sAMAccountName
scim_attr_username = sAMAccountName

# 是否开启定时同步
scim_sync_cron_enabled = false
scim_sync_cron_expression = 0 0 6 * * ?\
# 同步的时候是否删除LDAP中不存在的用户
scim_sync_cron_remove_not_exist = false
```
## 配置项说明

以下针对一些重要或者风险的配置项进行说明：
* `scim_key` 和 `scim_url`：对于 CloudSSO 用户，可以参考该文档生成[同步密钥](https://help.aliyun.com/document_detail/264937.html)。生成成功后，可以参考该截图获取对应 URL：
  ![screenshot1](image/cloudsso_key.png)
* `scim_attr_externalid`：️⚠️ 该字段用于唯一标识一个用户，通过该 ID 将 LDAP 用户和 SCIM 服务端用户进行关联。一旦设定，不要修改，否则会造成两端用户不匹配，进而可能导致 SCIM 服务端中不匹配用户被删除。
* `scim_sync_cron_remove_not_exist`：⚠️ ️同步的时候是否删除 LDAP 中不存在的用户，开启该选项后，仅会保留 LDAP 搜索条件（`ldap_searchbase` 和 `ldap_searchfilter`）查出来的用户，也就是说，开启该选项后，如果之前手工将某个 LDAP 用户账号同步到 SCIM 端，但该用户账号不在 LDAP 搜索条件中，那么下次定时任务触发时，该用户账号会在 SCIM 端被删除。
* `scim_sync_cron_expression`：cron 表达式示例可以参考该[文档](https://help.aliyun.com/document_detail/64769.html)。 

## 配置文件读取顺序

1. 运行时参数，示例: java -jar -DconfigPath=/home/test/ldap2scim.properties ldap2scim.jar  
2. ldap2scim.jar同目录下的ldap2scim.properties
3. 用户home/config/目录下的ldap2scim.properties


# 部署
基于springboot搭建

打包: `mvn clean package -Dmaven.test.skip=true`

代码启动: main class: `ldap2scim.Ldap2scimApplication`

jar包启动: `java -jar ldap2scim.jar`


# 界面使用说明

## SCIM查询

![SCIM查询界面](image/screenshot_1.png)

## LDAP 查询界面

![LDAP 查询界面](image/screenshot_3.png)

在该界面中，可以进行手动同步，或者测试 LDAP 的搜索条件是否正确。
LDAP 的搜索语法可以[参考这里](https://www.cnblogs.com/dreamer-fish/p/5832735.html)
高级选项：
* `在SCIM服务端同步删除未选择用户`：该选项表示是否启用对等同步

对于手动同步，支持两种同步模式，说明如下：

### 操作说明

#### 选择并同步到SCIM

如果只希望同步该选择条件下的部分用户，可以手工选择需要同步的用户/组账号，单击`选择并同步到SCIM`，此时根据`高级选项`中的设置，会有不同的行为。我们假设此时 SCIM 服务端有用户 C，本地 LDAP 中有用户 A 和用户 B：
* 勾选`在SCIM服务端同步删除未选择用户`：同步选择的用户账号到 SCIM 服务端，并将 SCIM 服务端中不在选择范围的用户账号删除。如果勾选了用户 A，那么同步完成后，SCIM 服务端中用户 C 被删除，并新建了用户 A 账号。
* 不勾选`在SCIM服务端同步删除未选择用户`：同步选择的用户账号到 SCIM 服务端，SCIM 服务端中多余的账号依旧存在。如果勾选了用户 A，那么同步完成后，SCIM 服务端有用户 A 和用户 C。

#### 全部同步到SCIM

将该搜索条件下的所有用户同步到 SCIM 服务端。该选项等同于手工选择了该搜索条件下的所有用户并点击`选择并同步到SCIM`。相关高级选项和上述章节保持一致。需要特别注意定时任务同步的情况。

#### 注意事项

⚠️ 需要特别注意的是，如果启用了定时任务，并且`scim_sync_cron_remove_not_exist=true`，那么下次定时任务触发时，不在 LDAP 搜索条件中的用户会被删除！

## 任务查看界面

手动同步&自动同步的任务都会展示在这里，双击可以看详情

![screenshot1](image/screenshot_2.png)
