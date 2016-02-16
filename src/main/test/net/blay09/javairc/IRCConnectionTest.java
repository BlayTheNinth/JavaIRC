package net.blay09.javairc;

import org.junit.Test;

import static org.junit.Assert.*;

public class IRCConnectionTest {

    @Test
    public void testIRCConnection() throws Exception {
        Configuration configuration = Configuration.builder()
                .server("irc.esper.net")
                .nick("BalyChan")
                .debug(true)
                .autoJoinChannel("#BalyWare")
                .build();
        IRCConnection connection = new IRCConnection(configuration, new IRCAdapter() {
            @Override
            public void onConnected(IRCConnection connection) {
                connection.quit("Bye!");
            }
        });
        connection.start();
        int timeout = 0;
        while(connection.isRunning()) {
            Thread.sleep(1000);
            timeout++;
            if(timeout >= 20) {
                fail("Timed out.");
            }
        }
    }
}