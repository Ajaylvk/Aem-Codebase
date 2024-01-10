package com.softwareag.core.services.domains;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisteredDomainsConfigServiceTest {

    private static final String[] REGISTERED_DOMAINS = {"https://www.softwareag.com", "https://techcommunity.softwareag.com"};

    private RegisteredDomainsService serviceUnderTest;

    @Mock
    private RegisteredDomainsService.Config mockConfig;

    @BeforeEach
    public void setUp() {
        serviceUnderTest = new RegisteredDomainsService();
    }

    @Test
    void getRegisteredDomains_returnsConfigString() {
        when(mockConfig.registeredDomains()).thenReturn(REGISTERED_DOMAINS);
        serviceUnderTest.activate(mockConfig);

        List<String> actualResult = serviceUnderTest.getRegisteredDomains();

        assertEquals(2, actualResult.size());
    }
}
