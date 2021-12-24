package com.example.voiceassistent.parsing;

import android.content.Context;
import android.util.Log;

import com.example.voiceassistent.AI;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class ParsingHtmlServiceHolidays {
    //private static String URL = "http://mirkosmosa.ru/holiday/";
    private static final String ERROR_MESSAGE = "Ошибка получения данных с сайта. Попробуйте позже.";
    private static final String NULL_ANSWER_MESSAGE = "Не удалось найти дату.";
    private static final String MESSAGE_NO_HOLIDAY = "Нет праздника";

    public static List<String> getHoliday(String date, Context ctx){
        String URL = "http://mirkosmosa.ru/holiday/";
        List<String> resultListHolidays = new ArrayList<>();
        String year = getYear(date);
        String month = getMonth(date);
        Document document = readDocument(ctx, year);

        URL += year;
        if (document == null) {
            try {
                Log.d("Jsoup", "connect");
                document = Jsoup.connect(URL).get();
                //saveDocument(ctx, year, document); //если документ не сохранен - записываем его
            } catch (Exception ex) {
                resultListHolidays.add(ERROR_MESSAGE);
                return resultListHolidays; //не удалось скачать документ
            }
        }

        Element body = document.body();
        Elements elements = body.getElementsByClass("holiday_month");

        for (int i = 0; i < elements.size(); i+=2){ //ищем нужный месяц
            Element element = elements.get(i);
            String holidaysOnMonth = element.select("h3.div_center").text();
            String dateString = "Праздники в " + month + " " + year + " года";
            if (holidaysOnMonth.equals(dateString)){ //если месяц найден
                Elements el2 = element.getElementsByClass("next_phase");
                for (int j = 0; j < el2.size(); ++j){ //идем по дням месяца
                    Element element1 = el2.get(j);
                    String dateOnMonth = element1.selectFirst("div").selectFirst("span").text();
                    if (dateOnMonth.equals(date)){ //сравниваем имеющие даты
                        Elements el3 = element1.select("a");
                        if (el3.size() == 0)
                            resultListHolidays.add(MESSAGE_NO_HOLIDAY);
                        else {
                            for (int k = 0; k < el3.size(); ++k) {
                                resultListHolidays.add(el3.get(k).text());
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
        if (resultListHolidays.size() == 0)
            resultListHolidays.add(NULL_ANSWER_MESSAGE);

        return resultListHolidays;
    }

    private static String getMonth(String date){ //преобразуем
        String[] words = date.split(" ");
        words[1] = AI.deleteLastSymbol(words[1]) + "е";
        return words[1];
    }

    private static String getYear(String date){
        String[] words = date.split(" ");
        return words[2];
    }

    private static boolean saveDocument(Context ctx, String year, Document document){
        try {
            File file = new File(year);
            if (!file.exists()) {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                        ctx.openFileOutput(year, Context.MODE_PRIVATE)));
                bw.write(document.toString());
                bw.close();
                Log.i("Parsing", "Document saved");
                return true;
            }
            else {
                Log.i("Parsing", "Document exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Parsing saveDoc", "Error="+e.toString());
        }
        return false;
    }

    private static Document readDocument(Context ctx, String year) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    ctx.openFileInput(year)));
            Document document;
            StringBuilder documentAsString = new StringBuilder();
            String read = "";
            while ((read = br.readLine()) != null) {
                documentAsString.append(read);
            }
            document = Jsoup.parseBodyFragment(documentAsString.toString());
            Log.i("Parsing readDoc", "Document read");
            return document;
        } catch (IOException e) {
            Log.e("Parsing readDoc", "Error="+e.toString());
        }
        return null;
    }

}
