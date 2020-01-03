package net.md_5.bungee.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * It offers multiple utilities for sending messages. These include sending messages to certain groups of players and also handling colors.
 *
 * @author Lucas2993
 * @author Valtn
 */
public class MessageUtils {
    // Used to identify the color
    public static final char COLOR_CHAR = '&';
    private static final boolean USING_COLORS_BY_DEFAULT = true;

    // Delimiter used to identify the part of a message that contains a clickable element.
    private static final String CLICK_TEXT_DELIMITER = "@";
    // String used as a mark for the area where a clickable component in a message would go.
    private static final String WILDCARD = "####";

    /**
     * Translate the colors of the message.
     *
     * @param text Message to translate.
     * @return The message with all its colors already translated.
     */
    public static String translateColors(String text) {
        // Replace each occurrence of the color character with the special character that is used for the color.
        return ChatColor.translateAlternateColorCodes(COLOR_CHAR, text);
    }

    /**
     * Translate the colors of the message.
     *
     * @param text Message to translate.
     * @return The message with all its colors already translated in the array format of BaseComponent.
     */
    public static BaseComponent[] translateColorsToComponent(String text) {
        return TextComponent.fromLegacyText(translateColors(text));
    }

    /**
     * Colors are removed within the given text (if any).
     *
     * @param text Text to review.
     * @return The text without colors.
     */
    public static String removeColors(String text) {
        return ChatColor.stripColor(text);
    }

    // WRAPERS

    public static void sendMessage(CommandSender sender, String message) {
        sendMessage(sender, message, USING_COLORS_BY_DEFAULT);
    }

    public static void sendGlobalMessage(List<String> messages) {
        sendGlobalMessage(messages, USING_COLORS_BY_DEFAULT);
    }

    public static void sendGlobalMessage(String message) {
        sendGlobalMessage(message, USING_COLORS_BY_DEFAULT);
    }

    public static void sendListPlayersMessage(List<ProxiedPlayer> players, List<String> messages) {
        sendListPlayersMessage(players, messages, USING_COLORS_BY_DEFAULT);
    }

    public static void sendListPlayersMessage(List<ProxiedPlayer> players, String message) {
        sendListPlayersMessage(players, message, USING_COLORS_BY_DEFAULT);
    }

    public static void sendPlayerMessages(ProxiedPlayer player, List<String> messages) {
        sendPlayerMessages(player, messages, USING_COLORS_BY_DEFAULT);
    }

    // Main Functionalities

    /**
     * Send a message to a specific server member.
     *
     * @param sender    Member to whom the message is sent.
     * @param message   Message to send.
     * @param useColors Indicates whether the colors that may be present are translated or not.
     */
    public static void sendMessage(CommandSender sender, String message, boolean useColors) {
        // If colors are used.
        if (useColors) {
            // Translate the colors within the message.
            message = translateColors(message);
        }

        // Send the message.
        sender.sendMessage(TextComponent.fromLegacyText(message));
    }

    /**
     * Send a set of messages globally.
     *
     * @param messages  List of messages to send.
     * @param useColors Indicates whether the colors that may be present are translated or not.
     */
    public static void sendGlobalMessage(List<String> messages, boolean useColors) {
        // For each message to send.
        for (String message : messages) {
            // Send the message globally.
            sendGlobalMessage(message, useColors);
        }
    }

    /**
     * Send a message globally.
     *
     * @param message   Message to send.
     * @param useColors Indicates whether the colors that may be present are translated or not.
     */
    public static void sendGlobalMessage(String message, boolean useColors) {
        // If colors are used.
        if (useColors) {
            // Translate the colors within the message.
            message = translateColors(message);
        }

        // Send the global message.
        ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(message));
    }

    /**
     * Send a set of messages to a set of players.
     *
     * @param players   Set of players who will receive the messages.
     * @param messages  Message to send.
     * @param useColors Indicates whether the colors that may be present are translated or not.
     */
    public static void sendListPlayersMessage(List<ProxiedPlayer> players, List<String> messages, boolean useColors) {
        // For each message to send.
        for (String message : messages) {
            // Send the message to the set of players.
            sendListPlayersMessage(players, message, useColors);
        }
    }

    /**
     * Send a message to a set of players.
     *
     * @param players   Set of players who will receive the message.
     * @param message   Message to send.
     * @param useColors Indicates whether the colors that may be present are translated or not.
     */
    public static void sendListPlayersMessage(List<ProxiedPlayer> players, String message, boolean useColors) {
        // For each player.
        for (ProxiedPlayer player : players) {
            // Send the message to the player.
            sendMessage(player, message, useColors);
        }
    }

    /**
     * Send a set of messages to a player.
     *
     * @param player    Player who will receive the messages.
     * @param messages  Messages to send.
     * @param useColors Indicates whether the colors that may be present are translated or not.
     */
    public static void sendPlayerMessages(ProxiedPlayer player, List<String> messages, boolean useColors) {
        // Create a list with the player.
        List<ProxiedPlayer> playerList = new ArrayList<>();
        playerList.add(player);

        // Send the message set effectively.
        sendListPlayersMessage(playerList, messages, useColors);
    }

    /**
     * Insert a clickable segment into the text of a message.
     *
     * @param message Message where the command is inserted.
     * @param command Command to insert
     * @return Array of BaseComponent that correspond to the message with the included button. If there is no button the message goes without modifications.
     */
    public static BaseComponent[] insertCommandInMessage(String message, String command) {
        // The text in which the command will be inserted is obtained.
        String commandString = StringUtils.substringBetween(message, CLICK_TEXT_DELIMITER);

        // If it could not be obtained.
        if (commandString == null) {
            // The message is returned unaltered with the translated colors.
            return MessageUtils.translateColorsToComponent(message);
        }

        // The segment where the text will go with the command is removed and replaced by an auxiliary string.
        String messageReplaced = message.replaceFirst(CLICK_TEXT_DELIMITER + "(.*?)" + CLICK_TEXT_DELIMITER, WILDCARD);

        // The message is divided according to the special chain.
        String[] splitted = messageReplaced.split(WILDCARD);

        // If the message divided for any reason was empty.
        if (ArrayUtils.isEmpty(splitted)) {
            // The message is returned unaltered with the translated colors.
            return MessageUtils.translateColorsToComponent(message);
        }

        // The first segment of the message is formatted.
        BaseComponent[] result = MessageUtils.translateColorsToComponent(splitted[0]);

        // It is removed from the array so that the final part of the message remains in the first position (if there is one).
        splitted = ArrayUtils.remove(splitted, 0);

        // A component is created with the text that will contain the command.
        BaseComponent commandComponent = new TextComponent(MessageUtils.translateColors(commandString));
        // The command is inserted into the component.
        commandComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));

        // The component is added at the end of the array.
        result = ArrayUtils.add(result, commandComponent);

        // If the message had another segment.
        if (!ArrayUtils.isEmpty(splitted)) {
            // The colors are translated and inserted at the end.
            result = ArrayUtils.addAll(result, MessageUtils.translateColorsToComponent(splitted[0]));
        }

        return result;
    }
}
