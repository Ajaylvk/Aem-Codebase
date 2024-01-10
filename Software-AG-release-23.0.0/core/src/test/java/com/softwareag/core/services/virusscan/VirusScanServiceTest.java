package com.softwareag.core.services.virusscan;

import fi.solita.clamav.ClamAVClient;
import fi.solita.clamav.ClamAVSizeLimitException;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VirusScanServiceTest {

    VirusScanService serviceUnderTest;

    @Mock
    ClamAVClient clamAVClient;

    @BeforeEach
    private void setUp() throws IOException, IllegalAccessException {
        VirusScanServiceConfig virusScanServiceConfig = mock(VirusScanServiceConfig.class);
        when(virusScanServiceConfig.getClamAvHost()).thenReturn("127.0.0.1");
        when(virusScanServiceConfig.getClamAvPort()).thenReturn(3310);
        when(virusScanServiceConfig.getClamAvTimeout()).thenReturn(3000);
        this.serviceUnderTest = new VirusScanService();
        this.serviceUnderTest.activate(virusScanServiceConfig);
        FieldUtils.writeField(serviceUnderTest, "clamAVClient", clamAVClient, true);
    }

    @Test
    void fileContainsVirus_throwsException_ifConfigIsMissing() {
        final VirusScanService service = new VirusScanService();

        assertThrows(VirusScanConfigurationMissingException.class, () ->
            service.fileContainsVirus(null));
    }

    @Test
    void fileContainsVirus_throwsException_ifHostIsEmpty() {
        VirusScanServiceConfig virusScanServiceConfig = mock(VirusScanServiceConfig.class);
        when(virusScanServiceConfig.getClamAvHost()).thenReturn("");

        final VirusScanService service = new VirusScanService();
        service.activate(virusScanServiceConfig);

        assertThrows(VirusScanConfigurationMissingException.class, () ->
            service.fileContainsVirus(null));
    }

    @Test
    void fileContainsVirus_throwsException_ifClamAVClientIsNotInstantiated() throws IllegalAccessException {
        FieldUtils.writeField(serviceUnderTest, "clamAVClient", null, true);

        assertThrows(VirusScanFailedException.class, () ->
            serviceUnderTest.fileContainsVirus(null));
    }

    @Test
    void fileContainsVirus_throwsException_ifServerIsNotAvailable() throws IOException, IllegalAccessException {
        when(clamAVClient.ping()).thenReturn(false);

        assertThrows(VirusScanFailedException.class, () ->
            serviceUnderTest.fileContainsVirus(null));
    }

    @Test
    void fileContainsVirus_throwsException_ifAnErrorOccursDuringTheScan() throws IOException {
        when(clamAVClient.scan(any(InputStream.class))).thenThrow(ClamAVSizeLimitException.class);
        when(clamAVClient.ping()).thenReturn(true);

        assertThrows(VirusScanFailedException.class, () ->
            serviceUnderTest.fileContainsVirus(mock(InputStream.class)));
    }

    @Test
    void fileContainsVirus_returnsTrue_ifGivenInputStreamIsNull() throws IOException, IllegalAccessException {
        when(clamAVClient.ping()).thenReturn(true);

        boolean result = this.serviceUnderTest.fileContainsVirus(null);

        assertTrue(result);
    }

    @Test
    void fileContainsVirus_returnsFalse_ifGivenInputStreamIsACleanAsset() throws IOException, IllegalAccessException {
        URL fileUrl = this.getClass().getResource("sag-logo.png");
        File inputFile = new File(fileUrl.getPath());
        InputStream is = new FileInputStream(inputFile);
        byte[] resultBytes = {115, 116, 114, 101, 97, 109, 58, 32, 79, 75, 0};

        when(clamAVClient.scan(is)).thenReturn(resultBytes);
        when(clamAVClient.ping()).thenReturn(true);

        boolean result = this.serviceUnderTest.fileContainsVirus(is);

        is.close();

        assertFalse(result);
    }
}
