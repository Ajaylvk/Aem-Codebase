package com.softwareag.core.services.virusscan;

/**
 * Exception thrown if a virus scan is not possible.
 * This can be caused if the server is not available or another error occurs during the virus scan.
 */
public class VirusScanFailedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public VirusScanFailedException(String message) {
        super(message);
    }

    public VirusScanFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
