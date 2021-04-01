package net.minecraft.item.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShapelessRecipe implements ICraftingRecipe {
   private final ResourceLocation id;
   private final String group;
   private final ItemStack recipeOutput;
   private final NonNullList<Ingredient> recipeItems;

   public ShapelessRecipe(ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn, NonNullList<Ingredient> recipeItemsIn) {
      this.id = idIn;
      this.group = groupIn;
      this.recipeOutput = recipeOutputIn;
      this.recipeItems = recipeItemsIn;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.CRAFTING_SHAPELESS;
   }

   @OnlyIn(Dist.CLIENT)
   public String getGroup() {
      return this.group;
   }

   public ItemStack getRecipeOutput() {
      return this.recipeOutput;
   }

   public NonNullList<Ingredient> getIngredients() {
      return this.recipeItems;
   }

   public boolean matches(CraftingInventory inv, World worldIn) {
      RecipeItemHelper recipeitemhelper = new RecipeItemHelper();
      int i = 0;

      for(int j = 0; j < inv.getSizeInventory(); ++j) {
         ItemStack itemstack = inv.getStackInSlot(j);
         if (!itemstack.isEmpty()) {
            ++i;
            recipeitemhelper.func_221264_a(itemstack, 1);
         }
      }

      return i == this.recipeItems.size() && recipeitemhelper.canCraft(this, (IntList)null);
   }

   public ItemStack getCraftingResult(CraftingInventory inv) {
      return this.recipeOutput.copy();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canFit(int width, int height) {
      return width * height >= this.recipeItems.size();
   }

   public static class Serializer implements IRecipeSerializer<ShapelessRecipe> {
      public ShapelessRecipe read(ResourceLocation recipeId, JsonObject json) {
         String s = JSONUtils.getString(json, "group", "");
         NonNullList<Ingredient> nonnulllist = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));
         if (nonnulllist.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
         } else if (nonnulllist.size() > 9) {
            throw new JsonParseException("Too many ingredients for shapeless recipe");
         } else {
            ItemStack itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
            return new ShapelessRecipe(recipeId, s, itemstack, nonnulllist);
         }
      }

      private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray) {
         NonNullList<Ingredient> nonnulllist = NonNullList.create();

         for(int i = 0; i < ingredientArray.size(); ++i) {
            Ingredient ingredient = Ingredient.deserialize(ingredientArray.get(i));
            if (!ingredient.hasNoMatchingItems()) {
               nonnulllist.add(ingredient);
            }
         }

         return nonnulllist;
      }

      public ShapelessRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
         String s = buffer.readString(32767);
         int i = buffer.readVarInt();
         NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

         for(int j = 0; j < nonnulllist.size(); ++j) {
            nonnulllist.set(j, Ingredient.read(buffer));
         }

         ItemStack itemstack = buffer.readItemStack();
         return new ShapelessRecipe(recipeId, s, itemstack, nonnulllist);
      }

      public void write(PacketBuffer buffer, ShapelessRecipe recipe) {
         buffer.writeString(recipe.group);
         buffer.writeVarInt(recipe.recipeItems.size());

         for(Ingredient ingredient : recipe.recipeItems) {
            ingredient.write(buffer);
         }

         buffer.writeItemStack(recipe.recipeOutput);
      }
   }
}
