package net.blay09.javairc;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;

public class IRCMessage {

    private final Map<String, String> tags;
    private final String[] args;
    private @Getter String prefix;
    private @Getter String command;


    public IRCMessage(Map<String, String> tags, String prefix, String command, String[] args) {
        this.tags = tags;
        this.prefix = prefix;
        this.command = command;
        this.args = args;
    }

    public int getNumericCommand() {
        try {
            return Integer.parseInt(command);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public String getHostname() {
        int idx = prefix.indexOf("@");
        if(idx != -1 && idx + 1 < prefix.length()) {
            return prefix.substring(idx + 1);
        }
        return null;
    }

    public String getUsername() {
        int start = prefix.indexOf('!');
        int end = prefix.indexOf('@');
        if(end == -1) {
            end = prefix.length() - 1;
        }
        if(start != -1 && start + 1 < prefix.length()) {
            return prefix.substring(start + 1, end);
        }
        return null;
    }

    public String getNick() {
        int end = prefix.indexOf('!');
        if(end != -1) {
            return prefix.substring(0, end);
        }
        return prefix;
    }

    public IRCUser parseSender() {
        return new IRCUser(getNick(), getUsername(), getHostname());
    }

    public String arg(int idx) {
        if(idx >= args.length) {
            return null;
        }
        return args[idx];
    }

    public String[] subargs(int idx) {
        return Arrays.copyOfRange(args, idx, args.length);
    }

    public int argCount() {
        return args.length;
    }

    public String getTagByKey(String key) {
        return tags != null ? tags.get(key) : null;
    }
}
