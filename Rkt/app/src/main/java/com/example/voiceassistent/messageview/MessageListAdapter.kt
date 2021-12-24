package com.example.voiceassistent.messageview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.voiceassistent.R
import java.util.*

class MessageListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    @JvmField
    var messageList = ArrayList<Message>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view: View? = null
        view =
            if (viewType == USER_TYPE) { //создание сообщения от пользователя
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.user_message, parent, false)
            } else { //создание сообщения от ассистента
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.assistant_message, parent, false)
            }
        return MessageViewHolder(view)
    }

    /**
     * Вызывается кгда появляется новое сообщение
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mes = messageList[position] //сообщение под номером
        val messageViewHolder = holder as MessageViewHolder
        messageViewHolder.bind(mes)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(index: Int): Int {
        val message = messageList[index]
        return if (message.isSend ?: false) {
            USER_TYPE
        } else ASSISTANT_TYPE
    }

    companion object {
        private const val ASSISTANT_TYPE = 0
        private const val USER_TYPE = 1
    }
}