## You can run the following DDL to create table 'member' in database 'nisl_journal'

```ddl
CREATE TABLE `member` (
  `Id` int(2) unsigned NOT NULL AUTO_INCREMENT,
  `Name` varchar(64) DEFAULT '',
  `GroupId` int(1) unsigned DEFAULT NULL,
  `Submitted` tinyint(1) unsigned DEFAULT '0',
  `Content` text CHARACTER SET utf8mb4,
  `EmailAddress` varchar(128) NOT NULL DEFAULT '',
  `Identity` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`Id`,`EmailAddress`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8;
```