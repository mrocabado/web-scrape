package com.mindwaresrl.common;
import java.util.List;
import java.util.Random;
import com.mindwaresrl.model.WebScrapeRequest;


class RandomUserAgentStrategy implements UserAgentStrategy{
    private static final List<String> AGENTS = List.of(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/119.0"
    );
    @Override
    public String getUserAgent(){
        return AGENTS.get(new Random().nextInt(AGENTS.size()));
    }
}
class MobileUserAgentStrategy implements UserAgentStrategy {
    @Override
    public String getUserAgent() {
        return "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Mobile Safari/537.36";
    }
}


public class user_agent_update {
    public static UserAgentStrategy getStrategy(WebScrapeRequest request) {
        if (request.url().toString().contains("m.")) {
            return new MobileUserAgentStrategy();
        }
        return new RandomUserAgentStrategy();
    }
}
