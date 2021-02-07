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
public class HelpMenuHandler implements InputMessageHandler {
    private MainMenuService mainMenuService;
    private ReplyMessagesService messagesService;

    public HelpMenuHandler(MainMenuService mainMenuService, ReplyMessagesService messagesService) {
        this.mainMenuService = mainMenuService;
        this.messagesService = messagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        return mainMenuService.getMainMenuMessage(message.getChatId(),
                messagesService.getReplyText("reply.showHelpMenu", Emojis.MAGE));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_HELP_MENU;
    }
}
