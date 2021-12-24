package com.example.voiceassistent.messageview

import java.util.*

class Message {
    @JvmField
    var text: String
    @JvmField
    var date: Date? = null
    @JvmField
    var isSend: Boolean?

    constructor(text: String, isSend: Boolean) {
        this.text = text
        date = Date()
        this.isSend = isSend
    }

    constructor(entity: MessageEntity) {
        text = entity.text
        if (entity.date == null) date = null else date = Date(entity.date)
        isSend = entity.isSend != 0
    }
}