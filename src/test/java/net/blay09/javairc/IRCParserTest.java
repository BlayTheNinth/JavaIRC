package net.blay09.javairc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IRCParserTest {

    @Test
    public void testParse() throws Exception {
        IRCParser parser = new IRCParser();
        IRCMessage message;

        message = parser.parse(":irc.esper.net 372 BalyBot :Who reads these anyways?");
        assertEquals("irc.esper.net", message.getPrefix());
        assertEquals(372, message.getNumericCommand());
        assertEquals("BalyBot", message.arg(0));
        assertEquals("Who reads these anyways?", message.arg(1));

        message = parser.parse(":BlayTheNinth!~blay09@blay09.net PRIVMSG #BalyWare :Hello BalyBot!");
        assertEquals("BlayTheNinth!~blay09@blay09.net", message.getPrefix());
        assertEquals("BlayTheNinth", message.getNick());
        assertEquals("~blay09", message.getUsername());
        assertEquals("blay09.net", message.getHostname());
        assertEquals("PRIVMSG", message.getCommand());
        assertEquals(0, message.getNumericCommand());
        assertEquals("#BalyWare", message.arg(0));
        assertEquals("Hello BalyBot!", message.arg(1));

        message = parser.parse(":BlayTheNinth!~blay09@blay09.net PRIVMSG #BalyWare :\\u000314,01\\u001fformatted text\\u001f\\u0003");
        assertEquals("BlayTheNinth!~blay09@blay09.net", message.getPrefix());
        assertEquals("BlayTheNinth", message.getNick());
        assertEquals("~blay09", message.getUsername());
        assertEquals("blay09.net", message.getHostname());
        assertEquals("PRIVMSG", message.getCommand());
        assertEquals(0, message.getNumericCommand());
        assertEquals("#BalyWare", message.arg(0));
        assertEquals("\\u000314,01\\u001fformatted text\\u001f\\u0003", message.arg(1));

        message = parser.parse("@color=#0D4200;display-name=TWITCH\\sUserNaME;emotes=25:0-4,12-16/1902:6-10;mod=0;subscriber=0;turbo=1;user-id=1337;user-type=global_mod :twitch_username!twitch_username@twitch_username.tmi.twitch.tv PRIVMSG #channel :Kappa Keepo Kappa");
        assertEquals("#0D4200", message.getTagByKey("color"));
        assertEquals("TWITCH UserNaME", message.getTagByKey("display-name"));
        assertEquals("25:0-4,12-16/1902:6-10", message.getTagByKey("emotes"));
        assertEquals("0", message.getTagByKey("mod"));
        assertEquals("0", message.getTagByKey("subscriber"));
        assertEquals("1", message.getTagByKey("turbo"));
        assertEquals("1337", message.getTagByKey("user-id"));
        assertEquals("global_mod", message.getTagByKey("user-type"));
        assertEquals("twitch_username!twitch_username@twitch_username.tmi.twitch.tv", message.getPrefix());
        assertEquals("twitch_username", message.getNick());
        assertEquals("twitch_username", message.getUsername());
        assertEquals("twitch_username.tmi.twitch.tv", message.getHostname());
        assertEquals("PRIVMSG", message.getCommand());
        assertEquals(0, message.getNumericCommand());
        assertEquals("#channel", message.arg(0));
        assertEquals("Kappa Keepo Kappa", message.arg(1));
    }
}
