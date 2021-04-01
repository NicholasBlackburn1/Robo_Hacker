package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class FireAspectEnchantment extends Enchantment {
   protected FireAspectEnchantment(Enchantment.Rarity rarityIn, EquipmentSlotType... slots) {
      super(rarityIn, EnchantmentType.WEAPON, slots);
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 10 + 20 * (enchantmentLevel - 1);
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   public int getMaxLevel() {
      return 2;
   }
}
