
## Info
Written with [TelegramBots](https://github.com/rubenlagus/TelegramBots)      
and [Exposed](https://github.com/JetBrains/Exposed)   


### Build Status   
#### Dev:   
[![Build Status](https://travis-ci.com/Kraktun/KBot.svg?token=Uor7RP8xsv27XrHhEVTp&branch=dev)](https://travis-ci.com/Kraktun/KBot)
#### Master:   
[![Build Status](https://travis-ci.com/Kraktun/KBot.svg?token=Uor7RP8xsv27XrHhEVTp&branch=master)](https://travis-ci.com/Kraktun/KBot)   


## Features   
Filter commands received by the bot according to the chat and the status of the user.   
Add optional filters (e.g. accept a command only if it's a reply).   
Receive feedback when command is rejected and execute a function.   
Manage multi-reply commands (ask-answer model) with a timer.   
Boot-shutdown hooks.   

## How To   
* Add/edit a command in ```commands```: implement ```CommandInterface.kt``` and create an instance of ```BaseCommand.kt```.   
   * You can check ```example``` package for a list of templates.   
* Register your new command in ```MainBot.kt```.   
* For deeper customizations, change the method ```onUpdateReceived``` in ```MainBot.kt```.   
* Change whatever you want...   


## Notes   
The ```commands.core``` folder is meant to be used in a single bot (=thread). For concurrent threads on the same bot, every access to the ```var map``` must be synchronized.      
For concurrent bots, ```CommandProcessor.kt``` and ```MultiCommandsHandler.kt``` must be duplicated and each class used in a single bot.   
Classes in ```utils``` folder are already synchronized to manage concurrent bots.   


## License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.