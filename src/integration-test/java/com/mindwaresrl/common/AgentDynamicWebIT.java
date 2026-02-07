package com.mindwaresrl.common;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class AgentDynamicWebIT {

    @Test
    void givenRoundRobinStrategy_whenGetNextAgentIsCalled_thenAgentsRotateCorrectly() {
        int totalAgents = AgentDynamicWeb.getTotalAgents();
        Set<String> uniqueAgents = new HashSet<>();
        String firstAgent = null;

        // 1. Consume all unique agents in one cycle
        for (int i = 0; i < totalAgents; i++) {
            String currentAgent = AgentDynamicWeb.getNextAgent();
            uniqueAgents.add(currentAgent);

            if (i == 0) {
                firstAgent = currentAgent;
            }
        }

        // Verify we got 'totalAgents' unique strings
        assertThat(uniqueAgents).hasSize(totalAgents);

        // 2. Verify the cycle loops back to the start
        String nextAgentAfterCycle = AgentDynamicWeb.getNextAgent();
        assertThat(nextAgentAfterCycle).isEqualTo(firstAgent);
    }

    @Test
    void whenCreateProfileIsCalled_thenReturnsValidBrowserContextOptions() {
        var profile = AgentDynamicWeb.createProfile();

        assertThat(profile).isNotNull();
        assertThat(profile.userAgent).isNotNull();
        assertThat(profile.locale).isEqualTo("es-419");
        assertThat(profile.timezoneId).isEqualTo("America/La_Paz");
    }

    @Test
    void whenGetAllAgents_thenTheyShouldBeChromiumBased() {
        int totalAgents = AgentDynamicWeb.getTotalAgents();

        for (int i = 0; i < totalAgents; i++) {
            String agent = AgentDynamicWeb.getNextAgent();

            // Verify it's NOT Firefox
            assertThat(agent).doesNotContain("Firefox");

            // Verify it's either Chrome or Edge (contains 'Chrome' or 'Edg')
            assertThat(agent).satisfiesAnyOf(
                    ua -> assertThat(ua).contains("Chrome"),
                    ua -> assertThat(ua).contains("Edg"));
        }
    }

    @Test
    void whenGetTotalAgentsIsCalled_thenReturnsPositiveNumber() {
        // This confirms either API loaded or Fallback list was used
        assertThat(AgentDynamicWeb.getTotalAgents()).isGreaterThan(0);
    }
}
