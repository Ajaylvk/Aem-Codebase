package com.softwareag.core.services.virusscan;

/**
 * Exception thrown if the {@link VirusScanServiceConfig} is misconfigured.
 */
public class VirusScanConfigurationMissingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public VirusScanConfigurationMissingException(String message) {
        super(message);
    }

}
