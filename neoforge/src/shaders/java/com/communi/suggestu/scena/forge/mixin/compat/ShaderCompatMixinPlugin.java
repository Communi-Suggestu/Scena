package com.communi.suggestu.scena.forge.mixin.compat;

import net.neoforged.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ShaderCompatMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("sodium")) {
            return !isModLoaded("embeddium") && isModLoaded("sodium") && isModLoaded("iris");
        }

        if (mixinClassName.contains("embeddium")) {
            return isModLoaded("embeddium") && isModLoaded("iris");
        }

        return false;
    }

    private static boolean isModLoaded(String modId) {
        return LoadingModList.get().getMods().stream()
                .anyMatch(mod -> mod.getModId().toLowerCase(Locale.ROOT).equals(modId));
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
