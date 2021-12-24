package com.example.voiceassistent.messageview;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voiceassistent.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    protected TextView messageText;
    protected TextView messageDate;


    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.messageTextView);
        messageDate = itemView.findViewById(R.id.messageDateView);

    }

    /**
     * положить данные из модели в соответствующие текстовые поля
     */
    public void bind(Message message) {
        messageText.setText(message.text);
        DateFormat fmt = new SimpleDateFormat("");
        messageDate.setText(fmt.format(message.date));
    }


}
