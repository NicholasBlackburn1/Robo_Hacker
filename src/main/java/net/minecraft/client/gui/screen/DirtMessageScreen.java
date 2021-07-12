package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.ITextComponent;

public class DirtMessageScreen extends Screen
{
    public DirtMessageScreen(ITextComponent p_i51114_1_)
    {
        super(p_i51114_1_);
    }

    public boolean shouldCloseOnEsc()
    {
        return false;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderDirtBackground(0);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 70, 16777215);
        drawCenteredString(matrixStack, this.font, "cU§6w§eU §9u §5t§ch§6o§eu§ag§9h§bt §ct§6h§ei§as §bw§5a§cs §eu§as§9e§bf§5u§cl§6l§e!", this.width / 2, 85, 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
