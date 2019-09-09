LINK:
```
https://api.telegram.org/bot$bot_token/getUpdates
```

Private message
HTTP:
```
{"update_id":123456789,
"message":{"message_id":1234,"from":{"id":1234567,"is_bot":false,"first_name":"MYNAME","username":"MYUSERNAME","language_code":"en"},"chat":{"id":1234567,"first_name":"MYNAME","username":"MYUSERNAME","type":"private"},"date":1568038953,"text":"/hello","entities":[{"offset":0,"length":6,"type":"bot_command"}]}}
```
JAVA:
```
Update{updateId=123456789, message=Message{messageId=1234, from=User{id=1234567, firstName='MYNAME', isBot=false, lastName='null', userName='MYUSERNAME', languageCode='en'}, date=1568038953, chat=Chat{id=1234567, type='private', title='null', firstName='MYNAME', lastName='null', userName='MYUSERNAME', allMembersAreAdministrators=null, photo=null, description='null', inviteLink='null', pinnedMessage=null, stickerSetName='null', canSetStickerSet=null, permissions=null}, forwardFrom=null, forwardFromChat=null, forwardDate=null, text='/hello', entities=[MessageEntity{type='bot_command', offset=0, length=6, url=null, user=null}], captionEntities=null, audio=null, document=null, photo=null, sticker=null, video=null, contact=null, location=null, venue=null, animation=null, pinnedMessage=null, newChatMembers=null, leftChatMember=null, newChatTitle='null', newChatPhoto=null, deleteChatPhoto=null, groupchatCreated=null, replyToMessage=null, voice=null, caption='null', superGroupCreated=null, channelChatCreated=null, migrateToChatId=null, migrateFromChatId=null, editDate=null, game=null, forwardFromMessageId=null, invoice=null, successfulPayment=null, videoNote=null, authorSignature='null', forwardSignature='null', mediaGroupId='null', connectedWebsite='null', passportData=null, forwardSenderName='null', poll=null, replyMarkup=null}, inlineQuery=null, chosenInlineQuery=null, callbackQuery=null, editedMessage=null, channelPost=null, editedChannelPost=null, shippingQuery=null, preCheckoutQuery=null}
```

Group message
HTTP:
```
{"update_id":123456789,
"message":{"message_id":1234,"from":{"id":1234567,"is_bot":false,"first_name":"MYNAME","username":"MYUSERNAME","language_code":"en"},"chat":{"id":-9876543,"title":"GROUPNAME","type":"group","all_members_are_administrators":true},"date":1568039492,"text":"/hello","entities":[{"offset":0,"length":6,"type":"bot_command"}]}}
```
JAVA:
```
Update{updateId=123456789, message=Message{messageId=1234, from=User{id=1234567, firstName='MYNAME', isBot=false, lastName='null', userName='MYUSERNAME', languageCode='en'}, date=1568039492, chat=Chat{id=-9876543, type='group', title='GROUPNAME', firstName='null', lastName='null', userName='null', allMembersAreAdministrators=true, photo=null, description='null', inviteLink='null', pinnedMessage=null, stickerSetName='null', canSetStickerSet=null, permissions=null}, forwardFrom=null, forwardFromChat=null, forwardDate=null, text='/hello', entities=[MessageEntity{type='bot_command', offset=0, length=6, url=null, user=null}], captionEntities=null, audio=null, document=null, photo=null, sticker=null, video=null, contact=null, location=null, venue=null, animation=null, pinnedMessage=null, newChatMembers=null, leftChatMember=null, newChatTitle='null', newChatPhoto=null, deleteChatPhoto=null, groupchatCreated=null, replyToMessage=null, voice=null, caption='null', superGroupCreated=null, channelChatCreated=null, migrateToChatId=null, migrateFromChatId=null, editDate=null, game=null, forwardFromMessageId=null, invoice=null, successfulPayment=null, videoNote=null, authorSignature='null', forwardSignature='null', mediaGroupId='null', connectedWebsite='null', passportData=null, forwardSenderName='null', poll=null, replyMarkup=null}, inlineQuery=null, chosenInlineQuery=null, callbackQuery=null, editedMessage=null, channelPost=null, editedChannelPost=null, shippingQuery=null, preCheckoutQuery=null}
```

Channel message
HTTP:
```
{"update_id":123456789,
"channel_post":{"message_id":1234,"chat":{"id":-98765432,"title":"CHANNELNAME","type":"channel"},"date":1568039621,"text":"/hello","entities":[{"offset":0,"length":6,"type":"bot_command"}]}}
```
JAVA:
```
Update{updateId=123456789, message=null, inlineQuery=null, chosenInlineQuery=null, callbackQuery=null, editedMessage=null, channelPost=Message{messageId=1234, from=null, date=1568039621, chat=Chat{id=-98765432, type='channel', title='CHANNELNAME', firstName='null', lastName='null', userName='null', allMembersAreAdministrators=null, photo=null, description='null', inviteLink='null', pinnedMessage=null, stickerSetName='null', canSetStickerSet=null, permissions=null}, forwardFrom=null, forwardFromChat=null, forwardDate=null, text='/hello', entities=[MessageEntity{type='bot_command', offset=0, length=6, url=null, user=null}], captionEntities=null, audio=null, document=null, photo=null, sticker=null, video=null, contact=null, location=null, venue=null, animation=null, pinnedMessage=null, newChatMembers=null, leftChatMember=null, newChatTitle='null', newChatPhoto=null, deleteChatPhoto=null, groupchatCreated=null, replyToMessage=null, voice=null, caption='null', superGroupCreated=null, channelChatCreated=null, migrateToChatId=null, migrateFromChatId=null, editDate=null, game=null, forwardFromMessageId=null, invoice=null, successfulPayment=null, videoNote=null, authorSignature='null', forwardSignature='null', mediaGroupId='null', connectedWebsite='null', passportData=null, forwardSenderName='null', poll=null, replyMarkup=null}, editedChannelPost=null, shippingQuery=null, preCheckoutQuery=null}
```
