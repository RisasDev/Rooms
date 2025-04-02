package dev.risas.nokrooms.utilities;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
public class ChatUtil {

    public final String NORMAL_LINE = "&7&m-----------------------------";
    private final char COLOR_CHAR = ChatColor.COLOR_CHAR;

    public String translate(String text) {
        Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(text);
        StringBuilder buffer = new StringBuilder(text.length() + 4 * 8);

        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public String[] translate(String[] text) {
        for (int i = 0; i < text.length; i++) {
            text[i] = translate(text[i]);
        }
        return text;
    }

    public List<String> translate(List<String> list) {
        return list.stream().map(ChatUtil::translate).collect(Collectors.toList());
    }

    public void sendMessage(CommandSender sender, String text) {
        sender.sendMessage(translate(text));
    }

    public void sendMessage(CommandSender sender, String[] text) {
        sender.sendMessage(translate(text));
    }
}
