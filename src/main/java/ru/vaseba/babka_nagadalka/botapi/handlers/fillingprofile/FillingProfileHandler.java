package ru.vaseba.babka_nagadalka.botapi.handlers.fillingprofile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.vaseba.babka_nagadalka.botapi.BotState;
import ru.vaseba.babka_nagadalka.botapi.InputMessageHandler;
import ru.vaseba.babka_nagadalka.cache.UserDataCache;
import ru.vaseba.babka_nagadalka.model.UserProfileData;
import ru.vaseba.babka_nagadalka.service.PredictionService;
import ru.vaseba.babka_nagadalka.service.ReplyMessagesService;
import ru.vaseba.babka_nagadalka.service.UsersProfileDataService;
import ru.vaseba.babka_nagadalka.utils.Emojis;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class FillingProfileHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessagesService messagesService;
    private PredictionService predictionService;
    private UsersProfileDataService profileDataService;

    public FillingProfileHandler(UserDataCache userDataCache, ReplyMessagesService messagesService,
                                 PredictionService predictionService, UsersProfileDataService profileDataService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.predictionService = predictionService;
        this.profileDataService = profileDataService;
    }

    @Override
    public SendMessage handle(Message message) {

        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.FILLING_PROFILE)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.ASK_WHAT_IF_YES);

        }
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.FILLING_PROFILE;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        String usersAnswer = inputMsg.getText();
        int userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();

        UserProfileData profileData = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        SendMessage replyToUser = null;

        if (botState.equals(BotState.ASK_WHAT_IF_YES)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.whatIfYes");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_WHAT_IF_NO);
        }

        if (botState.equals(BotState.ASK_WHAT_IF_NO)) {
            profileData.setWhatIfYes(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.whatIfNo");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_WHAT_NO_IF_DO);
        }

        if (botState.equals(BotState.ASK_WHAT_NO_IF_DO)) {
            profileData.setWhatIfNo(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.whatNoIfDo");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_WHAT_NO_IF_NOT_DO);
        }

        if (botState.equals(BotState.ASK_WHAT_NO_IF_NOT_DO)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.whatNoIfNotDo");
            profileData.setWhatNoIfDo(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.PROFILE_FILLED);
        }

        if (botState.equals(BotState.PROFILE_FILLED)) {
            profileData.setWhatNoIfNotDo(usersAnswer);
            profileData.setChatId(chatId);

            profileDataService.saveUserProfileData(profileData);

            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);

            String profileFilledMessage = messagesService.getReplyText("reply.profileFilled",
                    profileData.getWhatIfYes(), Emojis.SPARKLES);
            String predictionMessage = predictionService.getPrediction();

            replyToUser = new SendMessage(chatId, String.format("%s%n%n%s %s", profileFilledMessage, Emojis.SCROLL, predictionMessage));
            replyToUser.setParseMode("HTML");
        }

        userDataCache.saveUserProfileData(userId, profileData);

        return replyToUser;
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



