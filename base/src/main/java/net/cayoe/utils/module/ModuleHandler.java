package net.cayoe.utils.module;

import java.util.List;

public interface ModuleHandler {

    /**
     * Execute a module
     * @param module specific
     */
    void executeModule(final Module module);

    /**
     * Register a module
     * @param module to register
     */
    void registerModule(final Module module);

    /**
     * Register modules
     * @param module to register
     */
    void registerModules(final Module... module);

    /**
     * Unload a specific module
     * @param module specific module
     */
    void unloadModule(final Module module);

    /**
     * Reload a specific module
     * @param module specific module
     */
    void reloadModule(final Module module);

    /**
     * Deactivate all active modules
     */
    void unloadModules();

    /**
     * Reload all modules
     */
    void reloadModules();

    /**
     * Checks whether a module is loaded.
     * @return if loaded or not
     */
    boolean isModuleEnabled(final Module module);

    /**
     * Checks whether a module is loaded.
     * @return if loaded or not
     */
    boolean isModuleEnabled(final String moduleName);

    /**
     * Gets all cached modules
     * @return the cached modules
     */
    List<Module> getCachedModules();

    /**
     * Gets all modules
     * @return module list
     */
    List<Module> getModules();

    /**
     * Get a module through the real name
     * @param realName
     * @return
     */
    Module getModule(final String realName);
}
