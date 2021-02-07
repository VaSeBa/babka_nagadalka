package ru.vaseba.babka_nagadalka.botapi.handlers.menu;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.vaseba.babka_nagadalka.botapi.BotState;
import ru.vaseba.babka_nagadalka.botapi.InputMessageHandler;
import ru.vaseba.babka_nagadalka.cache.UserDataCache;
import ru.vaseba.babka_nagadalka.model.UserProfileData;
import ru.vaseba.babka_nagadalka.service.UsersProfileDataService;


@Component
public class ShowProfileHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private UsersProfileDataService profileDataService;

    public ShowProfileHandler(UserDataCache userDataCache, UsersProfileDataService profileDataService) {
        this.userDataCache = userDataCache;
        this.profileDataService = profileDataService;
    }

    @Override
    public SendMessage handle(Message message) {
        SendMessage userReply;
        final int userId = message.getFrom().getId();
        final UserProfileData profileData = profileDataService.getUserProfileData(message.getChatId());

        userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
        if (profileData != null) {
            userReply = new SendMessage(message.getChatId(),
                    String.format("%s%n-------------------%n%s", "Теперь осталось понять, что из этого для тебя важнее:", profileData.toString()));
        } else {
            userReply = new SendMessage(message.getChatId(), "Ты еще не ответил на вопросы!");
        }

        return userReply;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_USER_PROFILE;
    }
}
