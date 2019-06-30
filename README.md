# NISLJournalManager: A staff work journal auto-collect system.
[![Build Status](https://travis-ci.com/Implementist/NISLJournalManager.svg?token=tApu9wqBLRxw6iZbENoB&branch=master)](https://travis-ci.com/Implementist/NISLJournalManager)
[![License](https://img.shields.io/badge/licence-Apache%202.0-brightgreen.svg?style=flat)](LICENSE)

NISLJournalManager is a spring framework based staff work journal auto-collect system. You can DIY your urging and summary tasks without re-compile the whole project but modify the config files only.

Various options make it easy to use. For example, you can set which day(s) is the rest day of your group in a week. And you can also set `holidayers` list for people who are on leave. After doing so, they do not need to submit work journals during their holiday.

## Usage
You can build up your work journal collecting system by just doing the following few steps.

### Step1: Set up databases
- Create a database with name `nisl_journal` and its charset should be `utf8 -- UTF-8 Unicode`.
- Create a table named `group` with [DDL](https://github.com/Implementist/NISLJournalManager/blob/master/docs/ddl-group.md).
- Create a table named `member` with [DDL](https://github.com/Implementist/NISLJournalManager/blob/master/docs/ddl-member.md).

### Step2: Make your own config
- There are various properties to config to run your bussiness. For details see [Configuration](https://github.com/Implementist/NISLJournalManager/tree/master#configuration)

### Step3: Build & Deploy
After finishing configuration, you need to:
- Insert some groups and their members into the two tables in database `nisl_journal`. 
- Build this project with maven and then deploy the `NISLJournalManager.war` file to the tomcat server.

## Configuration
**The following steps of config is nessesary.**

### Set Property of DB
For program access your DB successfully, you have to replace default DB properties with your own. For details see [DB config](https://github.com/Implementist/NISLJournalManager/blob/master/docs/db-config.md).

### Journal Properties
You can set your own bussiness by modify two config files: `src/main/resources/journalConfig.xml` and `src/main/resources/systemConfig.xml`.

**Let's begin with `journalConfig.xml`; there are five types of configurable bean.**

- Mail Sender Identity
```xml
    <bean id="mailSenderIdentity" class="com.implementist.nisljournalmanager.domain.Identity">
        <property name="from" value="SENDER_ADDRESS"/>
        <property name="nickName" value="YOUR_NICK_NAME"/>
        <property name="authCode" value="AUTH_CODE_OR_PASSWORD"/>
    </bean>
```

Property | Type | Description | Example
- | - | - | -
from | String | Email address of mail sender | abc@example.com
nickName | String | Nick name for mail sender | Imple
authCode | String | Auth code or password of this email | PASSWORD

