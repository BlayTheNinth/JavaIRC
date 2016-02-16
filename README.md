# JavaIRC
Lightweight IRC library for Java.

This library is still a work-in-progress and is not recommended for use in production environments yet.
Until the first public release, the API may change at any time.

**Features**
* Simple, straight-forward API
* No runtime dependencies on other libraries
* SSL Support
* IRCv3 Capability Support
* IRCv3 Message Tags Support

**Example Usage**
```java
import net.blay09.javairc.Configuration;
import net.blay09.javairc.IRCConnection;
import net.blay09.javairc.IRCAdapter;

public class JavaIRCExample extends IRCAdapter {

    @Override
    void onChannelChat(IRCConnection connection, IRCMessage message, IRCUser user, String channel, String text) {
        if(text.startsWith("!test")) {
            connection.privmsg(channel, "It works!");
        }
    }

    public static void main(String[] args) {
        Configuration configuration = Configuration.builder()
                .server("irc.esper.net")
                .nick("ChangeMe")
                .autoJoinChannel("#BalyWare")
                .debug(true)
                .build();
        IRCConnection connection = new IRCConnection(configuration, new JavaIRCExample());
        connection.start();
    }
}
```