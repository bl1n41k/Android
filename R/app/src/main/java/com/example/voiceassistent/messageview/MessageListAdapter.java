package com.example.voiceassistent.messageview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voiceassistent.R;

import java.util.ArrayList;

public class MessageListAdapter extends RecyclerView.Adapter {

    public ArrayList<Message> messageList = new ArrayList<>();
    private static final int ASSISTANT_TYPE=0;
    private static final int USER_TYPE=1;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == USER_TYPE) {//создание сообщения от пользователя
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_message,parent,false);
        }
        else { //создание сообщения от ассистента
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.assistant_message,parent,false);
        }

        return new MessageViewHolder(view);
    }

    /**
     * Вызывается кгда появляется новое сообщение
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message mes = messageList.get(position); //сообщение под номером
        MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
        messageViewHolder.bind(mes);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public int getItemViewType(int index){
        Message message = messageList.get(index);
        if (message.isSend){
            return USER_TYPE;
        }
        else
            return ASSISTANT_TYPE;
    }

}
