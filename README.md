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
You can set your own tasks by modifying config file `src/main/resources/journalConfig.xml`.

- **Mail Sender Identity**</br>
Identity info of mail sender
```xml
    <bean id="mailSenderIdentity" class="com.implementist.nisljournalmanager.domain.Identity">
        <property name="from" value="SENDER_ADDRESS"/>
        <property name="nickName" value="YOUR_NICK_NAME"/>
        <property name="authCode" value="AUTH_CODE_OR_PASSWORD"/>
    </bean>
```

| Property | Type | Description | Example |
| - | - | - | - |
| from | String | Email address of mail sender | abc@example.com |
| nickName | String | Nick name for mail sender | Implementist |
| authCode | String | Auth code or password of this email | Password |</br></br>

- **Urge Task**</br>
Set scheduled tasks to send mail to urge your staff to submit their work journal. **Urge mail will be sent to members of groups configured in `SummaryTask` by default. The program will perform a filtration automatically to get the correct `to` list.**
```xml
    <bean id="urgeTask1" class="com.implementist.nisljournalmanager.domain.UrgeTask">
        <property name="startTime" value="START_TIME"/>
        <property name="mailSubject" value="URGE_MAIL_SUBJECT"/>
        <property name="mailContent" value="URGE_MAIL_CONTENT"/>
        <property name="mailSenderIdentity" ref="mailSenderIdentity"/>
    </bean>
```
    
| Property | Type | Description | Example |
| - | - | - | - |
| startTime | String | Time of send urge mail | 22:00:00 |
| mailSubject | String | Subject of urge mail | Urge Mail |
| mailContent | String | Content of urge mail | Please submit your work journal on time. |
| mailSenderIdentity | String | Reference of id of mail sender Identity. It better not be modified. | mailSenderIdentity |

- **Summary Task**</br>
The summary task will conclude work journals of all staff to generate a `PDF` file and make it be the attachment of the summary mail. Then send the letter to staff and bosses or just to bosses.
```xml
    <bean id="summaryTask1" class="com.implementist.nisljournalmanager.domain.SummaryTask">
        <property name="groupOnHoliday" value="false"/>
        <property name="forBossesOnly" value="true"/>
        <property name="holidayers">
            <null/>
        </property>
        <property name="groups">
            <list>
                <value>1</value>
                <value>2</value>
            </list>
        </property>
        <property name="restDays">
            <array>
                <value>7</value>
            </array>
        </property>
        <property name="startTime" value="START_TIME"/>
        <property name="mailSubject" value="SUMMARY_MAIL_SUBJECT"/>
        <property name="bossesAddresses">
            <array>
                <value>boss1@address.com</value>
                <value>boss2@address.com</value>
            </array>
        </property>
        <property name="mailContent" value="SUMMARY_MAIL_CONTENT"/>
        <property name="mailSenderIdentity" ref="mailSenderIdentity"/>
    </bean>
```

| Property | Type | Description | Example |
| - | - | - | - |
| groupOnHoliday | boolean | Are members of this group on holiday? They do not need to send journals when it is `true` | false |
| forBossesOnly | boolean | Should summary mail be sent to bosses only or every one? | true |
| holidayers | String[] | Array of members who are on leave | `<value>Mary</value>` or `<null>` |
| groups | List<Integer> | Group ids of members who should send work journal | <value>1</value> |
| reastDays | int[] | Rest days during a week. `1` means Monday | <value>7</value> |
| startTime | String | Time of send summary mail | 23:00:00 |
| mailSubject | String | Subject of summary mail | Summary Mail |
| bossesAddresses | String[] | Array of bosses' mail addresses | <value>boss1@address.com</value> |
| mailContent | String | Content of summary mail | Attachment of this mail is about work journals of members of our group today. |
| mailSenderIdentity | String | Reference of id of mail sender Identity. It better not be modified. | mailSenderIdentity |

- **Initialize Task**</br>
This task will periodically reset the content of DB and clear `inbox` of mail sender.
```xml
    <bean id="initializeTask" class="com.implementist.nisljournalmanager.domain.InitializeTask">
        <property name="startTime" value="START_TIME"/>
        <property name="initialContent" value=""/>
        <property name="sourceFolder" value="INBOX"/>
        <property name="targetFolder" value="TARGET_FOLDER"/>        
        <property name="mailSenderIdentity" ref="mailSenderIdentity"/>
    </bean>
```

| Property | Type | Description | Example |
| - | - | - | - |
| startTime | String | Time of execute initialize task | 23:59:00 |
| initialContent | String | Init journal content of each member |  |
| sourceFolder | String | From which folder of mail sender to read journal contents of members | INBOX |
| targetFolder | String | Move mails of the last day to which folder | OldJournals |
| mailSenderIdentity | String | Reference of id of mail sender Identity. It better not be modified. | mailSenderIdentity |

-------
### System Properties
There are few more lines of config remaining in `src/main/resources/systemConfig.xml`.

- **System Config**</br>
```xml
    <bean id="systemConfig" class="com.implementist.nisljournalmanager.domain.SystemConfig">
        <property name="holidayModeOn" value="false"/>
        <property name="holidayFrom" value="2019-06-07"/>
        <property name="holidayTo" value="2019-06-09"/>
        <property name="workdayModeOn" value="false"/>
        <property name="workdayFrom" value="2019-05-05"/>
        <property name="workdayTo" value="2019-05-05"/>
    </bean>
```

| Property | Type | Description | Example |
| - | - | - | - |
| holidayModeOn | boolean | Are the whole group of members on holiday that no one should send journal? | false |
| holidayFrom | String | Date of the first day of holiday | 2019-10-01 |
| holidayTo | String | Date of the last day of holiday | 2019-10-07 |
| workDayModeOn | boolean | Should staff work even on a rest day? | false |
| workdayFrom | String | Date of the first day of workday | 2019-09-28 |
| workdayTo | String | Date of the last day of workday | 2019-09-29 |

</br>
