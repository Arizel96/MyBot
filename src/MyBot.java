import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Arizel on 20.11.2016.
 */
public class MyBot extends TelegramLongPollingBot {
    public static HashMap<String, ArrayList<String>> mapRus;
    public static ArrayList<String> others = new ArrayList<>();
    private static final String TO_USER = "Благодарю за запрос, сударь, вот актуальнейший курс на сегодня по отношению к гривне : ";
    private static final String ERROR = "Упс, что-то пошло не так. Прошу прощения!";
    private static final String TO_REPEAT = "Прошу прощения, сударь. Мне совершенно не ясны ваши намерения. " +
            "Пожалуйста, будьте добры, сообщите мне валюту курс на которую вас интересует, спасибо.";

    private String currencyEuro;
    private String currencyUsd;
    private String currencyRub;

    @Override
    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage();
        String msgText = msg.getText().toLowerCase();
        if (msg != null && msg.hasText()) {
            getData();
            sendMsg(defineCurrency(msgText), msg);
        }else {
            sendMsg(TO_REPEAT, msg);
        }
    }


    @Override
    public String getBotUsername() {
        return "KianyRivs_Bot";
    }


    public void sendMsg(String text, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String defineCurrency(String msgText) {
        for (Map.Entry<String, ArrayList<String>> pair : mapRus.entrySet()) {

            ArrayList<String> list = pair.getValue();

            for (int i = 0; i < list.size(); i++) {
                if (msgText.contains(list.get(i))) {
                    if (pair.getKey().equals("usd")) {
                        return currencyUsd;
                    } else if (pair.getKey().equals("rub")) {
                        return currencyRub;
                    } else if (pair.getKey().equals("eur")) {
                        return currencyEuro;
                    }
                }
            }
        }
        return TO_REPEAT;
    }

    public String getUsd(ArrayList<HashMap<String, Object>> data) {
        HashMap<String, Object> map = data.get(2);
        String buy = (String) map.get("buy");
        String sale = (String) map.get("sale");
        String currency = TO_USER + "\nПокупка : " + buy + "\n" + "Продажа: " + sale + "\n (Курс ПриватБанка)";
        return currency;
    }

    public String getRub(ArrayList<HashMap<String, Object>> data) {
        HashMap<String, Object> map = data.get(1);
        String buy = (String) map.get("buy");
        String sale = (String) map.get("sale");
        String currency = TO_USER + "\nПокупка : " + buy + "\n" + "Продажа: " + sale + "\n (Курс ПриватБанка)";
        return currency;
    }

    public String getEuro(ArrayList<HashMap<String, Object>> data) {
        HashMap<String, Object> map = data.get(0);
        String buy = (String) map.get("buy");
        String sale = (String) map.get("sale");
        String currency = TO_USER + "\nПокупка : " + buy + "\n" + "Продажа: " + sale + "\n (Курс ПриватБанка)";
        return currency;
    }

    private void getData() {
        StringBuilder sb = new StringBuilder();
        try {
            URLConnection urlConnection = new URL("https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5").openConnection();
            InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
            Scanner scanner = new Scanner(reader);
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }
            JSONArray array = (JSONArray) new JSONParser().parse(String.valueOf(sb));
            currencyEuro = getEuro(array);
            currencyUsd = getUsd(array);
            currencyRub = getRub(array);
        } catch (IOException e) {
           currencyEuro = ERROR;
           currencyRub = ERROR;
           currencyUsd = ERROR;
        } catch (ParseException e) {
            currencyEuro = ERROR;
            currencyRub = ERROR;
            currencyUsd = ERROR;
        }
//        catch (IndexOutOfBoundsException e) {
//            currencyEuro = ERROR;
//            currencyRub = ERROR;
//            currencyUsd = ERROR;
//        }

    }

    @Override
    public String getBotToken() {
        return "260266090:AAH8IgdAI4g1KbzBuaIJ9YXXwvvvf11UZLs";
    }
}
