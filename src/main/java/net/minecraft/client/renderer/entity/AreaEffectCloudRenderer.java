package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.util.ResourceLocation;

public class AreaEffectCloudRenderer extends EntityRenderer<AreaEffectCloudEntity>
{
    public AreaEffectCloudRenderer(EntityRendererManager manager)
    {
        super(manager);
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getEntityTexture(AreaEffectCloudEntity entity)
    {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }
}
