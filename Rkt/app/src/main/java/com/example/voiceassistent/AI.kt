package com.example.voiceassistent

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.util.Log
import com.example.voiceassistent.numberapi.NumberToString
import com.example.voiceassistent.parsing.ParsingHtmlServiceHolidays.getHoliday
import com.example.voiceassistent.phoneapi.PhoneToString
import com.example.voiceassistent.weatherapi.ForecastToString
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import java.util.regex.Pattern

public object AI {
    /**
     * da6f447934a5da1b5cb4ca2d4313cda1
     * http://api.weatherstack.com/current?access_key=da6f447934a5da1b5cb4ca2d4313cda1&query=%D0%9A%D0%B8%D1%80%D0%BE%D0%B2
     * 0966eb0edb7571b52b40233d6f471892
     * Задачи:
     * расчитать кол-во дней до даты (есть 2 даты - текущая и нужная(посчитать не была ли она уже))
     * лабораторная работа 6 - не сохраняет ласт сообщение
     */
    /**
     * Получить ответ на вопрос
     * @param question вопрос
     * @param callback функциональный ответ
     * @param ctx контекст, передавать mainActivity для работы с файлами
     */
    fun getAnswer(question: String, callback: androidx.core.util.Consumer<String?>, ctx: Context?) {
        val STRING_EMPTY = ""
        val DEFAULT_ANSWER = "Ничего не нашлось. Совсем ничего. Попробуйте немного изменить вопрос."
        val WEATHER_SEARCH = "Хм... Изучаю погоду."
        val WEATHER_ERROR =
            "Не могу узнать погоду. Попробуйте ввести названиие города в именительном падеже."
        val NUMBER_GETTING_ERROR = "Ошибка получения."
        val NUMBER_ERROR = "Не удалось получить число. Попробуйте снова."
        val TRANSLATE_ERROR = "Ошибка перевода."
        val MAX_LENGTH_MESSAGE = 150
        val dictionaryCode: MutableMap<String, Int>
        dictionaryCode = HashMap()
        dictionaryCode["какой сегодня день"] = 0
        dictionaryCode["который час"] = 1
        dictionaryCode["какой день недели"] = 2
        dictionaryCode["дней до"] = 3
        dictionaryCode["привет"] = 4
        dictionaryCode["как дела"] = 5
        dictionaryCode["чем занимаешься"] = 6
        dictionaryCode["погода"] = 7
        dictionaryCode["число"] = 8
        dictionaryCode["праздник"] = 9
        dictionaryCode["номер"] = 11
        val helloPhrase =
            Arrays.asList("привет", "хеллоу", "бонжур", "только вас вспоминал", "я тут", "я здесь")
        val howAreYouPhrase =
            Arrays.asList("не плохо", "отлично", "отлично, приятно, что интересуетесь")
        val WYDphrases = Arrays.asList("отвечаю на вопросы", "перечитываю статьи в Википедии")
        val retAnswer: MutableList<String> = ArrayList()
        val answerDir: MutableMap<Int, String?> = TreeMap()
        for (entry: Map.Entry<String, Int> in dictionaryCode.entries) {
            val index = question.indexOf(entry.key)
            if (index != -1) {
                var conStr = ""
                when (entry.value) {
                    0 -> {
                        conStr += getDay(0)
                        Log.i("AI", "Today=$conStr")
                    }
                    1 -> {
                        conStr += currentTime
                        Log.i("AI", "CurrentTime=$conStr")
                    }
                    2 -> {
                        conStr += dayOfWeek
                        Log.i("AI", "DayOfWeek=$conStr")
                    }
                    3 -> {
                        conStr += getNumberOfDaysToDate(question)
                        Log.i("AI", "getNumberOfDaysToDate=$conStr")
                    }
                    4 -> {
                        conStr += randomPhrase(helloPhrase)
                        Log.i("AI", "random answer=$conStr")
                    }
                    5 -> {
                        conStr += randomPhrase(howAreYouPhrase)
                        Log.i("AI", "random answer=$conStr")
                    }
                    6 -> {
                        conStr += randomPhrase(WYDphrases)
                        Log.i("AI", "random answer=$conStr")
                    }
                    7 -> {
                        val cityPattern = Pattern.compile(
                            "погода в (городе)?(\\p{L}+[- ]*\\p{L}*[- ]*\\p{L}*)",
                            Pattern.CASE_INSENSITIVE
                        )
                        val cityMatcher = cityPattern.matcher(question)
                        if (cityMatcher.find()) {
                            var cityName = cityMatcher.group()
                            cityName = cityName.replace("погода в ".toRegex(), "")
                            cityName = cityName.replace("городе ".toRegex(), "")
                            while (cityName[cityName.length - 1] == ' ') {
                                cityName = deleteLastSymbol(cityName)
                            }
                            if (cityName[cityName.length - 1] == 'е') {
                                cityName = deleteLastSymbol(cityName)
                            }
                            Log.i("AI", "Weather city=$cityName")
                            conStr += WEATHER_SEARCH
                            ForecastToString.getForecast(
                                cityName,
                                androidx.core.util.Consumer { s ->
                                    Log.i("AI", "Weather answer=$s")
                                    callback.accept(java.lang.String.join(", ", s))
                                })
                        }
                    }
                    8 -> {
                        val numberPattern = Pattern.compile(
                            "число -?(\\d+)",
                            Pattern.CASE_INSENSITIVE
                        )
                        val numberMatcher = numberPattern.matcher(question)
                        if (numberMatcher.find()) {
                            val number = numberMatcher.group().replace("число ", "")
                            Log.i("AI", "Number=$number")
                            if (isNumeric(number)) {
                                conStr += "Число $number будет..."
                                val sing = number[0] == '-'
                                NumberToString.getNumber(
                                    number,
                                    object : androidx.core.util.Consumer<String?> {
                                        override fun accept(t: String?) {
                                            var s = t
                                            if (sing && s != NUMBER_GETTING_ERROR) s = "минус $s"
                                            Log.i("AI", "Number answer=$s")
                                            if (s!!.length > 150) {
                                                val list = transformAnswer(s)
                                                list.forEach(java.util.function.Consumer { el: String? ->
                                                    callback.accept(
                                                        java.lang.String.join(", ", el)
                                                    )
                                                })
                                            } else callback.accept(java.lang.String.join(", ", s))
                                        }
                                    })
                            } else {
                                conStr += NUMBER_ERROR
                            }
                        }
                    }
                    9 -> {
                        val dateList = getDate(
                            question, Pattern.compile(
                                "праздник ((0?[1-9]|[12][0-9]|3[01]) (янв(?:аря)?|фев(?:раля)?|мар(?:та)?|апр(?:еля)?|мая|июн(?:я)?|июл(?:я)?|авг(?:уста)?|сен(?:тября)?|окт(?:ября)?|ноя(?:бря)?|дек(?:абря)?) \\d{4})?(сегодня?|завтра?|вчера?)?",
                                Pattern.CASE_INSENSITIVE
                            ), "праздник "
                        )
                        if (dateList.size == 1) {
                            conStr += "Ищу праздник..."
                        } else if (dateList.size > 1) {
                            conStr += "Ищу праздники..."
                        }
                        if (dateList.size > 0) {
                            Observable.fromCallable<List<String>>(
                                {
                                    val answer: MutableList<String> = ArrayList()
                                    var i: Int = 0
                                    while (i < dateList.size) {
                                        Log.i("AI", "dateList=" + dateList.get(i))
                                        val answerList: List<String> =
                                            getHoliday(dateList.get(i), (ctx)!!)
                                        val answerString: StringBuilder = StringBuilder()
                                        var j: Int = 0
                                        while (j < answerList.size) {
                                            answerString.append(answerList.get(j)).append(". ")
                                            ++j
                                        }
                                        answer.add(
                                            dateList.get(i)
                                                .toString() + " - " + answerString.toString()
                                        )
                                        ++i
                                    }
                                    answer
                                }).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ result: List<String> ->
                                    result.forEach(
                                        java.util.function.Consumer { el: String? ->
                                            callback.accept(
                                                java.lang.String.join(", ", el)
                                            )
                                        })
                                })
                        }
                    }
                    10 -> {
                    }
                    11 -> {
                        val phonePattern = Pattern.compile(
                            "номер ([7-9]{1})([0-9]{9})",
                            Pattern.CASE_INSENSITIVE
                        )
                        val phoneMatcher = phonePattern.matcher(question)
                        if (phoneMatcher.find()) {
                            val number = phoneMatcher.group().replace("номер ", "")
                            Log.i("AI", "Number=$number")
                            conStr += "Номер $number..."
                            PhoneToString.getNumber(
                                number,
                                object : androidx.core.util.Consumer<String?> {
                                    override fun accept(s: String?) {
                                        callback.accept(java.lang.String.join(", ", s))
                                    }
                                })
                        }
                    }
                    else -> Log.e("AI", "Error. Case " + entry.value)
                }
                answerDir[index] = conStr
            }
        }
        var ret = STRING_EMPTY
        for (entry: Map.Entry<Int, String?> in answerDir.entries) {
            if (!entry.value!!.isEmpty() || entry.value != null) {
                if (ret != STRING_EMPTY && (ret + entry.value).length >= MAX_LENGTH_MESSAGE) {
                    retAnswer.add(ret)
                    ret = STRING_EMPTY
                }
                if (entry.value != STRING_EMPTY) ret += entry.value.toString() + ". "
            }
        }
        retAnswer.add(ret)
        for (i in retAnswer.indices) {
            val callStr = if ((retAnswer[i].isEmpty())) DEFAULT_ANSWER else changeStringWithPunc(
                retAnswer[i]
            )
            callback.accept(java.lang.String.join(", ", callStr))
            Log.e("AI", callback.toString())
        }
    }

    /**
     * Преобразование ответа к виду "как в предложении"
     * @param str исходная строка
     * @return
     */
    fun changeStringWithPunc(str: String?): String {
        val stringBuffer = StringBuilder(str)
        var t = false
        val symbols = ".?!"
        for (i in 0 until stringBuffer.length) {
            val elem = stringBuffer[i]
            if (!t && Character.isAlphabetic(elem.toInt())) { //пред символ знак и элемент - буква
                stringBuffer.setCharAt(i, Character.toUpperCase(elem)) //Заглавный
                t = true
            } else if (symbols.contains(elem.toString())) //если знак, запоминаем
                t = false
        }
        return stringBuffer.toString()
    }

    /**
     * Получить нужный падеж слова
     * @param count число
     * @param str1 мн.ч Родительный падеж
     * @param str2 ед.ч Именительный падеж
     * @param str3 ед.ч Родительный падеж
     * @return
     */
    fun getWord(
        count: Int,
        str1: String,
        str2: String,
        str3: String
    ): String { //"градусов", "градус", " градуса"
        var n = count % 100
        if (n < 11 || n > 14) {
            n = count % 10
            if (n == 1) return str2
            if (n >= 2 && n <= 4) return str3
        }
        return str1
    }

    /**
     * Удалить все ссылки [число] и (слова) из предложения
     * @param text
     * @return
     */
    fun removeLinks(text: String): String {
        var text = text
        val pattern = Pattern.compile("\\[\\d+\\]")
        val matcher = pattern.matcher(text)
        while (matcher.find()) {
            val find = matcher.group()
            text = text.replace(find, "")
        }
        val pattern2 = Pattern.compile("\\(.+\\) \\— ", Pattern.CASE_INSENSITIVE)
        val matcher2 = pattern2.matcher(text)
        while (matcher2.find()) {
            val find = matcher2.group()
            val pattern1 = Pattern.compile("\\(.+\\)", Pattern.CASE_INSENSITIVE)
            val matcher1 = pattern1.matcher(find)
            if (matcher1.find()) {
                text = text.replace(matcher1.group(), "")
            } else text = text.replace(find, "")
        }
        return text
    }

    /**
     * Усекает строку, оставляя только первое предложение
     * @param str
     * @return
     */
    fun getTruncatedString(str: String): String {
        var str = str
        val symbols = ".?!"
        for (i in 0 until str.length) {
            val elem = str[i]
            if (symbols.contains(elem.toString())) {
                str = str.substring(0, i + 1)
                break
            }
        }
        return str
    }

    /**
     * Проверяет, является ли строка числом типа Long
     * @param str
     * @return
     */
    private fun isNumeric(str: String): Boolean {
        var ret = false
        try {
            str.toLong()
            ret = true
        } catch (ignored: NumberFormatException) {
        }
        Log.w("isNumeric", java.lang.Boolean.toString(ret))
        return ret
    }

    /**
     * Получить лист дат из строки
     * @param str исходная строка
     * @param pattern паттерн, используемый в поиске даты
     * @param replaceStr строка, удаляемая из паттерна
     * @return список строк
     */
    private fun getDate(str: String, pattern: Pattern, replaceStr: String): List<String> {
        val result: MutableList<String> = ArrayList()
        val matcher = pattern.matcher(str)
        while (matcher.find()) {
            val dateHoliday = matcher.group().replace(replaceStr, "")
            var addStr = ""
            when (dateHoliday) {
                "" -> addStr += ""
                "сегодня" -> addStr += getDay(0)
                "завтра" -> addStr += getDay(1)
                "вчера" -> addStr += getDay(-1)
                else -> {
                    Log.d("AI", "getDate:dateHoliday=$dateHoliday")
                    val myMap: Map<String?, String?> = object : HashMap<String?, String?>() {
                        init {
                            put("янв", "аря")
                            put("фев", "раля")
                            put("мар", "та")
                            put("апр", "еля")
                            put("июн", "я")
                            put("июл", "я")
                            put("авг", "уста")
                            put("сен", "тября")
                            put("окт", "ября")
                            put("ноя", "бря")
                            put("дек", "абря")
                        }
                    }
                    val words: Array<String?> = dateHoliday.split(" ").toTypedArray()
                    if (myMap.containsKey(words[1])) {
                        words[1] += myMap[words[1]]
                        addStr += words[0].toString() + " " + words[1] + " " + words[2]
                    } else {
                        addStr += dateHoliday
                    }
                    Log.d("AI", "getDate:addStr=$addStr")
                }
            }
            Log.i("AI", "date=$addStr")
            if (addStr != "") result.add(addStr)
        }
        return result
    }

    /**
     * Получить день с +amount от текущего
     * @param amount число дней от текущего числа
     * @return
     */
    private fun getDay(amount: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, amount)
        return SimpleDateFormat("d MMMM yyyy", MainActivity.LANGUAGE).format(cal.time)
    }

    /**
     * @return текущее время
     */
    private val currentTime: String
        private get() {
            val cal = Calendar.getInstance()
            return SimpleDateFormat("HH:mm", MainActivity.LANGUAGE).format(cal.time)
        }

    /**
     * @return текущий день недели
     */
    private val dayOfWeek: String
        private get() {
            val cal = Calendar.getInstance()
            return SimpleDateFormat("EEEE", MainActivity.LANGUAGE).format(cal.time)
        }

    /**
     * Получить кол-во дней до даты
     * @param que
     * @return число дней
     */
    private fun getNumberOfDaysToDate(que: String): String {
        val stringList = calculateNumberOfDays(que)
        val resultString = StringBuilder()
        if (stringList.size != 0) {
            for (i in stringList.indices) {
                val num = stringList[i]
                if (num < 0) resultString.append("Не могу посчитать.") else if (num == 0) resultString.append(
                    "Сегодня этот день."
                ) else resultString.append(
                    num.toString() + " " + getWord(
                        stringList[i], "дней", "день", "дня"
                    ) + "."
                )
            }
        }
        return if (resultString.length > 0 && resultString.get(resultString.length - 1) == '.') deleteLastSymbol(
            resultString.toString()
        ) else resultString.toString()
    }

    /**
     * Рассчитать кол-во дней
     * @param str
     * @return список кол-ва дней до дат
     */
    private fun calculateNumberOfDays(str: String): List<Int> {
        val result: MutableList<Int> = ArrayList()
        val date = getDate(
            str, Pattern.compile(
                "дней до ((0?[1-9]|[12][0-9]|3[01]) (янв(?:аря)?|фев(?:раля)?|мар(?:та)?|апр(?:еля)?|мая|июн(?:я)?|июл(?:я)?|авг(?:уста)?|сен(?:тября)?|окт(?:ября)?|ноя(?:бря)?|дек(?:абря)?) \\d{4})",
                Pattern.CASE_INSENSITIVE
            ), "дней до"
        )
        val today = getStringAsDate(getDay(0))
        for (i in date.indices) {
            var count = -1
            val dateSearch = getStringAsDate(date[i])
            if (dateSearch != null) {
                count = daysBetween(today, dateSearch)
            }
            result.add(count)
        }
        return result
    }

    private fun getStringAsDate(str: String): Date? {
        try {
            val inputFormat = SimpleDateFormat("d MMMM yyyy", MainActivity.LANGUAGE)
            val cal = Calendar.getInstance()
            cal.time = inputFormat.parse(str)
            return cal.time
        } catch (ex: Exception) {
            Log.e("getSAD", ex.toString())
        }
        return null
    }

    private fun daysBetween(d1: Date?, d2: Date): Int {
        return ((d2.time - d1!!.time) / (1000 * 60 * 60 * 24)).toInt()
    }

    /**
     * Случайная фраза из списка
     * @param list список фраз
     * @return
     */
    private fun randomPhrase(list: List<String>): String {
        return list[(Math.random() * list.size).toInt()]
    }

    /**
     * Удалить последний символ
     * @param str
     * @return
     */
    fun deleteLastSymbol(str: String): String {
        return str.replaceFirst(".$".toRegex(), "")
    }

    /**
     * Преобразовать длинный ответ на несколько небольших
     * @param str
     * @return список фраз
     */
    private fun transformAnswer(str: String): List<String> {
        var str = str
        val list: MutableList<String> = ArrayList()
        val ch = str.length / 150 + 1
        Log.e("ch = ", str.length.toString() + " " + 150)
        val sizeCh = str.length / ch
        for (i in 0 until ch - 1) {
            var j = if ((i == 0)) (i + 1) * sizeCh else i * sizeCh
            while (str[j] != ' ') {
                j--
            }
            list.add(str.substring(0, j))
            str = str.substring(j + 1)
        }
        list.add(str)
        return list
    }
}