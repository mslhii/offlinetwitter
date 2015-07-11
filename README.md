offlinetwitter
==============

An Android GUI wrapper to hit Twitter SMS APIs without using any internet connection at all.

The goal of this app is to provide a user with an offline Twitter experience. 
Due to robust Twitter SMS APIs, it's entirely possible to construct an app UI to hit Twitter servers without any internet connection.
Such an app can be good for developing countries lacking robust internet or mobile data infrastructure.

The current version as is tests SMS functionality and integration into the UI. Place on hiatus now.

V1 Features added:
- SMS sorting by notifications/actual texts
- UI implementation to mimic Twitter

V1 Features in progress:
- Profile SMS listener not being handled properly

V1 Features to be added:
- Messages implementation needs to be finished
- SMS Reception listeners for updates
- More polished UI

V2 Features to be added:
Internal SQLite DB to store SMS to be used (also as a backup copy of SMS data)
Notification detection (Any version not KitKat and above, thanks Google)
Caching once internet connection is reached?

Some good docs are here:
https://support.twitter.com/articles/14589-getting-started-with-twitter-via-sms


Quick note:
Seems like Kitkat and Lollipop is giving me a lot of problems with its new "default SMS app" rule. Guess I need to place some more Asynctasks to handle silent SMS tasks in the background because of this. A simple implementation got harder thanks to Google :(
