package net.minecraft.data;

import java.nio.file.Path;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class FluidTagsProvider extends TagsProvider<Fluid> {
   public FluidTagsProvider(DataGenerator generatorIn) {
      super(generatorIn, Registry.FLUID);
   }

   protected void registerTags() {
      this.getOrCreateBuilder(FluidTags.WATER).add(Fluids.WATER, Fluids.FLOWING_WATER);
      this.getOrCreateBuilder(FluidTags.LAVA).add(Fluids.LAVA, Fluids.FLOWING_LAVA);
   }

   protected Path makePath(ResourceLocation id) {
      return this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/tags/fluids/" + id.getPath() + ".json");
   }

   public String getName() {
      return "Fluid Tags";
   }
}
