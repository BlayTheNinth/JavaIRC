package net.blay09.javairc;

import lombok.Value;

@Value
public class IRCUser {
    private String nick;
    private String username;
    private String hostname;
}
