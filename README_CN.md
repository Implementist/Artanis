# NISLJournalManager: 员工工作日报自动收发系统
[![Build Status](https://travis-ci.com/Implementist/NISLJournalManager.svg?token=tApu9wqBLRxw6iZbENoB&branch=master)](https://travis-ci.com/Implementist/NISLJournalManager)
[![License](https://img.shields.io/badge/licence-Apache%202.0-brightgreen.svg?style=flat)](LICENSE)

[English Version](https://github.com/Implementist/NISLJournalManager/blob/master/README.md)

![汇总PDF的内容](https://raw.github.com/Implementist/NISLJournalManager/master/images/content_of_summary_pdf_cn.png)

NISLJournalManager是一款基于Spring Framework的员工工作日报自动收发系统。你可以DIY你的催促和总结任务，在无需重新编译整个项目的前提下，只修改少数配置文件。

丰富的可选项使其非常得简单易用. 例如，你可以设置你的小组在一周中的哪一天休息。你还可以将正在请假或休假中的员工加入到`holidayers`列表，这样他们在假期就不会被系统催促提交工作日志了。

**注意：本系统是一个不包含任何UI界面的纯后端系统。**

## 使用说明
你可以通过以下很少的步骤来构建自己的工作日志收发系统。

### 步骤1: 申请一个邮箱
这个邮箱被称作`Mail Sender`.
- 你的小组中的员工应该将他们的工作日志发送到这个邮箱。
- 系统将通过这个账户发送邮件（包括催促邮件和汇总邮件）。
- 系统会从这个邮箱的收件箱收取工作日志。
**你可以扩展其他邮件服务提供商的配置并使用他们的电子邮箱，否则在默认情况下，你应该申请并使用网易163帐户。**

### 步骤2: 创建数据库
- 创建一个名叫`nisl_journal`的数据库，它的字符集应该是`utf8mb4 -- UTF-8 Unicode`。
- 通过 [DDL](https://github.com/Implementist/NISLJournalManager/blob/master/docs/ddl-group.md) 创建一个名为`group`的表。
- 通过 [DDL](https://github.com/Implementist/NISLJournalManager/blob/master/docs/ddl-member.md) 创建一个名为`member`的表。

### 步骤3: 使用自己的配置
- 有许多属性可以配置来运行你自己的业务逻辑。详情见 [配置](https://github.com/Implementist/NISLJournalManager/blob/master/README_CN.md#%E9%85%8D%E7%BD%AE)

### 步骤4: 构建和部署
完成了配置之后，你需要：
- 向数据库`nisl_journal`中插入一些组和他们的成员信息。
- 通过maven构建这个工程然后将`NISLJournalManager.war`文件部署到tomcat服务器下。

## 配置
下面的配置步骤是必需的

### 设置数据库属性
为了使程序可以成功的访问你的数据库，你需要将默认的数据库配置换成你自己的。详情见 [数据库配置](https://github.com/Implementist/NISLJournalManager/blob/master/docs/db-config.md).

### 日志属性
你可以通过修改配置文件`src/main/resources/journalConfig.xml`来设置自己的任务

- **Mail Sender Identity**</br>
Mail Sender的身份信息
```xml
    <bean id="mailSenderIdentity" class="com.implementist.nisljournalmanager.domain.Identity">
        <property name="from" value="SENDER_ADDRESS"/>
        <property name="nickName" value="YOUR_NICK_NAME"/>
        <property name="authCode" value="AUTH_CODE_OR_PASSWORD"/>
    </bean>
```

| 属性 | 类型 | 说明 | 举例 |
| - | - | - | - |
| from | String | Mail Sender的邮箱地址 | abc@example.com |
| nickName | String | Mail Sender的昵称 | Implementist |
| authCode | String | 授权码或者密码 | Password |

</br>

- **Urge Task**</br>
设置发送督促邮件的任务，督促员工提交工作日志。**在默认情况下，督促邮件将发送给`SummaryTask`中配置的组的成员。程序将自动执行过滤，以获得正确的`to`列表。**
```xml
    <bean id="urgeTask1" class="com.implementist.nisljournalmanager.domain.UrgeTask">
        <property name="startTime" value="START_TIME"/>
        <property name="mailSubject" value="URGE_MAIL_SUBJECT"/>
        <property name="mailContent" value="URGE_MAIL_CONTENT"/>
        <property name="mailSenderIdentity" ref="mailSenderIdentity"/>
    </bean>
```
    
| 属性 | 类型 | 说明 | 举例 |
| - | - | - | - |
| startTime | String | 发送催促邮件的时间 | 22:00:00 |
| mailSubject | String | 催促邮件的主题 | 催促邮件 |
| mailContent | String | 催促邮件的内容 | 请按时提交你的工作日志。 |
| mailSenderIdentity | String | 对邮件发送者身份的id引用。最好不要修改。 | mailSenderIdentity |

</br>

- **Summary Task**</br>
总结任务将总结所有员工的工作日志，生成一个PDF文件，作为总结邮件的附件。然后把这封邮件发送给员工和老板，或者只发送给领导。
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

| 属性 | 类型 | 说明 | 举例 |
| - | - | - | - |
| groupOnHoliday | boolean | 是否整个小组都在放假？如果设置为`true`，所有人都不需要发送工作日志 | false |
| forBossesOnly | boolean | 汇总邮件之发送给领导还是发送给所有人？ | true |
| holidayers | String[] | 小组中正在请假或休假的人 | `<value>小明</value>` 或者 `<null/>` |
| groups | List&lt;Integer&gt; | 需要提交工作日志的小组的id号 | &lt;value&gt;1&lt;/value&gt; |
| reastDays | int[] | 一周中的休息日，`1`代表星期一 | &lt;value&gt;7&lt;/value&gt; |
| startTime | String | 发送汇总邮件的时间 | 23:00:00 |
| mailSubject | String | 汇总邮件的主题 | 汇总邮件 |
| bossesAddresses | String[] | 领导的邮箱地址数组 | &lt;value&gt;boss1@address.com&lt;/value&gt; |
| mailContent | String | 汇总邮件的内容| 附件中是今天本小组的工作日志汇总 |
| mailSenderIdentity | String | 对邮件发送者身份的id引用。最好不要修改。 | mailSenderIdentity |

</br>

- **Initialize Task**</br>
这个任务会重置数据库中的日志内容并清空Mail Sender的收件箱.
```xml
    <bean id="initializeTask" class="com.implementist.nisljournalmanager.domain.InitializeTask">
        <property name="startTime" value="START_TIME"/>
        <property name="initialContent" value=""/>
        <property name="sourceFolder" value="INBOX"/>
        <property name="targetFolder" value="TARGET_FOLDER"/>        
        <property name="mailSenderIdentity" ref="mailSenderIdentity"/>
    </bean>
```

| 属性 | 类型 | 说明 | 举例 |
| - | - | - | - |
| startTime | String | 执行初始化任务的时间 | 23:59:00 |
| initialContent | String | 每个成员的初始化日报内容 |  |
| sourceFolder | String | 系统从Mail Sender的哪个文件夹读取员工日志信息？ | INBOX |
| targetFolder | String | 前一天的日志邮件需要被移动到Mail Sender的哪个文件夹？ | 以往日志 |
| mailSenderIdentity | String | 对邮件发送者身份的id引用。最好不要修改。 | mailSenderIdentity |

-------
### 系统属性
还有少量的配置在`src/main/resources/systemConfig.xml`文件中。

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

| 属性 | 类型 | 说明 | 举例 |
| - | - | - | - |
| holidayModeOn | boolean | 节假日模式是否开启？开启后所有人都不需要提交工作日志 | false |
| holidayFrom | String | 节假日的第一天 | 2019-10-01 |
| holidayTo | String | 节假日的最后一天 | 2019-10-07 |
| workDayModeOn | boolean | 调休模式是否开启？开启后即使在休息日也需要提交工作日志 | false |
| workdayFrom | String | 调休日的第一天 | 2019-09-28 |
| workdayTo | String | 调休日的最后一天 | 2019-09-29 |

## 联系我
- **邮箱:** implementist@outlook.com
- **微信:** Megamind_cs
- **CSDN:** https://blog.csdn.net/mr_megamind
- **[GitHub Issues](https://github.com/Implementist/NISLJournalManager/issues)**

## 开源许可
```
Copyright (c) 2017-present, NISLJournalManager Contributors.

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