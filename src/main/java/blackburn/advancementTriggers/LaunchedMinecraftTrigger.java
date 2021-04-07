package blackburn.advancementTriggers;

import com.google.gson.JsonObject;

import blackburn.BlackburnConst;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.EntityPredicate.AndPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftGame;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class LaunchedMinecraftTrigger extends AbstractCriterionTrigger<LaunchedMinecraftTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation("launched");
    private Boolean status;

    public ResourceLocation getId()
    {
        return ID;
    }

    @Override
    protected Instance deserializeTrigger(JsonObject json, AndPredicate entityPredicate,
            ConditionArrayParser conditionsParser) {
        // TODO Auto-generated method stub
        return new LaunchedMinecraftTrigger.Instance(entityPredicate);
    
    }
   
    public void trigger(ServerPlayerEntity player)
    {
        this.triggerListeners(player, (instance) ->
        {
            if(BlackburnConst.mc.getMainWindow().isFullscreen()){
                this.status = true;
            }
            return this.status;
        });
    }

    public static class Instance extends CriterionInstance
    {
       
        public Instance(EntityPredicate.AndPredicate player)
        {
            super(LaunchedMinecraftTrigger.ID, player);
            
        }

    }


    
}
