package ru.vaseba.babka_nagadalka.botapi;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.vaseba.babka_nagadalka.MyBabkaNagadalka;
import ru.vaseba.babka_nagadalka.cache.UserDataCache;
import ru.vaseba.babka_nagadalka.model.UserProfileData;
import ru.vaseba.babka_nagadalka.service.MainMenuService;
import ru.vaseba.babka_nagadalka.service.ReplyMessagesService;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergei Viacheslaev
 */
@Component
@Slf4j
public class TelegramFacade {
    private BotStateContext botStateContext;
    private UserDataCache userDataCache;
    private MainMenuService mainMenuService;
    private MyBabkaNagadalka myWizardBot;
    private ReplyMessagesService messagesService;

    public TelegramFacade(BotStateContext botStateContext, UserDataCache userDataCache, MainMenuService mainMenuService,
                          @Lazy MyBabkaNagadalka myWizardBot, ReplyMessagesService messagesService) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
        this.mainMenuService = mainMenuService;
        this.myWizardBot = myWizardBot;
        this.messagesService = messagesService;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
            return processCallbackQuery(callbackQuery);
        }

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, userId: {}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getFrom().getId(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }


    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        int userId = message.getFrom().getId();
        long chatId = message.getChatId();
        BotState botState;
        SendMessage replyMessage;

        switch (inputMsg) {
            case "/start":
                botState = BotState.ASK_DESTINY;
                myWizardBot.sendPhoto(chatId, messagesService.getReplyText("reply.hello"), "static/images/babka.jpg");
                break;
            case "Помочь принять верное решение":
                botState = BotState.FILLING_PROFILE;

                break;
            case "ответы":
                botState = BotState.SHOW_USER_PROFILE;
                break;
            case "Скачать ответы":
                myWizardBot.sendDocument(chatId, "Ваша анкета", getUsersProfile(userId));
                botState = BotState.SHOW_USER_PROFILE;
                break;
            case "Помощь":
                botState = BotState.SHOW_HELP_MENU;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId);
                break;
        }

        userDataCache.setUsersCurrentBotState(userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }


    private BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final int userId = buttonQuery.getFrom().getId();
        BotApiMethod<?> callBackAnswer = mainMenuService.getMainMenuMessage(chatId, "Воспользуйтесь главным меню");


        if (buttonQuery.getData().equals("buttonSolution")) {
            callBackAnswer = new SendMessage(chatId, "Только глупец принимает решения не думая! \nНе зря говорят: \nСемь раз отмерь, один раз отрежь \nесть один тайный способ что бы принять верное решение! \nДай знать если готов");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_WHAT_IF_YES);

        } else if (buttonQuery.getData().equals("buttonIwillThink")) {
            callBackAnswer = sendAnswerCallbackQuery("Возвращайся, когда будешь готов", false, buttonQuery);
        } else if (buttonQuery.getData().equals("-")) {
            callBackAnswer = sendAnswerCallbackQuery("Самое сложное — не знать, правильно ли ты сделал...", true, buttonQuery);
        } else {
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
        }
        return callBackAnswer;
    }


    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }

    @SneakyThrows
    public File getUsersProfile(int userId) {
        UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
        File profileFile = ResourceUtils.getFile("classpath:static/docs/users_profile.txt");

        try (FileWriter fw = new FileWriter(profileFile.getAbsoluteFile());
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(userProfileData.toString());
        }
        return profileFile;
    }

    private InlineKeyboardMarkup getReadingButtonsMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonGenderMan = new InlineKeyboardButton().setText("Да");
        InlineKeyboardButton buttonGenderWoman = new InlineKeyboardButton().setText("Нет");

        buttonGenderMan.setCallbackData("buttonYes");
        buttonGenderWoman.setCallbackData("buttonNo");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonGenderMan);
        keyboardButtonsRow1.add(buttonGenderWoman);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }


}
