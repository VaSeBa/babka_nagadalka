package ru.vaseba.babka_nagadalka.botapi.handlers.menu;


import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.vaseba.babka_nagadalka.botapi.BotState;
import ru.vaseba.babka_nagadalka.botapi.InputMessageHandler;
import ru.vaseba.babka_nagadalka.service.MainMenuService;
import ru.vaseba.babka_nagadalka.service.ReplyMessagesService;
import ru.vaseba.babka_nagadalka.utils.Emojis;


@Component
public class MainMenuHandler implements InputMessageHandler {
    private ReplyMessagesService messagesService;
    private MainMenuService mainMenuService;

    public MainMenuHandler(ReplyMessagesService messagesService, MainMenuService mainMenuService) {
        this.messagesService = messagesService;
        this.mainMenuService = mainMenuService;
    }

    @Override
    public SendMessage handle(Message message) {
        return mainMenuService.getMainMenuMessage(message.getChatId(), messagesService.getReplyText("reply.showMainMenu", Emojis.MAGE));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_MAIN_MENU;
    }


}
