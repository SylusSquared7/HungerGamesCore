package dev.sylus.HungerGamesCore.Game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class LootItem {
    private final Material material;
    private final String customName;
    private final Map<Enchantment, Integer> enchantmentToLevelMap = new HashMap<>();
    private final double chance;
    private final int minAmount;
    private final int maxAmount;
    ItemStack itemStack;
    public LootItem(ConfigurationSection section) {
        Material material;

        try {
            material = Material.valueOf(section.getString("material"));
        } catch (Exception e) {
            material = Material.AIR;
        }

        this.material = material;
        this.customName = section.getString("name");

        ConfigurationSection enchantmentsSection = section.getConfigurationSection("enchantments");
        if (enchantmentsSection != null) {
            for (String enchantmentKey : enchantmentsSection.getKeys(false)) {
                Enchantment enchantment = Enchantment.getByKey(
                        NamespacedKey.minecraft(
                                enchantmentKey.toLowerCase()
                        )
                );

                if (enchantment != null) {
                    int level = enchantmentsSection.getInt(enchantmentKey);
                    enchantmentToLevelMap.put(enchantment, level);
                }
            }
        }
        this.chance = section.getDouble("chance");
        this.minAmount = section.getInt("minAmount");
        this.maxAmount = section.getInt("maxAmount");
    }
    public boolean shouldFill(Random random){
        return random.nextDouble() < chance;
    }

    public ItemStack make(ThreadLocalRandom random){
        int amount = random.nextInt(minAmount, maxAmount + 1);
        itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (customName != null){
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', customName));
        }
        if (!enchantmentToLevelMap.isEmpty()){
            for (Map.Entry<Enchantment, Integer> enchantEntry : enchantmentToLevelMap.entrySet()){
                itemMeta.addEnchant(enchantEntry.getKey(), enchantEntry.getValue(), true);
                itemMeta.setUnbreakable(true);
            }
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    public ItemStack getItem(){
        return itemStack;
    }

    @Override
    public boolean equals(Object obj) { // This is to make sure checking if an item has already been put in the chest
        if (this == obj) return true;   // works correctly
        if (obj == null || getClass() != obj.getClass()) return false;
        LootItem lootItem = (LootItem) obj;
        return material == lootItem.material &&
                enchantmentToLevelMap.equals(lootItem.enchantmentToLevelMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, enchantmentToLevelMap);
    }


}