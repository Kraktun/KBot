## Info
Written with [TelegramBots](https://github.com/rubenlagus/TelegramBots) library
following [TelegramBotsExample](https://github.com/rubenlagus/TelegramBotsExample)

## TO DO
The plan is to make the bot modular, so:
1) Completely rewrite [processNonCommandUpdates()](https://github.com/Kraktun/KBot/tree/master/src/krak/miche/handler/CommandsHandler.java#L101) to make it modular (replace all those switch with static methods)
2) Change the way commands are stored, making a list of all the commands so if you change one you don't have to find all the occurrences
3) Make methods in secondary_handlers static

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