package com.softwareag.core.services.virusscan;



import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.solita.clamav.ClamAVClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * The class defines a Virus Scan Service that consumes ClamAV.
 */
@Component(service = VirusScanService.class)
@Designate(ocd = VirusScanServiceConfig.class)
@ServiceDescription("Virus Scanner Service")

public class VirusScanService {
    private static final Logger LOG = LoggerFactory.getLogger(VirusScanService.class);

    private VirusScanServiceConfig config;

    private ClamAVClient clamAVClient;

    @Activate
    public void activate(final VirusScanServiceConfig config) {
        if (config != null) {
            this.config = config;
            this.clamAVClient = new ClamAVClient(this.config.getClamAvHost(), this.config.getClamAvPort(), this.config.getClamAvTimeout());
        }
    }

    /**
     * Checks if the given {@link InputStream} contains a virus.
     *
     * @param is
     *     file stream to check for virus.
     * @return {@code true} if a virus is detected, otherwise {@code false}.
     * @throws VirusScanFailedException
     *     occurs if the virus scan failed.
     * @throws VirusScanConfigurationMissingException
     *     occurs if the virus scan service is misconfigured.
     */
    public boolean fileContainsVirus(final InputStream is) {
        if (this.config != null && StringUtils.isNotBlank(this.config.getClamAvHost())) {
            if (ping()) {
                boolean isClean = checkFileStreamForVirus(is);
                // return inverted result
                return !isClean;
            } else {
                throw new VirusScanFailedException("ClamAV server is not available.");
            }
        } else {
            throw new VirusScanConfigurationMissingException("ClamAV OSGi configuration is missing.");
        }
    }

    /**
     * Checks the status of availability of the virus scanner server.
     *
     * @return {@code true} if the server is available, otherwise {@code false}.
     */
    public boolean ping() {
        boolean isClamAvDaemonConnectable = false;
        if (this.clamAVClient != null) {
            try {
                isClamAvDaemonConnectable = this.clamAVClient.ping();
            } catch (IOException | RuntimeException e) {
                LOG.error("Could not connect to ClamAV with host: '{}', port: '{}', timeout: '{}'.",
                    this.config.getClamAvHost(), this.config.getClamAvPort(), this.config.getClamAvTimeout(), e);
            }
        }
        return isClamAvDaemonConnectable;
    }

    private boolean checkFileStreamForVirus(final InputStream is) {
        boolean isClean = false;

        if (this.clamAVClient != null && is != null) {
            byte[] scan;
            try {
                scan = this.clamAVClient.scan(is);
            } catch (IOException | RuntimeException e) {
                throw new VirusScanFailedException("ClamAV virus scan failed.", e);
            }

            if (scan != null) {
                String resultString = new String(scan, StandardCharsets.US_ASCII);
                LOG.info("ClamAV scan result: '{}'", resultString);

                isClean = ClamAVClient.isCleanReply(scan);
                LOG.info("ClamAV virus scan completed. isCleanReply: '{}'", isClean);
            } else {
                LOG.info("ClamAV scan result is null");
            }
        }

        return isClean;
    }
}
