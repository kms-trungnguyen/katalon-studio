package com.kms.katalon.application;

public enum RunningMode {
    GUI("gui"),
    CONSOLE("console");
    
    private final String mode;
    
    private RunningMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }
}
