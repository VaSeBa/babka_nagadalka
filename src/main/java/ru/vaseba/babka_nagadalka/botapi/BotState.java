package ru.vaseba.babka_nagadalka.botapi;

/**Возможные состояния бота
 */

public enum BotState {
    ASK_DESTINY,
    ASK_NAME,
    ASK_AGE,
    ASK_GENDER,
    ASK_COLOR,
    ASK_NUMBER,
    ASK_MOVIE,
    ASK_SONG,
    FILLING_PROFILE,
    PROFILE_FILLED,
    SHOW_USER_PROFILE,
    SHOW_MAIN_MENU,
    SHOW_HELP_MENU,
    ARE_YOU_READY,
    ASK_WHAT_IF_YES,
    ASK_WHAT_IF_NO,
    ASK_WHAT_NO_IF_DO,
    ASK_WHAT_NO_IF_NOT_DO;

}
