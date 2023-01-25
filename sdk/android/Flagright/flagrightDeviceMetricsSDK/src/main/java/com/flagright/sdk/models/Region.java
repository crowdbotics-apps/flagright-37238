package com.flagright.sdk.models;

/**
 * Allowed Regions
 */
public enum Region {
    US1("us-1"),
    EU1("eu-1"),
    EU2("eu-2"),
    ASIA1("asia-1"),
    ASIA2("asia-2");

    private final String text;

    /**
     * @param text
     */
    Region(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}
