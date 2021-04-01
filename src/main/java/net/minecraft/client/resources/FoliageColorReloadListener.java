package net.minecraft.client.resources;

import java.io.IOException;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.FoliageColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoliageColorReloadListener extends ReloadListener<int[]> {
   private static final ResourceLocation FOLIAGE_LOCATION = new ResourceLocation("textures/colormap/foliage.png");

   protected int[] prepare(IResourceManager resourceManagerIn, IProfiler profilerIn) {
      try {
         return ColorMapLoader.loadColors(resourceManagerIn, FOLIAGE_LOCATION);
      } catch (IOException ioexception) {
         throw new IllegalStateException("Failed to load foliage color texture", ioexception);
      }
   }

   protected void apply(int[] objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
      FoliageColors.setFoliageBiomeColorizer(objectIn);
   }
}
