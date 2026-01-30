package com.mindwaresrl.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AgentDinamicWebIT {

    @Test
    void givenMultipleCalls_whenGetNextAgentIsInvoked_thenAgentsAreRotatedCorrectly() {
        String agent1 = AgentDinamicWeb.getNextAgent();
        String agent2 = AgentDinamicWeb.getNextAgent();
        String agent3 = AgentDinamicWeb.getNextAgent();
        String agent4 = AgentDinamicWeb.getNextAgent();

        assertThat(agent1).isNotEqualTo(agent2);
        assertThat(agent2).isNotEqualTo(agent3);
        assertThat(agent3).isNotEqualTo(agent4);
        assertThat(agent4).isEqualTo(agent1);
    }
}
