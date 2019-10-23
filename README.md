# DEVELOPMENT PAUSED UNTIL February 2020

## TODO 
### Add channel support


## Info
Libraries used:   
[TelegramBots](https://github.com/rubenlagus/TelegramBots)   
[SQLite-jdbc](https://github.com/xerial/sqlite-jdbc)   
[Quartz](https://github.com/quartz-scheduler/quartz)   
[JSoup](https://github.com/jhy/jsoup)   
[Exposed](https://github.com/JetBrains/Exposed)   
[Coroutines](https://github.com/Kotlin/kotlinx.coroutines)  
[klaxon](https://github.com/cbeust/klaxon)  
[ktlint-gradle](https://github.com/jlleitschuh/ktlint-gradle)   


### Build Status   
#### Dev:   
[![Build Status](https://travis-ci.com/Kraktun/KBot.svg?branch=dev)](https://travis-ci.com/Kraktun/KBot)
#### Master:   
[![Build Status](https://travis-ci.com/Kraktun/KBot.svg?branch=master)](https://travis-ci.com/Kraktun/KBot)   


## Latest release   
[![GitHub release](https://img.shields.io/github/release/Kraktun/KBot.svg?)](https://github.com/Kraktun/KBot/releases/latest)   


## Features   
* Filter commands received by the bot according to the chat and the status of the user.   
* Add optional filters (e.g. accept a command only if it's a reply).   
* Receive feedback when command is rejected and execute a function.   
* Manage multi-reply commands (ask-answer model) with a timer.   
* Boot-shutdown hooks.   


## How To   
* The package ```com.kraktun.kbot.commands``` contains a list of all the commands one or more of your bots support.
	* You can check ```com.kraktun.kbot.commands.examples``` package for a list of templates for both normal commands and ask-answer commands.  
* The package ```com.kraktun.kbot.commands.core``` contains the logic to register commands and parse messages.
* The package ```com.kraktun.kbot.bots``` contains the core of a bot, where you define which commands you want to register for that particular bot.
	* Note that a command is equal for all bots where it is registered. You can change the behaviour for one or more bot by checking the username (```absSender.username()```) but not the engine (targets, filters etc).
* The package ```com.kraktun.kbot.jobs``` contains a list of jobs to execute in a time frame. A job may be bound to one or more bots (as of now only LongPolling). Jobs must be registered in ```JobExecutor.kt```.
* The bots defined in package ```com.kraktun.kbot.bots``` must be registered in ```Main.kt``` and have a valid token + username in ```BotConfig.kt```.


## License

![GitHub](https://img.shields.io/github/license/Kraktun/KBot.svg)   

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.