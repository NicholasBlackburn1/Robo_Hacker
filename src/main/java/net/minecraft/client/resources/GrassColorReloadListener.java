package net.minecraft.client.resources;

import java.io.IOException;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GrassColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GrassColorReloadListener extends ReloadListener<int[]> {
   private static final ResourceLocation GRASS_LOCATION = new ResourceLocation("textures/colormap/grass.png");

   protected int[] prepare(IResourceManager resourceManagerIn, IProfiler profilerIn) {
      try {
         return ColorMapLoader.loadColors(resourceManagerIn, GRASS_LOCATION);
      } catch (IOException ioexception) {
         throw new IllegalStateException("Failed to load grass color texture", ioexception);
      }
   }

   protected void apply(int[] objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
      GrassColors.setGrassBiomeColorizer(objectIn);
   }
}
