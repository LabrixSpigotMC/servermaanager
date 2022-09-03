package net.cayoe.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_16_R3.NBTBase;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemBuilder(final Material material){
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(final Material material, final String name){
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();

        assert itemMeta != null;
        itemMeta.setDisplayName(name);
    }

    public ItemBuilder(final ItemStack itemStack){
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder setDisplayName(final String name){
        itemMeta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setAmount(final int amount){
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder clearLore() {
        this.itemMeta.setLore(new LinkedList<String>());
        this.itemStack.setItemMeta(this.itemMeta);

        return this;
    }

    public ItemBuilder addLore(String... lore) {
        List<String> list = ((this.itemMeta.hasLore()) ? this.itemMeta.getLore() : new LinkedList<>());
        list.addAll(Arrays.asList(lore));

        this.itemMeta.setLore(list);
        this.itemStack.setItemMeta(this.itemMeta);

        return this;
    }

    public ItemBuilder removeLore(String... lore) {
        List<String> list = ((this.itemMeta.hasLore()) ? this.itemMeta.getLore() : new LinkedList<String>());
        list.removeAll(Arrays.asList(lore));

        this.itemMeta.setLore(list);
        this.itemStack.setItemMeta(this.itemMeta);

        return this;
    }

    public ItemBuilder setLore(String... lore) {
        this.itemMeta.setLore(Arrays.asList(lore));
        this.itemStack.setItemMeta(this.itemMeta);

        return this;
    }

    public ItemBuilder setDurability(final short durability){
        itemStack.setDurability(durability);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        this.itemMeta.addEnchant(enchantment, level, ignoreLevelRestriction);
        this.itemStack.setItemMeta(this.itemMeta);

        return this;
    }

    public ItemBuilder removeEnchant(Enchantment enchantment) {
        this.itemMeta.removeEnchant(enchantment);
        this.itemStack.setItemMeta(this.itemMeta);

        return this;
    }

    public boolean hasEnchants() {
        return this.itemMeta.hasEnchants();
    }

    public boolean hasEnchant(Enchantment enchantment) {
        return this.itemMeta.hasEnchant(enchantment);
    }

    public int getEnchantLevel(Enchantment enchantment) {
        return this.itemMeta.getEnchantLevel(enchantment);
    }

    public ItemStack getCurrencyItemStack(){
        return this.itemStack;
    }

    public ItemBuilder addItemFlags(ItemFlag... itemFlags) {
        this.itemMeta.addItemFlags(itemFlags);
        this.itemStack.setItemMeta(this.itemMeta);

        return this;
    }

    public ItemBuilder removeItemFlags(ItemFlag... itemFlags) {
        this.itemMeta.removeItemFlags(itemFlags);
        this.itemStack.setItemMeta(this.itemMeta);

        return this;
    }

    public ItemBuilder setOwner(String owner) {
        SkullMeta metadata = ((SkullMeta) this.itemMeta);
        metadata.setOwner(owner);
        this.itemStack.setItemMeta(metadata);
        return this;
    }

    private boolean isSkull() {
        return this.itemStack.getType().equals(Material.PLAYER_HEAD);
    }

    private  final String JSON_SKIN = "{\"isPublic\":true,\"textures\":{\"SKIN\":{\"url\":\"https://textures.minecraft.net/texture/%s\"}}}";

    public ItemBuilder setSkullTexture(String texture, final String name) {
        return new Spigot(this).setSkullTexture(texture).setDisplayName(name);
    }

    public boolean hasItemFlag(ItemFlag itemFlag) {
        return this.itemMeta.hasItemFlag(itemFlag);
    }

    public Set<ItemFlag> getItemFlags() {
        return this.itemMeta.getItemFlags();
    }

    public boolean hasLore() {
        return this.itemMeta.hasLore();
    }

    public List<String> getLore() {
        return ((this.itemMeta.getLore() == null) ? Collections.emptyList() : this.itemMeta.getLore());
    }

    public int getAmount(){
        return itemStack.getAmount();
    }

    public short getDurability(){
        return itemStack.getDurability();
    }

    public Material getType(){
        return itemStack.getType();
    }

    public boolean isPlayerSkull(){
        return itemStack.getType().equals(Material.PLAYER_HEAD);
    }

    public ItemMeta getItemMeta(){
        return itemMeta;
    }

    public void setMeta(final ItemMeta itemMeta){
        itemStack.setItemMeta(itemMeta);
    }

    public ItemStack build(){
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static class Spigot {

        private final ItemBuilder builder;

        public Spigot(ItemBuilder builder) {
            this.builder = builder;
        }

        public ItemBuilder setSkullTexture(UUID uuid) {
            if(this.builder.isPlayerSkull()) {
                SkullMeta metadata = ((SkullMeta) this.builder.getItemMeta());

                try {
                    Field field = metadata.getClass().getDeclaredField("profile");
                    field.setAccessible(true);

                    field.set(metadata, GameProfileBuilder.fetch(uuid));
                    field.setAccessible(false);
                } catch (IllegalAccessException | NoSuchFieldException | IOException e) {
                    e.printStackTrace();
                }
                this.builder.itemStack.setItemMeta(metadata);
                this.builder.itemStack.setItemMeta(this.builder.getItemMeta());
            }
            return this.builder;
        }

        private  final String JSON_SKIN = "{\"isPublic\":true,\"textures\":{\"SKIN\":{\"url\":\"https://textures.minecraft.net/texture/%s\"}}}";

        public ItemBuilder setSkullTexture(String texture) {
            if(this.builder.isPlayerSkull()) {
                SkullMeta metadata = ((SkullMeta) this.builder.getItemMeta());

                GameProfile profile = new GameProfile(UUID.randomUUID(), "");
                profile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString(String.format(JSON_SKIN, texture))));

                try {
                    Field field = metadata.getClass().getDeclaredField("profile");
                    field.setAccessible(true);
                    field.set(metadata, profile);
                    field.setAccessible(false);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
                this.builder.itemStack.setItemMeta(metadata);
                this.builder.itemStack.setItemMeta(this.builder.getItemMeta());
            }
            return this.builder;
        }

        public ItemBuilder removeNBT(String key) {
            ItemMeta itemMeta = builder.getItemMeta();

            net.minecraft.server.v1_16_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(this.builder.itemStack);
            NBTTagCompound compound = ((itemStack.hasTag()) ? itemStack.getTag() : new NBTTagCompound());
            compound.remove(key);
            itemStack.setTag(compound);

            builder.setMeta(CraftItemStack.getItemMeta(itemStack));
            this.builder.itemStack.setItemMeta(itemMeta);
            return this.builder;
        }

        public boolean hasNBT(String key) {
            net.minecraft.server.v1_16_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(this.builder.itemStack);
            NBTTagCompound compound = ((itemStack.hasTag()) ? itemStack.getTag() : new NBTTagCompound());
            return compound.hasKey(key);
        }

        public String getNBTString(String key) {
            net.minecraft.server.v1_16_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(this.builder.itemStack);
            NBTTagCompound compound = ((itemStack.hasTag()) ? itemStack.getTag() : new NBTTagCompound());
            return compound.getString(key);
        }

        public boolean getNBTBoolean(String key) {
            net.minecraft.server.v1_16_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(this.builder.itemStack);
            NBTTagCompound compound = ((itemStack.hasTag()) ? itemStack.getTag() : new NBTTagCompound());
            return compound.getBoolean(key);
        }

        public int getNBTInt(String key) {
            net.minecraft.server.v1_16_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(this.builder.itemStack);
            NBTTagCompound compound = ((itemStack.hasTag()) ? itemStack.getTag() : new NBTTagCompound());
            return compound.getInt(key);
        }

        public double getNBTDouble(String key) {
            net.minecraft.server.v1_16_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(this.builder.itemStack);
            NBTTagCompound compound = ((itemStack.hasTag()) ? itemStack.getTag() : new NBTTagCompound());
            return compound.getDouble(key);
        }

        public double getNBTLong(String key) {
            net.minecraft.server.v1_16_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(this.builder.itemStack);
            NBTTagCompound compound = ((itemStack.hasTag()) ? itemStack.getTag() : new NBTTagCompound());
            return compound.getLong(key);
        }

        public float getNBTFloat(String key) {
            net.minecraft.server.v1_16_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(this.builder.itemStack);
            NBTTagCompound compound = ((itemStack.hasTag()) ? itemStack.getTag() : new NBTTagCompound());
            return compound.getFloat(key);
        }

        public ItemBuilder setNBT(String key, NBTBase base) {
            net.minecraft.server.v1_16_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(this.builder.itemStack);
            NBTTagCompound compound = ((itemStack.hasTag()) ? itemStack.getTag() : new NBTTagCompound());
            compound.set(key, base);
            itemStack.setTag(compound);

            builder.setMeta(CraftItemStack.getItemMeta(itemStack));
//            this.builder.getItemMeta() = CraftItemStack.getItemMeta(itemStack);
            this.builder.itemStack.setItemMeta(this.builder.getItemMeta());
            return this.builder;
        }
    }

}
