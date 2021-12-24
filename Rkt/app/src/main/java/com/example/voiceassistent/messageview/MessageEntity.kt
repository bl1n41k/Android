package com.example.voiceassistent.messageview

class MessageEntity {
    @JvmField
    var text: String
    @JvmField
    var date: String
    @JvmField
    var isSend: Int

    constructor(text: String, date: String, isSend: Int) {
        this.text = text
        this.date = date
        this.isSend = isSend
    }

    constructor(message: Message) {
        text = message.text
        date = message.date.toString()
        isSend = if (!(message.isSend ?: false)) 0 else 1
    }
}