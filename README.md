
## Info
Written with [TelegramBots](https://github.com/rubenlagus/TelegramBots)      
and [Exposed](https://github.com/JetBrains/Exposed)   

### Build Status   
#### Dev:   
[![Build Status](https://travis-ci.com/Kraktun/KBot.svg?token=Uor7RP8xsv27XrHhEVTp&branch=dev)](https://travis-ci.com/Kraktun/KBot)
#### Master:   
[![Build Status](https://travis-ci.com/Kraktun/KBot.svg?token=Uor7RP8xsv27XrHhEVTp&branch=master)](https://travis-ci.com/Kraktun/KBot)   

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