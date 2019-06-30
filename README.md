# NISLJournalManager: A staff work journal auto-collect system.
[![Build Status](https://travis-ci.com/Implementist/NISLJournalManager.svg?token=tApu9wqBLRxw6iZbENoB&branch=master)](https://travis-ci.com/Implementist/NISLJournalManager)
[![License](https://img.shields.io/badge/licence-Apache%202.0-brightgreen.svg?style=flat)](LICENSE)

NISLJournalManager is a spring framework based staff work journal auto-collect system. You can DIY your urging and summary tasks without re-compile the whole project but modify the config files only.

Various options make it easy to use. For example, you can set which day(s) is the rest day of your group in a week. And you can also set 'holidayers' list for people who are on leave. After doing so, they do not need to submit work journals during their holiday.

## Usage
You can build up your work journal collecting system by just doing the following few steps.
### Step1: Set up databases
- You need to create a database with name 'nisl_journal' and its charset should be 'utf8 -- UTF-8 Unicode'.
- Create a table named 'group' with [DDL](https://github.com/Implementist/NISLJournalManager/blob/master/docs/ddl-group.md).
- Create a table named 'member' with [DDL](https://github.com/Implementist/NISLJournalManager/blob/master/docs/ddl-member.md)