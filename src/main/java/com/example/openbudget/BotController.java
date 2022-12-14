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
                                        .text("??? Tasdiqlash kodi xato kiritildi!")
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
//                    result.append('??');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'O' && (text.charAt(i + 1) == '\'' || text.charAt(i + 1) == '`')) {
//                    result.append('??');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'y' && (text.charAt(i + 1) == 'a' || text.charAt(i + 1) == 'A')) {
//                    result.append('??');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'Y' && (text.charAt(i + 1) == 'a' || text.charAt(i + 1) == 'A')) {
//                    result.append('??');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'y' && (text.charAt(i + 1) == 'o' || text.charAt(i + 1) == 'O')) {
//                    result.append('??');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'Y' && (text.charAt(i + 1) == 'o' || text.charAt(i + 1) == 'O')) {
//                    result.append('??');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'y' && (text.charAt(i + 1) == 'u' || text.charAt(i + 1) == 'U')) {
//                    result.append('??');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'Y' && (text.charAt(i + 1) == 'u' || text.charAt(i + 1) == 'U')) {
//                    result.append('??');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'y' && (text.charAt(i + 1) == 'e' || text.charAt(i + 1) == 'E')) {
//                    result.append('??');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'Y' && (text.charAt(i + 1) == 'e' || text.charAt(i + 1) == 'E')) {
//                    result.append('??');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 't' && (text.charAt(i + 1) == 's' || text.charAt(i + 1) == 'S')) {
//                    result.append('??');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'T' && (text.charAt(i + 1) == 's' || text.charAt(i + 1) == 'S')) {
//                    result.append('??');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'c' && (text.charAt(i + 1) == 'h' || text.charAt(i + 1) == 'H')) {
//                    result.append('??');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'C' && (text.charAt(i + 1) == 'h' || text.charAt(i + 1) == 'H')) {
//                    result.append('??');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 's' && (text.charAt(i + 1) == 'h' || text.charAt(i + 1) == 'H')) {
//                    result.append('??');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == 'S' && (text.charAt(i + 1) == 'h' || text.charAt(i + 1) == 'H')) {
//                    result.append('??');
//                    i++;
//                    continue;
//                }
//                if (text.charAt(i) == '??') {
//                    result.append('S');
//                    result.append('h');
//                    continue;
//                }
//                if (text.charAt(i) == '??') {
//                    result.append('s');
//                    result.append('h');
//                    continue;
//                }
//                if (text.charAt(i) == '??') {
//                    result.append('C');
//                    result.append('h');
//                    continue;
//                }
//                if (text.charAt(i) == '??') {
//                    result.append('??');
//                    result.append('h');
//                    continue;
//                }
//                if (text.charAt(i) == '??') {
//                    result.append('T');
//                    result.append('s');
//                    continue;
//                }
//                if (text.charAt(i) == '??') {
//                    result.append('t');
//                    result.append('s');
//                    continue;
//                }
//                if (text.charAt(i) == '??') {
//                    result.append('y');
//                    result.append('o');
//                    continue;
//                }
//                if (text.charAt(i) == '??') {
//                    result.append('Y');
//                    result.append('o');
//                    continue;
//                }
//                if (text.charAt(i) == '??') {
//                    result.append('y');
//                    result.append('e');
//                    continue;
//                }
//                if (text.charAt(i) == '??') {
//                    result.append('Y');
//                    result.append('e');
//                    continue;
//                }
//                if (text.charAt(i) == '??') {
//                    result.append('Y');
//                    result.append('a');
//                    continue;
//                }
//                if (text.charAt(i) == '??') {
//                    result.append('y');
//                    result.append('a');
//                    continue;
//                }
//                if (text.charAt(i) == '??') {
//                    result.append('y');
//                    result.append('u');
//                    continue;
//                }
//                if (text.charAt(i) == '??') {
//                    result.append('Y');
//                    result.append('u');
//                    continue;
//                }
//                if (text.charAt(i) == '??') {
//                    result.append('O');
//                    result.append('\'');
//                    continue;
//                }
//                if (text.charAt(i) == '??') {
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
//                return '??';
//            case 'b':
//                return '??';
//            case 'd':
//                return '??';
//            case 'e':
//                return '??';
//            case 'f':
//                return '??';
//            case 'g':
//                return '??';
//            case 'h':
//                return '??';
//            case 'i':
//                return '??';
//            case 'j':
//                return '??';
//            case 'k':
//                return '??';
//            case 'l':
//                return '??';
//            case 'm':
//                return '??';
//            case 'n':
//                return '??';
//            case 'o':
//                return '??';
//            case 'p':
//                return '??';
//            case 'q':
//                return '??';
//            case 'r':
//                return '??';
//            case 's':
//                return '??';
//            case 't':
//                return '??';
//            case 'u':
//                return '??';
//            case 'v':
//                return '??';
//            case 'x':
//                return '??';
//            case 'y':
//                return '??';
//            case 'z':
//                return '??';
//
//            case 'A':
//                return '??';
//            case 'B':
//                return '??';
//            case 'D':
//                return '??';
//            case 'E':
//                return '??';
//            case 'F':
//                return '??';
//            case 'G':
//                return '??';
//            case 'H':
//                return '??';
//            case 'I':
//                return '??';
//            case 'J':
//                return '??';
//            case 'K':
//                return '??';
//            case 'L':
//                return '??';
//            case 'M':
//                return '??';
//            case 'N':
//                return '??';
//            case 'O':
//                return '??';
//            case 'P':
//                return '??';
//            case 'Q':
//                return '??';
//            case 'R':
//                return '??';
//            case 'S':
//                return '??';
//            case 'T':
//                return '??';
//            case 'U':
//                return '??';
//            case 'V':
//                return '??';
//            case 'X':
//                return '??';
//            case 'Y':
//                return '??';
//            case 'Z':
//                return '??';
//
//            case '\'':
//                return '??';
//            case '??':
//                return '\'';
//
//            case '??':
//                return 'a';
//            case '??':
//                return 'b';
//            case '??':
//                return 'd';
//            case '??':
//                return 'e';
//            case '??':
//                return 'f';
//            case '??':
//                return 'g';
//            case '??':
//                return 'h';
//            case '??':
//                return 'i';
//            case '??':
//                return 'j';
//            case '??':
//                return 'k';
//            case '??':
//                return 'l';
//            case '??':
//                return 'm';
//            case '??':
//                return 'n';
//            case '??':
//                return 'o';
//            case '??':
//                return 'p';
//            case '??':
//                return 'q';
//            case '??':
//                return 'r';
//            case '??':
//                return 's';
//            case '??':
//                return 't';
//            case '??':
//                return 'u';
//            case '??':
//                return 'v';
//            case '??':
//                return 'x';
//            case '??':
//                return 'y';
//            case '??':
//                return 'z';
//
//            case '??':
//                return 'A';
//            case '??':
//                return 'B';
//            case '??':
//                return 'D';
//            case '??':
//                return 'E';
//            case '??':
//                return 'F';
//            case '??':
//                return 'G';
//            case '??':
//                return 'H';
//            case '??':
//                return 'I';
//            case '??':
//                return 'J';
//            case '??':
//                return 'K';
//            case '??':
//                return 'L';
//            case '??':
//                return 'M';
//            case '??':
//                return 'N';
//            case '??':
//                return 'O';
//            case '??':
//                return 'P';
//            case '??':
//                return 'Q';
//            case '??':
//                return 'R';
//            case '??':
//                return 'S';
//            case '??':
//                return 'T';
//            case '??':
//                return 'U';
//            case '??':
//                return 'V';
//            case '??':
//                return 'X';
//            case '??':
//                return 'Y';
//            case '??':
//                return 'Z';
//
//            default:
//                return a;
//        }
//    }

}
