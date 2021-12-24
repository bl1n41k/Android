package com.example.voiceassistent;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voiceassistent.messageview.Message;
import com.example.voiceassistent.messageview.MessageEntity;
import com.example.voiceassistent.messageview.MessageListAdapter;

import java.util.Collection;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    protected Button sendButton;
    protected EditText questionText;
    protected RecyclerView chatMessageList;
    SharedPreferences sPref;
    protected MessageListAdapter messageListAdapter;
    protected TextToSpeech textToSpeech;
    protected static Locale LANGUAGE = new Locale("ru");
    public static final String APP_PREFERENCES = "mysettings";
    DBHelper dBHelper;
    SQLiteDatabase database;
    Cursor cursor;

    @Override
    protected void onDestroy() {
        if (textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        database.close();
        dBHelper.close();
        Log.i("LOG", "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("array", messageListAdapter.messageList);
        Log.d("LOG", "SaveState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        messageListAdapter.messageList.addAll((Collection<? extends Message>) savedInstanceState.getSerializable("array"));
        Log.i("LOG", "RestoreState");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.day_settings:
                //установка дневной темы
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                isLight = true;
                Log.i("ItemSelected", "day theme");
                break;
            case R.id.night_settings:
                //установка ночной темы
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                isLight = false;
                Log.i("ItemSelected", "night theme");
                break;
            case R.id.clear_dialog:
                messageListAdapter.messageList.clear();

                break;
            default:
                Log.e("ItemSelected", "Error itemId="+item.getItemId());
                break;
        }
        chatMessageList.scrollToPosition(messageListAdapter.messageList.size());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish(){
        Log.i("LOG", "finish");
    }

    @Override
    protected void onStop() {

        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(THEME, isLight);
        editor.apply();

        database.delete(dBHelper.TABLE_MESSAGES, null, null);
        for (int i = 0; i < messageListAdapter.messageList.size(); ++i){
            MessageEntity entity = new MessageEntity(messageListAdapter.messageList.get(i));
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.FIELD_MESSAGE, entity.text);
            contentValues.put(DBHelper.FIELD_SEND, entity.isSend);
            contentValues.put(DBHelper.FIELD_DATE, entity.date);
            database.insert(dBHelper.TABLE_MESSAGES,null,contentValues);
        }
        Log.i("LOG", "onStop");
        super.onStop();
    }

    private boolean isLight = true;
    private String THEME = "THEME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        isLight = sPref.getBoolean(THEME, true);
        if (isLight)
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        sendButton = findViewById(R.id.sendButton);
        questionText = findViewById(R.id.questionField);
        chatMessageList = findViewById(R.id.chatMessageList);
        messageListAdapter = new MessageListAdapter();
        chatMessageList.setLayoutManager(new LinearLayoutManager(this));
        chatMessageList.setAdapter(messageListAdapter);
        dBHelper = new DBHelper(this);
        database = dBHelper.getWritableDatabase();

        if (savedInstanceState == null){
            Log.i("onCreate", "RestoreDialog");
            cursor = database.query(dBHelper.TABLE_MESSAGES, null, null, null, null, null, null);
            if (cursor.moveToFirst()){
                int messageIndex = cursor.getColumnIndex(dBHelper.FIELD_MESSAGE);
                int dateIndex = cursor.getColumnIndex(dBHelper.FIELD_DATE);
                int sendIndex = cursor.getColumnIndex(dBHelper.FIELD_SEND);

                do{
                    MessageEntity entity = new MessageEntity(cursor.getString(messageIndex),
                            cursor.getString(dateIndex), cursor.getInt(sendIndex));
                    Message message = new Message(entity);
                    if (message.isSend == null || message.date == null || message.text == null){
                        //
                    }
                    else {
                        messageListAdapter.messageList.add(message);
                    }
                }while (cursor.moveToNext());
            }
            cursor.close();
        }


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSend();
            }
        });

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = textToSpeech.setLanguage(LANGUAGE);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("TTS", "Language not supported");
                    }
                    else {
                        Log.d("TTS", "Ready");
                    }
                }
                else {
                    Log.e("TTS", "Failed");
                }
            }
        });

        Log.i("LOG", "onCreate");
    }


    protected static String THREE_DOTS = "...";
    protected void onSend() {
        String text = questionText.getText().toString();

        if (text.equals("")) {
            Toast.makeText(this, R.string.toastError , Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, R.string.toastWaitAnswer, Toast.LENGTH_SHORT).show();
            if (text.length() > AI.MAX_LENGTH_MESSAGE){
                text = text.substring(0, AI.MAX_LENGTH_MESSAGE) + THREE_DOTS;
            }
            messageListAdapter.messageList.add(new Message(text, true));
            Log.i("Dialog", "question = " + text);
            AI.getAnswer(text.toLowerCase(), new Consumer<String>() {
                @Override
                public void accept(String answer) {
                    speak(answer);
                    messageListAdapter.messageList.add(new Message(answer, false));
                    chatMessageList.scrollToPosition(messageListAdapter.messageList.size()-1);
                    Log.i("Dialog", "answer = " + answer);
                }
            }, this);
            chatMessageList.scrollToPosition(messageListAdapter.messageList.size()-1);
            questionText.setText("");
        }
    }

    protected void speak(String text){
        //textToSpeech.setPitch(0.5f);
        //textToSpeech.setSpeechRate(0.5f);
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

}