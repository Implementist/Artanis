## Set your DB config
**You can follow steps below to replace the default DB config by your own.**

### Encrypt sensitive properties
There is a tool in package `com.implementist.nisljournalmanager` named as `DESUtil.java`

- Modify `SECRET_KEY`
`SECRET_KEY` is used to encrypt and decrypt sensitive properties. You can modify it whatever by your wish or just keep the default value `IMplementist`.

```java
    private static final String SECRET_KEY = "IMplementist";
```

- Encrypt sensitive properties
For `username` and `password` of DB is sensitive, we create the main method to call `encrypt` to encode them.

```java
    public static void main(String[] args) {
        DESUtil util = new DESUtil();
        System.out.println(util.encode("root"));
        System.out.println(util.encode("password"));
    }
```

**Output**
```
fmvcOKRgpm0=
VcnJNjqg6p9SQ/XsmlEhag==
```

### Modify config file
The config file `jdbc.properties` is under `src/main/resources/`.

```
driverClassName=com.mysql.jdbc.Driver
url=jdbc:mysql://localhost:3306/nisl_journal
username=fmvcOKRgpm0=
password=VcnJNjqg6p9SQ/XsmlEhag==
```

| Property | Description | Example |
| - | - | - |
| driverClassName | Mysql Connector | com.mysql.jdbc.Driver |
| url | URL to connect db. Your can modify port and db name by your wish | jdbc:mysql://localhost:3306/nisl_journal |
| username | Encrypted db username | fmvcOKRgpm0= |
| password | Encrypted db password | VcnJNjqg6p9SQ/XsmlEhag== |
