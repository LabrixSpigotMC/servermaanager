package net.cayoe.utils.module;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

public class SimpleModuleHandler implements ModuleHandler{

    public List<Module> modules;
    public List<Module> cachedModules;

    public SimpleModuleHandler(){
        modules = Lists.newArrayList();
        cachedModules = Lists.newArrayList();
    }

    @Override
    public void executeModule(Module module) {
        module.onLoad();
    }

    @Override
    public void registerModule(Module module) {
        modules.add(module);
        cachedModules.add(module);

        executeModule(module);
    }

    @Override
    public void registerModules(Module... module) {
        modules.addAll(Arrays.asList(module));
        cachedModules.addAll(Arrays.asList(module));

        final Module[] modulesList = module;

        for (Module modules : modulesList)
            executeModule(modules);
    }

    @Override
    public void unloadModule(Module module) {
        cachedModules.remove(module);
    }

    @Override
    public void reloadModule(Module module) {
        cachedModules.remove(module);
        cachedModules.add(module);
    }

    @Override
    public void unloadModules() {
        cachedModules.forEach(cachedModules::remove);
    }

    @Override
    public void reloadModules() {
        cachedModules.forEach(modules -> {

            cachedModules.remove(modules);
            cachedModules.add(modules);

        });
    }

    @Override
    public boolean isModuleEnabled(Module module) {
        for (Module cachedModule : cachedModules)
            if(cachedModule.equals(module))
                return true;
        return false;
    }

    @Override
    public boolean isModuleEnabled(String moduleName) {
        for (Module cachedModule : cachedModules)
            if(cachedModule.name().equals(moduleName))
                return true;
        return false;    }

    @Override
    public List<Module> getCachedModules() {
        return cachedModules;
    }

    @Override
    public List<Module> getModules() {
        return modules;
    }

    @Override
    public Module getModule(String name) {
        for (Module cachedModule : getCachedModules())
            if(cachedModule.realName().equals(name))
                return cachedModule;
        return null;
    }
}
