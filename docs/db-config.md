## Set your DB config
**You can follow steps below to replace the default DB config by your own.**
All you need to focus on are the following few lines in `/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: ENC(H+m49N8OZKsO/vsOQv9WfHHXSablzAnsOFyiayEEVTACy6MMoKJnWpvOYn6RzAH3tkulkpboGUZ/G8Zmw/j6mbuxi6DXO1quimwHkcMbUj1cArc/AlWY+5R/3KZIP3CJkT7SKH03nfg=)
    username: ENC(lMDBcJMMAqRDsT68H20IRQ==)
    password: ENC(/qne/C3XsOIv//Ig2NmIFg==)
```

### Advantages of using cipher text
As you know, sensitive properties like `URL`, `username` and `password` of `database` should be encrypted to prevent to be known for ones who have nothing to do with this.

## Still use plain text
**If you decide to use palin text modify the config to:**

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/artanis?useSSL=true&serverTimezone=GMT%2B8
    username: YOUR USERNAME OF DB (default is "root")
    password: YOUR PASSWORD OF DB (default is ""(empty string))
```


## Encrypt sensitive properties
**If you decide to use cipher text:**

### Choose a `SECRET_KEY`
`SECRET_KEY` is used to encrypt and decrypt sensitive properties. The default value is `Artanis@Imple`.

### Get cipher text
Create a main method to call `encrypt` to encode the above-mentioned sensitive properties.

```java
    public static void main(String[] args) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        //The `SECRET_KEY` you've chosen.
        textEncryptor.setPassword("Artanis@Imple");
        //Above-mentioned sensitive properties
        String url = textEncryptor.encrypt("jdbc:mysql://localhost:3306/artanis?useSSL=true&serverTimezone=GMT%2B8");
        String username = textEncryptor.encrypt("root")
        String password = textEncryptor.encrypt("");
                                            
        System.out.println(url);
        System.out.println(username);
        System.out.println(password);
    }
```

### Output
```
H+m49N8OZKsO/vsOQv9WfHHXSablzAnsOFyiayEEVTACy6MMoKJnWpvOYn6RzAH3tkulkpboGUZ/G8Zmw/j6mbuxi6DXO1quimwHkcMbUj1cArc/AlWY+5R/3KZIP3CJkT7SKH03nfg=
lMDBcJMMAqRDsT68H20IRQ==
/qne/C3XsOIv//Ig2NmIFg==
```

### Modify config file
Use the above output to update config. You need to add `ENC(` and `)` around each line of the above cipher text:

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: ENC(H+m49N8OZKsO/vsOQv9WfHHXSablzAnsOFyiayEEVTACy6MMoKJnWpvOYn6RzAH3tkulkpboGUZ/G8Zmw/j6mbuxi6DXO1quimwHkcMbUj1cArc/AlWY+5R/3KZIP3CJkT7SKH03nfg=)
    username: ENC(lMDBcJMMAqRDsT68H20IRQ==)
    password: ENC(/qne/C3XsOIv//Ig2NmIFg==)
```
