
# KBot

## Info

Libraries used:

[TelegramBots](https://github.com/rubenlagus/TelegramBots)\
[Quartz](https://github.com/quartz-scheduler/quartz)\
[Coroutines](https://github.com/Kotlin/kotlinx.coroutines)\
[ktlint-gradle](https://github.com/jlleitschuh/ktlint-gradle)\
[KUtils](https://github.com/Kraktun/KUtils)

Examples available at [KBotExamples](https://github.com/Kraktun/KBotExamples)

### Build Status

#### Dev

[![Build Status](https://travis-ci.com/Kraktun/KBot.svg?branch=dev)](https://travis-ci.com/Kraktun/KBot)

#### Master

[![Build Status](https://travis-ci.com/Kraktun/KBot.svg?branch=master)](https://travis-ci.com/Kraktun/KBot)

## Latest release

[![GitHub release](https://img.shields.io/github/release/Kraktun/KBot.svg?)](https://github.com/Kraktun/KBot/releases/latest)

## Features

* Filter updates received by the bot according to the type (command, callback, new users, ask-answer model etc.).
* Add filters to commands:
  * pair 'type of chat'-'status'
  * number of following arguments
  * additional message options (e.g. message is a reply)
  * additional chat options (e.g. bot is an admin)
* Execute code if an error occurs
* Execute custom code for each type of update received
* Manage multi-reply commands (ask-answer model) with custom timers
* Manage callback with custom timers
* Add and remove simple recurring jobs
* Many usefull extensions to the parent library

## How To

* Example bots will be available soon.

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