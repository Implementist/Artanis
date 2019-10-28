# Artanis: A Work Journal Auto Manage System - En Taro Artanis!
[![Build Status](https://travis-ci.com/Implementist/Artanis.svg?branch=master)](https://travis-ci.com/Implementist/Artanis)
[![License](https://img.shields.io/badge/licence-Apache%202.0-brightgreen.svg?style=flat)](LICENSE)
![release](https://img.shields.io/github/release/Implementist/Artanis.svg)
![vulnerabilities](https://img.shields.io/snyk/vulnerabilities/github/Implementist/Artanis.svg)

[中文版](https://github.com/Implementist/Artanis/blob/master/README_CN.md)

Artanis is a spring-boot based work journal auto manage system. You can DIY your tasks without re-compiling the whole project but modify the config files only.

Various options make it easy to use. For example, you can set which day(s) is the rest day of your group in a week. And you can also set `holidayers` list for people who are on leave. After doing so, they do not need to submit work journals during their holiday.

**Note that this is a pure back-end system without any UI pages.**

## Workflow
- The system executes **Urge Task** on schedule to remind people who have not submitted their work journal to do so.
- Members of group submit their work journal by sending an email to `MailSender` before the deadline.
- The system executes **Summary Task** at a fixed time which includes the following steps:
  - Collect work journal contents from `INBOX` of `MailSender`.
  - Generate a pdf file by using the above-mentioned contents.
  - Create a new email and set the pdf file which the last step generated as an attachment.
  - Send the email to boss or everyone.
- The system executes **Initialize Task** at a fixed time which includes the following steps:
  - Move all emails in `INBOX` to other folders (like 'past_journals').
  - Clear up file `submitted` and `journal_content` of database.

![Content of Summary PDF](https://raw.github.com/Implementist/Artanis/master/images/content_of_summary_pdf.png)

## Usage
You can build up your system by just following few steps.

### Step1: Apply for an Email Account
**This email account is called `Mail Sender`.** 
- The staff of your group should sender their work journal to this email address.
- The system sends emails(such as urge mails and summary mails) from this email address.
- The system collects work journals from the `INBOX` of this email address.
**You can extend configs of other mail service providers and then use their email account, or by default you should apply and use a `NetEase 163` account.**

### Step2: Set up databases
**By default you have to choose `MySQL` as the database for this system.**
- Please make sure you have installed `MySQL` to your server properly.
- Create a database with name `artanis` and its charset should be `utf8mb4 -- UTF-8 Unicode`.

### Step3: Make your own config
- There are various properties to config to run your bussiness. For details see [Configuration](https://github.com/Implementist/Artanis/tree/master#configuration)

### Step4: Build & Run
After finishing configuration, you need to:
- Build this project with maven to generate file `artanis.jar`.
- Copy `artanis.jar` to a directory of your server.
- Copy `/configs/journalConfig.xml` and `/configs/systemConfig.xml` to the same directory of `artanis.jar`.
- Run the follwing command in above-mentioned directory: `java -jar -Djasypt.encryptor.password=Artanis@Imple artanis.jar**`.
- Or use `java -jar artanis.jar` if you makes **DB Config** with plain text only.
- Insert some groups and their members into the two tables in database `artanis`. Then system starts to run.

## Configuration
The following steps of config is nessesary.

### Set Property of DB
For program access your DB successfully, you have to replace default DB properties with your own. For details see [DB Config](https://github.com/Implementist/Artanis/blob/master/docs/db-config.md).

### Journal Properties
You can set your own tasks by modifying config file `src/main/resources/journalConfig.xml`.

- **Mail Sender Identity**</br>
Identity info of mail sender
```xml
    <bean id="mailSenderIdentity" class="com.implementist.artanis.entity.Identity">
        <property name="from" value="SENDER_ADDRESS"/>
        <property name="nickName" value="YOUR_NICK_NAME"/>
        <property name="authCode" value="AUTH_CODE_OR_PASSWORD"/>
    </bean>
```

| Property | Type | Description | Example |
| - | - | - | - |
| from | String | Email address of mail sender | abc@example.com |
| nickName | String | Nick name for mail sender | Implementist |
| authCode | String | Auth code or password of this email | Password |

</br>

- **Urge Task**</br>
Set scheduled tasks to send mail to urge your staff to submit their work journal. **Urge mail will be sent to members of groups configured in `SummaryTask` by default. The program will perform a filtration automatically to get the correct `to` list.**
```xml
    <bean id="urgeTask1" class="com.implementist.artanis.entity.taskdata.UrgeTaskData">
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

</br>

- **Summary Task**</br>
The summary task will conclude work journals of all staff to generate a `PDF` file and make it be the attachment of the summary mail. Then send the letter to staff and bosses or just to bosses.
```xml
    <bean id="summaryTask1" class="com.implementist.artanis.entity.taskdata.SummaryTaskData">
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
| holidayers | String[] | Array of members who are on leave | `<value>Mary</value>` or `<null/>` |
| groups | List&lt;Integer&gt; | Group ids of members who should send work journal | &lt;value&gt;1&lt;/value&gt; |
| reastDays | int[] | Rest days during a week. `1` means Monday | &lt;value&gt;7&lt;/value&gt; |
| startTime | String | Time of send summary mail | 23:00:00 |
| mailSubject | String | Subject of summary mail | Summary Mail |
| bossesAddresses | String[] | Array of bosses' mail addresses | &lt;value&gt;boss1@address.com&lt;/value&gt; |
| mailContent | String | Content of summary mail | Attachment of this mail is about work journals of members of our group today. |
| mailSenderIdentity | String | Reference of id of mail sender Identity. It better not be modified. | mailSenderIdentity |

</br>

- **Initialize Task**</br>
This task will periodically reset the content of DB and clear `inbox` of mail sender.
```xml
    <bean id="initializeTask" class="com.implementist.artanis.entity.taskdata.InitializeTaskData">
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
    <bean id="systemConfig" class="com.implementist.artanis.entity.SystemConfig">
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

## Comunication
- **Email:** implementist@outlook.com
- **WeChat:** Megamind_cs
- **CSDN:** https://blog.csdn.net/mr_megamind
- **[GitHub Issues](https://github.com/Implementist/Artanis/issues)**

## LICENSE
```
Copyright (c) 2017-present, Artanis Contributors.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```