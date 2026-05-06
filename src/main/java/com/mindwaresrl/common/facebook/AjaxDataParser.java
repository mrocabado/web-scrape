package com.mindwaresrl.common.facebook;

import com.mindwaresrl.model.facebook.PhotoData;
import com.mindwaresrl.model.facebook.ReactionData;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class AjaxDataParser {

    private static final String[] STANDARD_KEYS = {
            "like_count", "comment_count", "reaction_count", "share_count"
    };

    private static final Pattern TYPED_REACTION =
            Pattern.compile("\"reaction_type\":\"(\\w+)\".*?\"count\":(\\d+)");

    public void parseAll(List<String> bodies, PhotoData data) {
        log.info("=== Parseando {} respuestas AJAX ===", bodies.size());
        for (String body : bodies) {
            parseOne(clean(body), data);
        }
    }

    private String clean(String body) {
        return body.startsWith("for (;;);") ? body.substring(9) : body;
    }

    private void parseOne(String body, PhotoData data) {
        for (String key : STANDARD_KEYS) {
            extractStandard(body, key, data);
        }
        extractTypedReactions(body, data);
    }

    private void extractStandard(String body, String key, PhotoData data) {
        Pattern p = Pattern.compile("\"" + key + "\":\\{?\"?count\"?:(\\d+)");
        Matcher m = p.matcher(body);
        if (m.find()) {
            data.addReaction(new ReactionData(key, "ajax", m.group(1)));
        }
    }

    private void extractTypedReactions(String body, PhotoData data) {
        Matcher m = TYPED_REACTION.matcher(body);
        while (m.find()) {
            data.addReaction(new ReactionData(m.group(1), "ajax-typed", m.group(2)));
        }
    }
}