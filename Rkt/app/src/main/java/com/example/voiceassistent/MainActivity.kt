package com.example.voiceassistent

import android.content.ContentValues
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.util.Consumer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voiceassistent.AI.getAnswer
import com.example.voiceassistent.messageview.Message
import com.example.voiceassistent.messageview.MessageEntity
import com.example.voiceassistent.messageview.MessageListAdapter
import java.util.*

class MainActivity : AppCompatActivity() {
    protected var sendButton: Button? = null
    protected var questionText: EditText? = null
    protected var chatMessageList: RecyclerView? = null
    var sPref: SharedPreferences? = null
    protected var messageListAdapter: MessageListAdapter? = null
    protected var textToSpeech: TextToSpeech? = null
    var dBHelper: DBHelper? = null
    var database: SQLiteDatabase? = null
    var cursor: Cursor? = null
    override fun onDestroy() {
        if (textToSpeech != null) {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
        database!!.close()
        dBHelper!!.close()
        Log.i("LOG", "onDestroy")
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("array", messageListAdapter!!.messageList)
        Log.d("LOG", "SaveState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        messageListAdapter!!.messageList.addAll(savedInstanceState.getSerializable("array") as Collection<out Message>)
        Log.i("LOG", "RestoreState")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.day_settings -> {
                //установка дневной темы
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
                isLight = true
                Log.i("ItemSelected", "day theme")
            }
            R.id.night_settings -> {
                //установка ночной темы
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
                isLight = false
                Log.i("ItemSelected", "night theme")
            }
            R.id.clear_dialog -> messageListAdapter?.messageList?.clear()
            else -> Log.e("ItemSelected", "Error itemId=" + item.itemId)
        }
        chatMessageList?.scrollToPosition(messageListAdapter!!.messageList!!.size)
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        Log.i("LOG", "finish")
    }

    override fun onStop() {
        val editor = sPref!!.edit()
        editor.putBoolean(THEME, isLight)
        editor.apply()
        database!!.delete(DBHelper.TABLE_MESSAGES, null, null)
        for (i in messageListAdapter!!.messageList.indices) {
            val entity = MessageEntity(messageListAdapter!!.messageList[i])
            val contentValues = ContentValues()
            contentValues.put(DBHelper.FIELD_MESSAGE, entity.text)
            contentValues.put(DBHelper.FIELD_SEND, entity.isSend)
            contentValues.put(DBHelper.FIELD_DATE, entity.date)
            database!!.insert(DBHelper.TABLE_MESSAGES, null, contentValues)
        }
        Log.i("LOG", "onStop")
        super.onStop()
    }

    private var isLight = true
    private val THEME = "THEME"
    override fun onCreate(savedInstanceState: Bundle?) {
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        isLight = sPref!!.getBoolean(THEME, true)
        if (isLight) delegate.localNightMode =
            AppCompatDelegate.MODE_NIGHT_NO else delegate.localNightMode =
            AppCompatDelegate.MODE_NIGHT_YES
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendButton = findViewById(R.id.sendButton)
        questionText = findViewById(R.id.questionField)
        chatMessageList = findViewById(R.id.chatMessageList)
        messageListAdapter = MessageListAdapter()
        chatMessageList!!.setLayoutManager(LinearLayoutManager(this))
        chatMessageList!!.setAdapter(messageListAdapter)
        dBHelper = DBHelper(this)
        database = dBHelper!!.getWritableDatabase()
        if (savedInstanceState == null) {
            Log.i("onCreate", "RestoreDialog")
            cursor = database!!.query(DBHelper.TABLE_MESSAGES, null, null, null, null, null, null)
            if (cursor!!.moveToFirst()) {
                val messageIndex = cursor!!.getColumnIndex(DBHelper.FIELD_MESSAGE)
                val dateIndex = cursor!!.getColumnIndex(DBHelper.FIELD_DATE)
                val sendIndex = cursor!!.getColumnIndex(DBHelper.FIELD_SEND)
                do {
                    val entity = MessageEntity(
                        cursor!!.getString(messageIndex),
                        cursor!!.getString(dateIndex), cursor!!.getInt(sendIndex)
                    )
                    val message = Message(entity)
                    if (message.isSend == null || message.date == null || message.text == null) {
                        //
                    } else {
                        messageListAdapter!!.messageList!!.add(message)
                    }
                } while (cursor!!.moveToNext())
            }
            cursor!!.close()
        }
        sendButton!!.setOnClickListener(View.OnClickListener { onSend() })
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech!!.setLanguage(LANGUAGE)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported")
                } else {
                    Log.d("TTS", "Ready")
                }
            } else {
                Log.e("TTS", "Failed")
            }
        }
        Log.i("LOG", "onCreate")
    }

    protected fun onSend() {
        var text = questionText!!.getText().toString()
        if (text == "") {
            Toast.makeText(this, R.string.toastError, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, R.string.toastWaitAnswer, Toast.LENGTH_SHORT).show()
            if (text.length > 150) {
                text = text.substring(0, 150) + THREE_DOTS
            }
            messageListAdapter!!.messageList!!.add(Message(text, true))
            Log.i("Dialog", "question = $text")
            getAnswer(text.toLowerCase(), Consumer { answer ->
                speak(answer)
                messageListAdapter!!.messageList!!.add(Message(answer ?: "", false))
                chatMessageList!!.scrollToPosition(messageListAdapter!!.messageList!!.size - 1)
                Log.i("Dialog", "answer = $answer")
            }, this)
            chatMessageList!!.scrollToPosition(messageListAdapter!!.messageList!!.size - 1)
            questionText!!.setText("")
        }
    }

    protected fun speak(text: String?) {
        //textToSpeech.setPitch(0.5f);
        //textToSpeech.setSpeechRate(0.5f);
        textToSpeech!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    companion object {
        var LANGUAGE = Locale("ru")
        const val APP_PREFERENCES = "mysettings"
        protected var THREE_DOTS = "..."
    }
}