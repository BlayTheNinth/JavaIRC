package net.blay09.javairc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IRCParser {

    private final List<String> args = new ArrayList<>();
    private String data;
    private int idx;

    public IRCMessage parse(String data) {
        reset(data);
        Map<String, String> tagMap = null;
        if (data.startsWith("@")) {
            idx++;
            String tags = nextToken();
            String[] splitTags = tags.split(";");
            tagMap = new HashMap<>(splitTags.length);
            for (String tagPair : splitTags) {
                int eqIdx = tagPair.indexOf('=');
                if (eqIdx != -1) {
                    String value = tagPair.substring(eqIdx + 1);
                    value = value.replace("\\s", " ");
                    value = value.replace("\\:", ";");
                    value = value.replace("\\n", "\n");
                    value = value.replace("\\r", "\r");
                    value = value.replace("\\\\", "\\");
                    tagMap.put(tagPair.substring(0, eqIdx), value);
                } else {
                    tagMap.put(tagPair, "");
                }
            }
        }
        String prefix;
        String cmd;
        if (data.startsWith(":", idx)) {
            idx++;
            prefix = nextToken();
        } else {
            prefix = "";
        }
        cmd = nextToken();

        String arg;
        while ((arg = nextToken()) != null) {
            args.add(arg);
        }
        return new IRCMessage(tagMap, prefix, cmd, args.toArray(new String[0]));
    }

    public void reset(String data) {
        this.data = data;
        idx = 0;
        args.clear();
    }

    public String nextToken() {
        if (idx >= data.length() - 1) {
            return null;
        }
        int nextIdx;
        if (data.charAt(idx) == ':') {
            idx++;
            nextIdx = data.length();
        } else {
            nextIdx = data.indexOf(' ', idx);
            if (nextIdx == -1) {
                nextIdx = data.length();
            }
        }
        String token = data.substring(idx, nextIdx);
        idx = nextIdx + 1;
        return token;
    }

}
