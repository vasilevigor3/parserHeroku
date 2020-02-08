import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class BotTest extends TelegramLongPollingBot {
    private final String redirectVK = "https://oauth.vk.com/authorize?client_id=7153000&display=page&redirect_uri=https://oauth.vk.com/blank.html&scope=friends&response_type=token&v=5.101&state=123456";
    private final String redirectEPN = "https://app.epn.bz/auth/social/google?client_id=Uym179IeibNjJVHshF0gPMAd3kZYfLWv&role=user&redirect_after_auth_url=https%3A%2F%2Fepn.bz%2Fapp-auth";


    private final String botUsername = "parserHeroku_bot";
    private final String botToken = "857820049:AAE6WQW7pRaZ3I7ViVZBgmf2T6SUZ1hl5Kg";
    private final String chatId = "-1001304932946";

    public String vkToken = "f05e872a7522184ccfd5415bcb04864a26074ad52e2684d806b29ea842d9b185942f42568aee5b480ea10";
    private final String epnToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzX3Rva2VuIiwiZXhwIjoxNTgxMjY4NjIxLCJ1c2VyX2lkIjozNDUyMTgsInVzZXJfcm9sZSI6InVzZXIiLCJjbGllbnRfcGxhdGZvcm0iOiJ3ZWIiLCJjbGllbnRfaXAiOiI1LjE2LjEyNC4xOCIsImNoZWNrX2lwIjpmYWxzZSwidG9rZW4iOiI5NTUwYjgzODJjYTE4OWNjMjg0ZWVhZGU3NjgyN2E4NzZlODc1N2JmIiwic2NvcGUiOiJkZWZhdWx0In0.hmaOgLZjNmgEwVwLqSjd540o_lZEz_B_7MjhwS8tj4hg5zYd4U6kiqe8v9pwTwAnMmX_TInpC5ka2SPOYyIagg";

    private int status;

    public static List<String> getGroupList() {
        List<String> groupList = new ArrayList<String>();
        groupList.add("w2b_it");
        groupList.add("beardaliexpress");
        groupList.add("bandos_ali");
        groupList.add("godnoten");

        return groupList;
    }


    private String vkToken1 = "";
    private String epnToken1 = "";


    public void sendText(Update update, String text) {
        try {
            execute(new SendMessage().setChatId(update.getMessage().getChatId()).setText(text));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public void autoSend(String group) throws IOException, URISyntaxException, InterruptedException {
        VkTest vkTest = new VkTest();
        EPN epn = new EPN();

//        vkTest.setToken(vkToken);
        epn.setAccess_token(epnToken);

        String linkConstructor = vkTest.linkConstructor(group, 1, 1);

        Thread.currentThread().sleep(1000);
        String stringResponse = vkTest.getStringResponse(linkConstructor);
        Thread.currentThread().sleep(1000);

        String picLink = vkTest.getPicLink(stringResponse);
        System.out.println(picLink);
        if (!picLink.equals("")) {
            String itemLink = vkTest.getItemLink(stringResponse);
            Thread.currentThread().sleep(1000);

            String linkForEpn = vkTest.getLinkForEpn(itemLink);
            Thread.currentThread().sleep(1000);

            System.out.println(linkForEpn);
            String longLinkForTG = epn.getLongLinkForTG(linkForEpn);

            String cutLinkForTG = epn.getCutLinkForTG(longLinkForTG);
            System.out.println(picLink + " - " + cutLinkForTG);

            sendPostToTG(cutLinkForTG, picLink);
        } else {
            System.out.println("GIF");
        }
    }

    Runnable task = () -> {
        while (!Thread.currentThread().isInterrupted()) {
            for (String group :getGroupList()) {
                try {
                    autoSend(group);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.currentThread().sleep(1000*60*60*2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }
    };
    Thread thread;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage().getText().equals("/vk")) {
            sendText(update, redirectVK);
            setStatus(1);
        } else if (update.getMessage().getText().equals("/epn")) {
            sendText(update, redirectEPN);
            setStatus(2);
        } else if (update.getMessage().getText().equals("/start")) {
                thread = new Thread(task);
                thread.start();

        } else if (update.getMessage().getText().equals("/stop")) {

            thread.interrupt();

        } else {
            if (status == 1) {
                vkToken1 = update.getMessage().getText();
                sendText(update, "VKtoken: " + vkToken);
                setStatus(0);
            } else if (status == 2) {
                epnToken1 = update.getMessage().getText();
                sendText(update, "EPNtoken: " + epnToken);
                setStatus(0);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public void sendPostToTG(String cutLink, String photoLink) {

        try {
            execute(new SendPhoto().setChatId(chatId).setPhoto(photoLink).setCaption(cutLink));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

//    public BotTest(DefaultBotOptions options) {
//        super(options);
//    }


}


