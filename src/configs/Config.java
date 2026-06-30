package configs;

/**
 * Common configuration lifecycle contract for loading, identifying, and closing a config.
 */
public interface Config {
    /**
     * Creates or loads the configuration content.
     */
    void create();

    /**
     * Returns the configuration name.
     *
     * @return the configuration name
     */
    String getName();

    /**
     * Returns the configuration version.
     *
     * @return the configuration version
     */
    int getVersion();

    /**
     * Releases resources associated with the configuration.
     */
    void close();
}
