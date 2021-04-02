package net.minecraft.client.renderer;

import java.util.Collection;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class StitcherException extends RuntimeException
{
    private final Collection<TextureAtlasSprite.Info> spriteInfos;

    public StitcherException(TextureAtlasSprite.Info spriteInfoIn, Collection<TextureAtlasSprite.Info> spriteInfosIn)
    {
        super(String.format("Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", spriteInfoIn.getSpriteLocation(), spriteInfoIn.getSpriteWidth(), spriteInfoIn.getSpriteHeight()));
        this.spriteInfos = spriteInfosIn;
    }

    public Collection<TextureAtlasSprite.Info> getSpriteInfos()
    {
        return this.spriteInfos;
    }

    public StitcherException(TextureAtlasSprite.Info p_i242106_1_, Collection<TextureAtlasSprite.Info> p_i242106_2_, int p_i242106_3_, int p_i242106_4_, int p_i242106_5_, int p_i242106_6_)
    {
        super(String.format("Unable to fit: %s, size: %dx%d, atlas: %dx%d, atlasMax: %dx%d - Maybe try a lower resolution resourcepack?", "" + p_i242106_1_.getSpriteLocation(), p_i242106_1_.getSpriteWidth(), p_i242106_1_.getSpriteHeight(), p_i242106_3_, p_i242106_4_, p_i242106_5_, p_i242106_6_));
        this.spriteInfos = p_i242106_2_;
    }
}
