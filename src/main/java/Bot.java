import java.util.ArrayList;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot{

    @Override
    public void onUpdateReceived(Update update) {
        // get chat id
        String chatId = update.getMessage().getChatId().toString();
        // get user inpute message
        String[] inputMessage = update.getMessage().getText().split(" ");
        //sendmessage to inform to user
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if(inputMessage[0].equals("/start")){
            sendMessage.setText("Hi! This is NDownloader this can sent you whole gallery of hentais.You just have to enter hentai gallery code.");
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if(inputMessage[0].equals("/help")){
            sendMessage.setText("Hi! This is NDownloader this can sent you whole gallery of hentais.You just have to enter hentai gallery code.");
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if(inputMessage[0].equals("/random")){
            random(chatId);
        } 
        else{
            checkStrOrInt(inputMessage[0], chatId);
        }
    }

    // method to check if user input is code or string. if input is a string this will show error message to user
    public void checkStrOrInt(String s,String chatId){
        SendMessage errorMessage = new SendMessage();
        errorMessage.setChatId(chatId);
        try {
            int code = Integer.parseInt(s);
            tryConnectToLink(code, errorMessage,chatId);
        } catch (NumberFormatException numberFormatException) {
            errorMessage.setText("You can only enter hentai codes.");
            try {
                execute(errorMessage);
            } catch (TelegramApiException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } 
    }

    
    // method to get connect to url and get html document 
    public void tryConnectToLink(int code,SendMessage errorMessage,String chatId){
        WebReader webReader = new WebReader("https://nhentai.net/g/"+code+"/");
        Document document = null;
        try {
            document = webReader.getDocument();
            getGalleryInfo(webReader,chatId);
        } catch (HttpStatusException httpStatusException) {
            errorMessage.setText("Your code didn't match.");
            try {
                execute(errorMessage);
            } catch (TelegramApiException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
    public void random(String chatId){
        SendMessage errorMessage = new SendMessage();
        errorMessage.setChatId(chatId);
        WebReader webReader = new WebReader("https://nhentai.net/random/");
        Document document = null;
        try {
            document = webReader.getDocument();
            getGalleryInfo(webReader,chatId);
        } catch (HttpStatusException httpStatusException) {
            errorMessage.setText("Try again please.");
            try {
                execute(errorMessage);
            } catch (TelegramApiException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


    // get gallery name, pages, link and show the user back
    public void getGalleryInfo(WebReader webReader, String chatId){
        SendMessage infoMessage = new SendMessage();
        infoMessage.setChatId(chatId);
        infoMessage.setText("Name: "+ webReader.getName()+ "\n"+  "Pages: "+ webReader.getPages() +"\nLink: " + "https://nhentai.net/g/"+webReader.getGalleryCode()+"/");
        try {
            execute(infoMessage);
        } catch (TelegramApiException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        getHentai(webReader,chatId);
    }


    // send gallery of hentai to user
    public void getHentai(WebReader webReader,String chatId){
        // arrayList to store links of images
        ArrayList<String> linkList = new ArrayList<String>();
        // to get links
        linkList = webReader.getImgLinks();

        // sendphoto object to send photo when last images is only one image because sendMediaGroup object can't send only one image
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);

        //send media group method to send images in method
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        ArrayList<InputMedia> arrList = new ArrayList<InputMedia>();
        sendMediaGroup.setMedias(arrList);
        sendMediaGroup.setChatId(chatId);


        // this for each loop will add images to arraylist and if there is 10 images this will send to user and get another 10 images
        for (String link : linkList) {
            arrList.add(new InputMedia(link) {
                @Override
                public String getType() {
                    // TODO Auto-generated method stub
                    return "photo";
                }
            });
            if (arrList.size() >= 10) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    execute(sendMediaGroup);
                } catch (TelegramApiException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                arrList.clear();
            }
            sendPhoto.setPhoto(new InputFile(link));
        }
        // to send photo when last images is only one image because sendMediaGroup object can't send only one image
        if (arrList.size() == 1) {
            try {
                execute(sendPhoto);
            } catch (TelegramApiException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            try {
                execute(sendMediaGroup);
            } catch (TelegramApiException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        // TODO Auto-generated method stub
        return "NDownloader";
    }

    @Override
    public String getBotToken() {
        // TODO Auto-generated method stub
        return "5013548638:AAGuNyamDAZkMOF8KMoSjMOyqhC9r5oe2JQ";
    }

    
}
