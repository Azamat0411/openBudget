package com.example.openbudget;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.*;

public class BotController extends TelegramLongPollingBot {

    private static final String token = "5758620284:AAG9_Hz2sP8Z9myuGXh0AqvSJVLe9zWUyDM";
    public static final String userName = "UyasOpenBudgetBot";

    private String appToken = "k";
    private String application = "128101";
    private List<Map> action = new ArrayList<>();
    private List<Map> users = new ArrayList<>();

    public String getBotUsername() {
        return userName;
    }

    public String getBotToken() {
        return token;
    }

//    private String readStringFromUrl(String url) throws IOException {
//        try (InputStream is = new URL(url).openStream()) {
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
//            StringBuilder sb = new StringBuilder();
//            int cp;
//            while ((cp = rd.read()) != -1) {
//                sb.append((char) cp);
//            }
//            return sb.toString();
//        }
//    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        SendMessage sendMessage = new SendMessage();

        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(message.getChatId());
        if (message.hasText()) {
            switch (message.getText()) {
                case "/start":
                    try {
                        Map<String, String> user = new LinkedHashMap<>();
                        user.put("userName", message.getFrom().getUserName());
                        user.put("firstName", message.getFrom().getFirstName());
                        user.put("lastName", message.getFrom().getLastName());
                        users.add(user);
                        execute(SendMessage.builder()
                                .chatId(message.getChatId())
                                .text("Assalomu alaykum!!!\nBu bot orqali Open Budgetda tashabbusga ovoz yig'ishingiz mumkin\nTelefon nomer kiriting\nNamuna: +998911916600")
                                .build());
                        return;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
//                        sendMessage.setText("Assalomu alaykum!!!\nBu bot orqali matnlarni lotindan kirilga yoki kirildan lotinga o'tkazishingiz mumkin");
//                    sendMessage.setText("Assalomu alaykum!!!\nBu bot orqali Open Budgetda tashabbusga ovoz yig'ishingiz mumkin");
                case "/help":
                    try {
                        execute(SendMessage.builder()
                                .chatId(message.getChatId())
                                .text(application)
                                .build());
                        return;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                case "/users":
                    StringBuilder u = new StringBuilder();
                    for (int i = 0; i < users.size(); i++) {
                        u.append(i + 1).append(".").append(users.get(i).get("firstName").toString()).append("\n");
                        u.append(users.get(i).get("lastName")).append("\n");
                        u.append(users.get(i).get("userName")).append("\n");
                    }
                    try {
                        execute(SendMessage.builder()
                                .chatId(message.getChatId())
                                .text(u.toString())
                                .build());
                        return;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                case "/neww":
                    sendMessage.setText("Yangi tashabbus linkini kiriting:\nMasalan: https://openbudget.uz/boards/0/000000");
                default: {
                    String m = message.getText();
                    m = m.replaceAll("\\+", "");
                    if (m.startsWith("https")) {
                        String[] a = m.split("/");
                        m = a[a.length - 1];
                        HttpsURLConnection response = post("https://first-app-deploy-heroku.herokuapp.com/api/openBudgetAccount", "{\"id\": " + message.getChatId() + ",\"application\": " + m + "}");
                        try{
                            if(response.getResponseCode() == 200 || response.getResponseCode() == 201){
                                sendMessage.setText("Muvaffaqiyatli saqlandi");
                            }else{
                                sendMessage.setText("Server bilan xatolik");
                            }
                        }catch (Exception e){
                            sendMessage.setText("Xatolik yuz berdi");
                        }
                    } else if (m.startsWith("998") && m.matches("[0-9]*") && m.length() == 12) {
                        sendMessage.setText("Kuting...");
                        Message send;
                        try {
                            send = execute(sendMessage);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
//                        HttpsURLConnection response = post("https://admin.openbudget.uz/api/v1/user/validate_phone/", "{\"phone\": " + "\""+m + "\""+",\"application\": " + "\""+application+"\"" + "}");
                        HttpsURLConnection response = post("https://admin.openbudget.uz/api/v1/user/validate_phone/", "{\n" +
                                "    \"application\": \""+application+"\",\n" +
                                "    \"phone\": \""+m+"\"\n" +
                                "}");
                        try{
                            System.out.println(response.getResponseCode());
                            System.out.println(response.getResponseMessage());
                            System.out.println(application);
                            if(response.getResponseCode() == 200 || response.getResponseCode() == 201){
                                Map<String, String> map = new LinkedHashMap<>();
                                StringBuilder textBuilder = new StringBuilder();

                                System.out.println(response.getResponseMessage());

                                InputStream in = response.getInputStream();
                                try (BufferedReader reader = new BufferedReader(new InputStreamReader
                                        (in, StandardCharsets.UTF_8))) {
                                    String c;
                                    while ((c = reader.readLine()) != null) {
                                        if(c.contains("token")){
                                            textBuilder.append(c);
                                        }
                                    }
                                    textBuilder = new StringBuilder(textBuilder.toString().replaceAll("&quot;",""));
                                }

                                String text = "";
                                if(textBuilder.toString().contains("token")){
                                    text = textBuilder.toString().replaceAll("token: ", "").replaceAll(" ", "");
                                    map.put("id", String.valueOf(message.getChatId()));
                                    map.put("token", text);
                                    map.put("phone", message.getText());
                                    action.add(map);
                                    text = "Sms xabar yuborildi";
                                }else {
                                    text = "Xatolik yuz berdi";
                                }
                                System.out.println(textBuilder);
                                for (Map user : action){
                                    System.out.println("phone: "+ user.get("phone") + "\nid: " + user.get("id") + "\ntoken: " + user.get("token")+"\n");
                                }
                                try {
                                    execute(EditMessageText.builder()
                                            .chatId(send.getChatId())
                                            .text(text)
                                            .messageId(send.getMessageId())
                                            .build());
                                } catch (TelegramApiException e) {
                                    throw new RuntimeException(e);
                                }
                            }else{
                                try {
                                    if(response.getResponseCode()==400) {
                                        execute(EditMessageText.builder()
                                                .chatId(send.getChatId())
                                                .text("Ovoz berish jarayonida bu raqamdan foydalanilgan!\n" +
                                                        "Boshqa raqam yuboring.")
                                                .messageId(send.getMessageId())
                                                .build());
                                    }else {
                                        execute(EditMessageText.builder()
                                                .chatId(send.getChatId())
                                                .text("Server bilan xatolik")
                                                .messageId(send.getMessageId())
                                                .build());
                                    }
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }catch (Exception e){
                            try {
                                execute(EditMessageText.builder()
                                        .chatId(send.getChatId())
                                        .text("Server bilan xatolik")
                                        .messageId(send.getMessageId())
                                        .build());
                            } catch (Exception exp) {
                                throw new RuntimeException(exp);
                            }
                        }
                        return;
                    } else if (m.matches("[0-9]*") && m.length() == 6) {
                        sendMessage.setText("Kuting...");
                        Message send;
                        try {
                            send = execute(sendMessage);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                        String _token = "";
                        String _phone = "";
                        for(Map map:action){
                            if(Objects.equals(map.get("id"), String.valueOf(message.getChatId()))){
                                _token = map.get("token").toString();
                                _phone = map.get("phone").toString();
                                break;
                            }
                        }

                        HttpsURLConnection response = post("https://admin.openbudget.uz/api/v1/user/temp/vote/", "{\n" +
                                "    \"application\": "+"\""+application+"\",\n" +
                                "    \"otp\": \""+message.getText()+"\",\n" +
                                "    \"phone\": \""+_phone+"\",\n" +
                                "    \"token\": "+_token+"\n" +
                                "}");

                        try {
                            if(response.getResponseCode() == 200 || response.getResponseCode() == 201){
//                                InputStream in = response.getInputStream();
//                                StringBuilder textBuilder = new StringBuilder();
//                                try (BufferedReader reader = new BufferedReader(new InputStreamReader
//                                        (in, StandardCharsets.UTF_8))) {
//                                    String c;
//                                    while ((c = reader.readLine()) != null) {
////                                    if(c.contains("token")){
//                                        textBuilder.append(c);
////                                    }else if(c.contains("details")){
////                                        textBuilder.append(c);
////                                    }
//                                    }
////                                textBuilder = new StringBuilder(textBuilder.toString().replaceAll("&quot;",""));
//
//                                }
//                                System.out.println(textBuilder);
                                for (int i = 0; i < action.size(); i++) {
                                    if(Objects.equals(action.get(i).get("id"), String.valueOf(message.getChatId()))){
                                        action.remove(i);
                                        break;
                                    }
                                }
                                execute(EditMessageText.builder()
                                        .chatId(send.getChatId())
                                        .text("Qabul qilindi")
                                        .messageId(send.getMessageId())
                                        .build());
                            }else if(response.getResponseCode()==400) {
                                execute(EditMessageText.builder()
                                        .chatId(send.getChatId())
                                        .text("‼ Tasdiqlash kodi xato kiritildi!")
                                        .messageId(send.getMessageId())
                                        .build());
                            }else{
                                execute(EditMessageText.builder()
                                        .chatId(send.getChatId())
                                        .text("Server bilan xatolik!")
                                        .messageId(send.getMessageId())
                                        .build());
                            }
                        }catch (Exception exp){
                            try {
                                execute(EditMessageText.builder()
                                        .chatId(send.getChatId())
                                        .text("Server bilan xatolik!")
                                        .messageId(send.getMessageId())
                                        .build());
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("error: "+exp);
                        }
                        return;
                    } else {
                        if (Objects.equals(message.getText(), "aaaa")) {
                            System.out.println(update.getMessage().getFrom().getUserName());
                            appToken = message.getFrom().getUserName();
                            sendMessage.setText(appToken);
                        } else if(m.startsWith("Azamat")){
//                            String str1="192.168.50.10";
//                            String str2="255.255.255.0";
//                            String str3="192.168.50.1";
//                            String[] command1 = { "netsh ", "interface ", "ipv4 ", "set ", "address ",
//                                    "name=", "\"Local Area Connection\" " ,"static ", str1," ", str2," ", str3};
//                            Process pp = null;
//                            for (String s : command1) {
//                                System.out.print(s);
//                            }
//                            try {
//                                pp = Runtime.getRuntime().exec(command1);
//                            } catch (IOException e) {
//                                System.out.println("error: "+e);
//                                throw new RuntimeException(e);
//                            }
//                            System.out.print( pp);
                            application = m.replaceAll("Azamat", "");
                            sendMessage.setText("Tashabbus o'rnatildi!!!");
                        }else {
                            sendMessage.setText("Noma'lum buyruq!!!");
//                            sendMessage.setText(appToken);
                        }
                        sendMessage.setReplyToMessageId(message.getMessageId());
                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                        return;
                    }
                }
            }


//            sendMessage.setChatId(message.getChatId());
//            DeleteMessage deleteMessage = new DeleteMessage(update.getMessage().getChatId().toString(), message.getMessageId());
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                System.out.println("error:" + e);
                throw new RuntimeException(e);
            }
        }
    }

    public HttpsURLConnection post(String uri, String data) {
        System.out.println(data);
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection conn;
            URL url = new URL(uri);

            conn = (HttpsURLConnection)url.openConnection();
            conn.setHostnameVerifier(allHostsValid);
            conn.setDoOutput(true);
            conn.setConnectTimeout(100000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            byte[] out = data.getBytes(StandardCharsets.UTF_8);

            conn.getOutputStream().write(out);

            conn.disconnect();
            return conn;
        } catch (Exception e) {
            System.out.println("error: " + e);
            return null;
//            e.printStackTrace();
        }
    }

//    public String kirilLotin(Message message) throws UnsupportedEncodingException {
//        String text = message.getText();
//        StringBuilder result = new StringBuilder();
//        for (int i = 0; i < text.length(); i++) {
//            if (i != text.length() - 1) {
//                if (text.charAt(i) == 'o' && (text.charAt(i + 1) == '\'' || text.charAt(i + 1) == '`')) {
//                    result.append('ў');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'O' && (text.charAt(i + 1) == '\'' || text.charAt(i + 1) == '`')) {
//                    result.append('Ў');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'y' && (text.charAt(i + 1) == 'a' || text.charAt(i + 1) == 'A')) {
//                    result.append('я');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'Y' && (text.charAt(i + 1) == 'a' || text.charAt(i + 1) == 'A')) {
//                    result.append('Я');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'y' && (text.charAt(i + 1) == 'o' || text.charAt(i + 1) == 'O')) {
//                    result.append('ё');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'Y' && (text.charAt(i + 1) == 'o' || text.charAt(i + 1) == 'O')) {
//                    result.append('Ё');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'y' && (text.charAt(i + 1) == 'u' || text.charAt(i + 1) == 'U')) {
//                    result.append('ю');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'Y' && (text.charAt(i + 1) == 'u' || text.charAt(i + 1) == 'U')) {
//                    result.append('Ю');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'y' && (text.charAt(i + 1) == 'e' || text.charAt(i + 1) == 'E')) {
//                    result.append('е');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'Y' && (text.charAt(i + 1) == 'e' || text.charAt(i + 1) == 'E')) {
//                    result.append('Е');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 't' && (text.charAt(i + 1) == 's' || text.charAt(i + 1) == 'S')) {
//                    result.append('ц');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'T' && (text.charAt(i + 1) == 's' || text.charAt(i + 1) == 'S')) {
//                    result.append('Ц');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'c' && (text.charAt(i + 1) == 'h' || text.charAt(i + 1) == 'H')) {
//                    result.append('ч');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'C' && (text.charAt(i + 1) == 'h' || text.charAt(i + 1) == 'H')) {
//                    result.append('Ч');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 's' && (text.charAt(i + 1) == 'h' || text.charAt(i + 1) == 'H')) {
//                    result.append('ш');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'S' && (text.charAt(i + 1) == 'h' || text.charAt(i + 1) == 'H')) {
//                    result.append('Ш');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'Ш') {
//                    result.append('S');
//                    result.append('h');
//                    continue;
//                }
//                if (text.charAt(i) == 'ш') {
//                    result.append('s');
//                    result.append('h');
//                    continue;
//                }
//                if (text.charAt(i) == 'Ч') {
//                    result.append('C');
//                    result.append('h');
//                    continue;
//                }
//                if (text.charAt(i) == 'ч') {
//                    result.append('с');
//                    result.append('h');
//                    continue;
//                }
//                if (text.charAt(i) == 'Ц') {
//                    result.append('T');
//                    result.append('s');
//                    continue;
//                }
//                if (text.charAt(i) == 'ц') {
//                    result.append('t');
//                    result.append('s');
//                    continue;
//                }
//                if (text.charAt(i) == 'ё') {
//                    result.append('y');
//                    result.append('o');
//                    continue;
//                }
//                if (text.charAt(i) == 'Ё') {
//                    result.append('Y');
//                    result.append('o');
//                    continue;
//                }
//                if (text.charAt(i) == 'е') {
//                    result.append('y');
//                    result.append('e');
//                    continue;
//                }
//                if (text.charAt(i) == 'Е') {
//                    result.append('Y');
//                    result.append('e');
//                    continue;
//                }
//                if (text.charAt(i) == 'Я') {
//                    result.append('Y');
//                    result.append('a');
//                    continue;
//                }
//                if (text.charAt(i) == 'я') {
//                    result.append('y');
//                    result.append('a');
//                    continue;
//                }
//                if (text.charAt(i) == 'ю') {
//                    result.append('y');
//                    result.append('u');
//                    continue;
//                }
//                if (text.charAt(i) == 'Ю') {
//                    result.append('Y');
//                    result.append('u');
//                    continue;
//                }
//                if (text.charAt(i) == 'Ў') {
//                    result.append('O');
//                    result.append('\'');
//                    continue;
//                }
//                if (text.charAt(i) == 'ў') {
//                    result.append('o');
//                    result.append('\'');
//                    continue;
//                }
//            }
//            result.append(transfer(text.charAt(i)));
//        }
//        return result.toString();
//    }

//    private char transfer(char a) {
//        switch (a) {
//            case 'a':
//                return 'а';
//            case 'b':
//                return 'б';
//            case 'd':
//                return 'д';
//            case 'e':
//                return 'э';
//            case 'f':
//                return 'ф';
//            case 'g':
//                return 'г';
//            case 'h':
//                return 'ҳ';
//            case 'i':
//                return 'и';
//            case 'j':
//                return 'ж';
//            case 'k':
//                return 'к';
//            case 'l':
//                return 'л';
//            case 'm':
//                return 'м';
//            case 'n':
//                return 'н';
//            case 'o':
//                return 'о';
//            case 'p':
//                return 'п';
//            case 'q':
//                return 'қ';
//            case 'r':
//                return 'р';
//            case 's':
//                return 'с';
//            case 't':
//                return 'т';
//            case 'u':
//                return 'у';
//            case 'v':
//                return 'в';
//            case 'x':
//                return 'х';
//            case 'y':
//                return 'й';
//            case 'z':
//                return 'з';
//
//            case 'A':
//                return 'А';
//            case 'B':
//                return 'Б';
//            case 'D':
//                return 'Д';
//            case 'E':
//                return 'Э';
//            case 'F':
//                return 'Ф';
//            case 'G':
//                return 'Г';
//            case 'H':
//                return 'Ҳ';
//            case 'I':
//                return 'И';
//            case 'J':
//                return 'Ж';
//            case 'K':
//                return 'К';
//            case 'L':
//                return 'Л';
//            case 'M':
//                return 'М';
//            case 'N':
//                return 'Н';
//            case 'O':
//                return 'О';
//            case 'P':
//                return 'П';
//            case 'Q':
//                return 'Қ';
//            case 'R':
//                return 'Р';
//            case 'S':
//                return 'С';
//            case 'T':
//                return 'Т';
//            case 'U':
//                return 'У';
//            case 'V':
//                return 'В';
//            case 'X':
//                return 'Х';
//            case 'Y':
//                return 'Й';
//            case 'Z':
//                return 'З';
//
//            case '\'':
//                return 'ъ';
//            case 'ъ':
//                return '\'';
//
//            case 'а':
//                return 'a';
//            case 'б':
//                return 'b';
//            case 'д':
//                return 'd';
//            case 'э':
//                return 'e';
//            case 'ф':
//                return 'f';
//            case 'г':
//                return 'g';
//            case 'ҳ':
//                return 'h';
//            case 'и':
//                return 'i';
//            case 'ж':
//                return 'j';
//            case 'к':
//                return 'k';
//            case 'л':
//                return 'l';
//            case 'м':
//                return 'm';
//            case 'н':
//                return 'n';
//            case 'о':
//                return 'o';
//            case 'п':
//                return 'p';
//            case 'қ':
//                return 'q';
//            case 'р':
//                return 'r';
//            case 'с':
//                return 's';
//            case 'т':
//                return 't';
//            case 'у':
//                return 'u';
//            case 'в':
//                return 'v';
//            case 'х':
//                return 'x';
//            case 'й':
//                return 'y';
//            case 'з':
//                return 'z';
//
//            case 'А':
//                return 'A';
//            case 'Б':
//                return 'B';
//            case 'Д':
//                return 'D';
//            case 'Э':
//                return 'E';
//            case 'Ф':
//                return 'F';
//            case 'Г':
//                return 'G';
//            case 'Ҳ':
//                return 'H';
//            case 'И':
//                return 'I';
//            case 'Ж':
//                return 'J';
//            case 'К':
//                return 'K';
//            case 'Л':
//                return 'L';
//            case 'М':
//                return 'M';
//            case 'Н':
//                return 'N';
//            case 'О':
//                return 'O';
//            case 'П':
//                return 'P';
//            case 'Қ':
//                return 'Q';
//            case 'Р':
//                return 'R';
//            case 'С':
//                return 'S';
//            case 'Т':
//                return 'T';
//            case 'У':
//                return 'U';
//            case 'В':
//                return 'V';
//            case 'Х':
//                return 'X';
//            case 'Й':
//                return 'Y';
//            case 'З':
//                return 'Z';
//
//            default:
//                return a;
//        }
//    }

}
