package com.mindwaresrl.common;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class AgentDinamicWebIT {

    @Test
    void givenRoundRobinStrategy_whenGetNextAgentIsCalled_thenAgentsRotateCorrectly() {
        int totalAgents = AgentDinamicWeb.getTotalAgents();
        Set<String> uniqueAgents = new HashSet<>();
        String firstAgent = null;

        // 1. Consume all unique agents in one cycle
        for (int i = 0; i < totalAgents; i++) {
            String currentAgent = AgentDinamicWeb.getNextAgent();
            uniqueAgents.add(currentAgent);

            if (i == 0) {
                firstAgent = currentAgent;
            }
        }

        // Verify we got 'totalAgents' unique strings
        assertThat(uniqueAgents).hasSize(totalAgents);

        // 2. Verify the cycle loops back to the start
        String nextAgentAfterCycle = AgentDinamicWeb.getNextAgent();
        assertThat(nextAgentAfterCycle).isEqualTo(firstAgent);
    }

    @Test
    void whenGetTotalAgentsIsCalled_thenReturnsPositiveNumber() {
        assertThat(AgentDinamicWeb.getTotalAgents()).isGreaterThan(0);
    }
}
