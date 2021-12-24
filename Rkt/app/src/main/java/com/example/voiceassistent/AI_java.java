/*
package com.example.voiceassistent;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.util.Log;

import androidx.core.util.Consumer;

import com.example.voiceassistent.numberapi.NumberToString;
import com.example.voiceassistent.parsing.ParsingHtmlServiceHolidays;
import com.example.voiceassistent.phoneapi.PhoneToString;
import com.example.voiceassistent.weatherapi.ForecastToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AI_java {
    */
/**
     * da6f447934a5da1b5cb4ca2d4313cda1
     * http://api.weatherstack.com/current?access_key=da6f447934a5da1b5cb4ca2d4313cda1&query=%D0%9A%D0%B8%D1%80%D0%BE%D0%B2
     * 0966eb0edb7571b52b40233d6f471892
     * Задачи:
     * расчитать кол-во дней до даты (есть 2 даты - текущая и нужная(посчитать не была ли она уже))
     * лабораторная работа 6 - не сохраняет ласт сообщение
     **//*


    public static final String STRING_EMPTY = "";
    public static final String DEFAULT_ANSWER = "Ничего не нашлось. Совсем ничего. Попробуйте немного изменить вопрос.";
    public static final String WEATHER_SEARCH = "Хм... Изучаю погоду.";
    public static final String WEATHER_ERROR = "Не могу узнать погоду. Попробуйте ввести названиие города в именительном падеже.";
    public static final String NUMBER_GETTING_ERROR = "Ошибка получения.";
    public static final String NUMBER_ERROR = "Не удалось получить число. Попробуйте снова.";
    public static final String TRANSLATE_ERROR = "Ошибка перевода.";
    public static final int MAX_LENGTH_MESSAGE = 150;

    static Map<String, Integer> dictionaryCode;
    static {
        dictionaryCode = new HashMap<>();
        dictionaryCode.put("какой сегодня день", 0);
        dictionaryCode.put("который час", 1);
        dictionaryCode.put("какой день недели", 2);
        dictionaryCode.put("дней до", 3);
        dictionaryCode.put("привет", 4);
        dictionaryCode.put("как дела", 5);
        dictionaryCode.put("чем занимаешься", 6);
        dictionaryCode.put("погода", 7);
        dictionaryCode.put("число", 8);
        dictionaryCode.put("праздник", 9);
        dictionaryCode.put("номер", 11);
    }
    static List<String> helloPhrase = Arrays.asList("привет", "хеллоу", "бонжур", "только вас вспоминал", "я тут", "я здесь");
    static List<String> howAreYouPhrase = Arrays.asList("не плохо", "отлично", "отлично, приятно, что интересуетесь");
    static List<String> WYDphrases = Arrays.asList("отвечаю на вопросы", "перечитываю статьи в Википедии");


    */
/**
     * Получить ответ на вопрос
     * @param question вопрос
     * @param callback функциональный ответ
     * @param ctx контекст, передавать mainActivity для работы с файлами
     *//*

    public static void getAnswer(String question, final Consumer<String> callback, Context ctx) {
        List<String> retAnswer = new ArrayList<>();
        Map<Integer, String> answerDir = new TreeMap<>();
        for (Map.Entry<String, Integer> entry : dictionaryCode.entrySet()) {
            int index = question.indexOf(entry.getKey());
            if (index != -1) {
                String conStr = "";
                switch (entry.getValue()) {
                    case 0:
                        conStr += getDay(0);
                        Log.i("AI", "Today=" + conStr);
                        break;
                    case 1:
                        conStr += getCurrentTime();
                        Log.i("AI", "CurrentTime=" + conStr);
                        break;
                    case 2:
                        conStr += getDayOfWeek();
                        Log.i("AI", "DayOfWeek=" + conStr);
                        break;
                    case 3:
                        conStr += getNumberOfDaysToDate(question);
                        Log.i("AI", "getNumberOfDaysToDate=" + conStr);
                        break;
                    case 4:
                        conStr += randomPhrase(helloPhrase);
                        Log.i("AI", "random answer=" + conStr);
                        break;
                    case 5:
                        conStr += randomPhrase(howAreYouPhrase);
                        Log.i("AI", "random answer=" + conStr);
                        break;
                    case 6:
                        conStr += randomPhrase(WYDphrases);
                        Log.i("AI", "random answer=" + conStr);
                        break;
                    case 7:
                        Pattern cityPattern = Pattern.compile("погода в (городе)?(\\p{L}+[- ]*\\p{L}*[- ]*\\p{L}*)",
                                Pattern.CASE_INSENSITIVE);
                        Matcher cityMatcher = cityPattern.matcher(question);
                        if (cityMatcher.find()){
                            String cityName = cityMatcher.group();
                            cityName = cityName.replaceAll("погода в ", "");
                            cityName = cityName.replaceAll("городе ", "");
                            while (cityName.charAt(cityName.length() - 1) == ' '){
                                cityName = deleteLastSymbol(cityName);
                            }
                            if (cityName.charAt(cityName.length() - 1) == 'е'){
                                cityName = deleteLastSymbol(cityName);
                            }

                            Log.i("AI", "Weather city="+cityName);
                            conStr += WEATHER_SEARCH;
                            ForecastToString.getForecast(cityName, new Consumer<String>() {
                                @Override
                                public void accept(String s) {
                                    Log.i("AI", "Weather answer=" + s);
                                    callback.accept(String.join(", ", s));
                                }
                            });
                        }
                        break;
                    case 8:
                        Pattern numberPattern = Pattern.compile("число -?(\\d+)",
                                Pattern.CASE_INSENSITIVE);
                        Matcher numberMatcher = numberPattern.matcher(question);
                        if (numberMatcher.find()){
                            String number = numberMatcher.group().replace("число ", "");
                            Log.i("AI", "Number=" + number);
                            if (isNumeric(number)) {
                                conStr += "Число " + number + " будет...";
                                boolean sing = number.charAt(0) == '-';
                                NumberToString.getNumber(number, new Consumer<String>() {
                                    @Override
                                    public void accept(String s) {
                                        if (sing && !s.equals(AI_java.NUMBER_GETTING_ERROR))
                                            s = "минус " + s;
                                        Log.i("AI", "Number answer=" + s);
                                        if (s.length() > AI_java.MAX_LENGTH_MESSAGE){
                                            List<String> list = transformAnswer(s);
                                            list.forEach(el ->{
                                                callback.accept(String.join(", ", el));
                                            });
                                        }
                                        else
                                            callback.accept(String.join(", ", s));
                                    }
                                });
                            }
                            else {
                                conStr += NUMBER_ERROR;
                            }
                        }
                        break;
                    case 9:
                        List<String> dateList = getDate(question, Pattern.compile("праздник ((0?[1-9]|[12][0-9]|3[01]) (янв(?:аря)?|фев(?:раля)?|мар(?:та)?|апр(?:еля)?|мая|июн(?:я)?|июл(?:я)?|авг(?:уста)?|сен(?:тября)?|окт(?:ября)?|ноя(?:бря)?|дек(?:абря)?) \\d{4})?(сегодня?|завтра?|вчера?)?",
                                Pattern.CASE_INSENSITIVE), "праздник ");
                        if (dateList.size() == 1) {
                            conStr += "Ищу праздник...";
                        }else if (dateList.size() > 1){
                            conStr += "Ищу праздники...";
                        }
                        if  (dateList.size() > 0) {
                            Observable.fromCallable(() -> {
                                List<String> answer = new ArrayList<>();
                                for (int i = 0; i < dateList.size(); ++i) {
                                    Log.i("AI", "dateList=" + dateList.get(i));
                                    List<String> answerList = ParsingHtmlServiceHolidays.getHoliday(dateList.get(i), ctx);
                                    StringBuilder answerString = new StringBuilder();
                                    for (int j = 0; j < answerList.size(); ++j) {
                                        answerString.append(answerList.get(j)).append(". ");
                                    }
                                    answer.add(dateList.get(i) + " - " + answerString.toString());
                                }
                                return answer;
                            }).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe((result) -> {
                                        result.forEach(el -> {
                                            callback.accept(String.join(", ", el));
                                        });
                                    });
                        }
                        break;
                    case 10:
                        break;
                    case 11:
                        Pattern phonePattern = Pattern.compile("номер ([7-9]{1})([0-9]{9})",
                                Pattern.CASE_INSENSITIVE);
                        Matcher phoneMatcher = phonePattern.matcher(question);
                        if (phoneMatcher.find()){
                            String number = phoneMatcher.group().replace("номер ", "");
                            Log.i("AI", "Number=" + number);
                            conStr += "Номер " + number + "...";
                            PhoneToString.getNumber(number, new Consumer<String>() {
                                @Override
                                public void accept(String s) {
                                    callback.accept(String.join(", ", s));
                                }
                            });
                        }
                        break;
                    default:
                        Log.e("AI", "Error. Case " + entry.getValue());
                        break;
                }
                answerDir.put(index, conStr);
            }
        }

        String ret = STRING_EMPTY;
        for (Map.Entry<Integer, String> entry : answerDir.entrySet()) {
            if (!entry.getValue().isEmpty() || entry.getValue() != null) {
                if (!ret.equals(STRING_EMPTY) && (ret + entry.getValue()).length() >= MAX_LENGTH_MESSAGE) {
                    retAnswer.add(ret);
                    ret = STRING_EMPTY;
                }
                if (!entry.getValue().equals(STRING_EMPTY))
                    ret += entry.getValue() + ". ";
            }
        }
        retAnswer.add(ret);

        for (int i = 0; i < retAnswer.size(); ++i){
            String callStr = (retAnswer.get(i).isEmpty()) ? DEFAULT_ANSWER
                    : changeStringWithPunc(retAnswer.get(i));
            callback.accept(String.join(", ", callStr));
            Log.e("AI", callback.toString());
        }

    }


    */
/**
     * Преобразование ответа к виду "как в предложении"
     * @param str исходная строка
     * @return
     *//*

    public static String changeStringWithPunc(String str){
        StringBuilder stringBuffer = new StringBuilder(str);
        boolean t = false;
        final String symbols = ".?!";
        for (int i = 0; i < stringBuffer.length(); ++i) {
            char elem = stringBuffer.charAt(i);
            if (!t && Character.isAlphabetic(elem)) {//пред символ знак и элемент - буква
                stringBuffer.setCharAt(i, Character.toUpperCase(elem));//Заглавный
                t = true;
            } else if (symbols.contains(String.valueOf(elem))) //если знак, запоминаем
                t = false;
        }
        return stringBuffer.toString();
    }

    */
/**
     * Получить нужный падеж слова
     * @param count число
     * @param str1 мн.ч Родительный падеж
     * @param str2 ед.ч Именительный падеж
     * @param str3 ед.ч Родительный падеж
     * @return
     *//*

    public static String getWord(int count, String str1, String str2, String str3) { //"градусов", "градус", " градуса"
        int n = count % 100;
        if(n < 11 || n > 14){
            n = count % 10;
            if(n == 1)
                return str2;
            if(n >= 2 && n <= 4)
                return str3;
        }
        return str1;
    }

    */
/**
     * Удалить все ссылки [число] и (слова) из предложения
     * @param text
     * @return
     *//*

    public static String removeLinks(String text){
        Pattern pattern = Pattern.compile("\\[\\d+\\]");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()){
            String find = matcher.group();
            text = text.replace(find, "");
        }

        Pattern pattern2 = Pattern.compile("\\(.+\\) \\— ", Pattern.CASE_INSENSITIVE);
        Matcher matcher2 = pattern2.matcher(text);
        while (matcher2.find()){
            String find = matcher2.group();
            Pattern pattern1= Pattern.compile("\\(.+\\)",  Pattern.CASE_INSENSITIVE);
            Matcher matcher1 = pattern1.matcher(find);

            if (matcher1.find()){
                text = text.replace(matcher1.group(), "");
            }
            else
                text = text.replace(find, "");

        }
        return text;
    }

    */
/**
     * Усекает строку, оставляя только первое предложение
     * @param str
     * @return
     *//*

    public static String getTruncatedString(String str){
        final String symbols = ".?!";
        for (int i = 0; i < str.length(); ++i) {
            char elem = str.charAt(i);
            if (symbols.contains(String.valueOf(elem))){
                str = str.substring(0, i+1);
                break;
            }
        }
        return str;
    }

    */
/**
     * Проверяет, является ли строка числом типа Long
     * @param str
     * @return
     *//*

    private static boolean isNumeric(String str) {
        boolean ret = false;
        try {
            Long.parseLong(str);
            ret = true;
        } catch(NumberFormatException ignored){ }
        Log.w("isNumeric", Boolean.toString(ret));
        return ret;
    }

    */
/**
     * Получить лист дат из строки
     * @param str исходная строка
     * @param pattern паттерн, используемый в поиске даты
     * @param replaceStr строка, удаляемая из паттерна
     * @return список строк
     *//*

    private static List<String> getDate(String str, Pattern pattern, String replaceStr){
        List<String> result = new ArrayList<>();
        Matcher matcher = pattern.matcher(str);
        while(matcher.find()) {
            String dateHoliday = matcher.group().replace(replaceStr, "");
            String addStr = "";
            switch (dateHoliday){
                case "":
                    addStr += "";
                    break;
                case "сегодня":
                    addStr += getDay(0);
                    break;
                case "завтра":
                    addStr += getDay(1);
                    break;
                case "вчера":
                    addStr += getDay(-1);
                    break;
                default:
                    Log.d("AI", "getDate:dateHoliday=" + dateHoliday );
                    Map<String, String> myMap = new HashMap<String, String>() {{
                        put("янв", "аря");
                        put("фев", "раля");
                        put("мар", "та");
                        put("апр", "еля");
                        put("июн", "я");
                        put("июл", "я");
                        put("авг", "уста");
                        put("сен", "тября");
                        put("окт", "ября");
                        put("ноя", "бря");
                        put("дек", "абря");
                    }};
                    String[] words = dateHoliday.split(" ");

                    if (myMap.containsKey(words[1])) {
                        words[1] += myMap.get(words[1]);
                        addStr += words[0] + " " + words[1] + " " + words[2];
                    }
                    else {
                        addStr += dateHoliday;
                    }
                    Log.d("AI", "getDate:addStr=" + addStr);
                    break;
            }
            Log.i("AI",  "date="+addStr);
            if (!addStr.equals(STRING_EMPTY))
                result.add(addStr);
        }
        return result;
    }

    */
/**
     * Получить день с +amount от текущего
     * @param amount число дней от текущего числа
     * @return
     *//*

    private static String getDay(int amount){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, amount);
        return new SimpleDateFormat("d MMMM yyyy", MainActivity.LANGUAGE).format(cal.getTime());
    }

    */
/**
     * @return текущее время
     *//*

    private static String getCurrentTime(){
        Calendar cal = Calendar.getInstance();
        return new SimpleDateFormat("HH:mm", MainActivity.LANGUAGE).format(cal.getTime());
    }

    */
/**
     * @return текущий день недели
     **//*

    private static String getDayOfWeek(){
        Calendar cal = Calendar.getInstance();
        return new SimpleDateFormat("EEEE", MainActivity.LANGUAGE).format(cal.getTime());
    }

    */
/**
     * Получить кол-во дней до даты
     * @param que
     * @return число дней
     *//*

    private static String getNumberOfDaysToDate(String que){
        List<Integer> stringList = calculateNumberOfDays(que);
        StringBuilder resultString = new StringBuilder();
        if (stringList .size() != 0) {
            for (int i = 0; i < stringList .size(); ++i){
                int num = stringList.get(i);
                if (num < 0)
                    resultString.append("Не могу посчитать.");
                else if (num == 0)
                    resultString.append("Сегодня этот день.");
                else
                    resultString.append(num + " " + AI_java.getWord(stringList.get(i), "дней", "день", "дня") + ".");
            }
        }
        if (resultString.length() > 0 && resultString.charAt(resultString.length() - 1) == '.')
            return deleteLastSymbol(resultString.toString());
        return resultString.toString();
    }


    */
/**
     * Рассчитать кол-во дней
     * @param str
     * @return список кол-ва дней до дат
     *//*

    private static List<Integer> calculateNumberOfDays(String str){
        List<Integer>result = new ArrayList<>();
        List<String> date = getDate(str, Pattern.compile("дней до ((0?[1-9]|[12][0-9]|3[01]) (янв(?:аря)?|фев(?:раля)?|мар(?:та)?|апр(?:еля)?|мая|июн(?:я)?|июл(?:я)?|авг(?:уста)?|сен(?:тября)?|окт(?:ября)?|ноя(?:бря)?|дек(?:абря)?) \\d{4})",
                Pattern.CASE_INSENSITIVE), "дней до");
        Date today = getStringAsDate(getDay(0));
        for (int i = 0; i < date.size(); ++i){
            int count = -1;
            Date dateSearch = getStringAsDate(date.get(i));
            if (dateSearch != null){
                count = daysBetween(today, dateSearch);
            }
            result.add(count);
        }
        return result;
    }

    private static Date getStringAsDate(String str){
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("d MMMM yyyy", MainActivity.LANGUAGE);
            Calendar cal = Calendar.getInstance();
            cal.setTime(inputFormat.parse(str));
            return cal.getTime();
        }
        catch (Exception ex) {
            Log.e("getSAD", ex.toString());
        }
        return null;
    }

    private static int daysBetween(Date d1, Date d2) {
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    */
/**
     * Случайная фраза из списка
     * @param list список фраз
     * @return
     *//*

    private static String randomPhrase(List<String> list){
        return list.get((int)(Math.random() * list.size()));
    }

    */
/**
     * Удалить последний символ
     * @param str
     * @return
     *//*

    public static String deleteLastSymbol(String str){
        return str.replaceFirst(".$","");
    }

    */
/**
     * Преобразовать длинный ответ на несколько небольших
     * @param str
     * @return список фраз
     *//*

    private static List<String> transformAnswer(String str){
        List<String> list = new ArrayList<>();
        int ch = str.length() / MAX_LENGTH_MESSAGE + 1;
        Log.e("ch = ", str.length() + " " + MAX_LENGTH_MESSAGE);
        int sizeCh = str.length() / ch;
        for (int i = 0; i < ch - 1; ++i){
            int j = (i == 0) ? (i + 1) * sizeCh : i * sizeCh;
            while (str.charAt(j) != ' '){
                j--;
            }
            list.add(str.substring(0, j));
            str = str.substring(j + 1);
        }
        list.add(str);
        return list;
    }

}
*/
