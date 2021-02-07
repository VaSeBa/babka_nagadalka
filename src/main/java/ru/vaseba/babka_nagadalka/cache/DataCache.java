package ru.vaseba.babka_nagadalka.cache;


import ru.vaseba.babka_nagadalka.botapi.BotState;
import ru.vaseba.babka_nagadalka.model.UserProfileData;

public interface DataCache {
    void setUsersCurrentBotState(int userId, BotState botState);

    BotState getUsersCurrentBotState(int userId);

    UserProfileData getUserProfileData(int userId);

    void saveUserProfileData(int userId, UserProfileData userProfileData);
}
