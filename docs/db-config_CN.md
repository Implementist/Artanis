## 设置你的数据库配置
**你可以通过以下的步骤用自己的配置替换默认的数据库配置**
你只需要关心`/src/main/resources/application.yml`文件中的几行:

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: ENC(H+m49N8OZKsO/vsOQv9WfHHXSablzAnsOFyiayEEVTACy6MMoKJnWpvOYn6RzAH3tkulkpboGUZ/G8Zmw/j6mbuxi6DXO1quimwHkcMbUj1cArc/AlWY+5R/3KZIP3CJkT7SKH03nfg=)
    username: ENC(lMDBcJMMAqRDsT68H20IRQ==)
    password: ENC(/qne/C3XsOIv//Ig2NmIFg==)
```

### 使用密文的好处
如你所知，`数据库`的`URL`、`用户名`和`密码`这类敏感信息应该被加密起来，以防不相干的人知道它们。

## 仍然使用明文
**如果你决定使用明文，将上述信息修改为:**

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/artanis?useSSL=true&serverTimezone=GMT%2B8
    username: 你的数据库用户名（默认是"root"）
    password: 你的数据库密码（默认是 ""（空字符串））
```


## 加密敏感属性
**如果你决定使用密文:**

### 选择一个 `秘钥`
`秘钥` 被用来加密和解密敏感属性。默认值为 `Artanis@Imple`。

### 获取密文
创建一个main方法，调用 `encrypt` 来加密上述的敏感属性。

```java
    public static void main(String[] args) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        //你所选择的 `秘钥`.
        textEncryptor.setPassword("Artanis@Imple");
        //上述敏感属性
        String url = textEncryptor.encrypt("jdbc:mysql://localhost:3306/artanis?useSSL=true&serverTimezone=GMT%2B8");
        String username = textEncryptor.encrypt("root")
        String password = textEncryptor.encrypt("");
                                            
        System.out.println(url);
        System.out.println(username);
        System.out.println(password);
    }
```

### 输出
```
H+m49N8OZKsO/vsOQv9WfHHXSablzAnsOFyiayEEVTACy6MMoKJnWpvOYn6RzAH3tkulkpboGUZ/G8Zmw/j6mbuxi6DXO1quimwHkcMbUj1cArc/AlWY+5R/3KZIP3CJkT7SKH03nfg=
lMDBcJMMAqRDsT68H20IRQ==
/qne/C3XsOIv//Ig2NmIFg==
```

### 修改配置文件
使用上述的输出来更新配置，你需要给上述输出的每行左右加上 `ENC(` 和 `)`：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: ENC(H+m49N8OZKsO/vsOQv9WfHHXSablzAnsOFyiayEEVTACy6MMoKJnWpvOYn6RzAH3tkulkpboGUZ/G8Zmw/j6mbuxi6DXO1quimwHkcMbUj1cArc/AlWY+5R/3KZIP3CJkT7SKH03nfg=)
    username: ENC(lMDBcJMMAqRDsT68H20IRQ==)
    password: ENC(/qne/C3XsOIv//Ig2NmIFg==)
```
