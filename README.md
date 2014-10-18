offlinetwitter
==============

An Android GUI wrapper to hit Twitter SMS APIs without using any internet connection at all.

The goal of this app is to provide a user with an offline Twitter experience. 
Due to robust Twitter SMS APIs, it's entirely possible to construct an app UI to hit Twitter servers without any internet connection.
Such an app can be good for developing countries lacking robust internet or mobile data infrastructure.

The current version as is tests SMS functionality and integration into the UI.

Features to be added:
Internal SQLite DB to store SMS to be used (also as a backup copy of SMS data)
Notification detection
SMS sorting by notifications/actual texts

Some good docs are here:
https://support.twitter.com/articles/14589-getting-started-with-twitter-via-sms