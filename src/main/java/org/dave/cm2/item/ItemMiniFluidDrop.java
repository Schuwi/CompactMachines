package org.dave.cm2.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.dave.cm2.CompactMachines2;
import org.dave.cm2.init.Fluidss;
import org.dave.cm2.init.Potionss;
import org.dave.cm2.misc.ConfigurationHandler;
import org.dave.cm2.misc.CreativeTabCM2;

public class ItemMiniFluidDrop extends ItemFood {
    public ItemMiniFluidDrop() {
        super(1, 0.1F, false);

        this.setCreativeTab(CreativeTabCM2.CM2_TAB);
    }

    @Override
    public Item setUnlocalizedName(String name) {
        if(!name.startsWith(CompactMachines2.MODID + ".")) {
            name = CompactMachines2.MODID + "." + name;
        }
        return super.setUnlocalizedName(name);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
        if(world.isRemote || !(player instanceof EntityPlayerMP)) {
            return super.onItemRightClick(itemStack, world, player, hand);
        }

        RayTraceResult trace = player.rayTrace(4.5d, 0.0f);
        if(trace == null || trace.getBlockPos() == null) {
            return super.onItemRightClick(itemStack, world, player, hand);
        }

        TileEntity te = world.getTileEntity(trace.getBlockPos());
        if(te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, trace.sideHit)) {
            IFluidHandler fluidHandler = world.getTileEntity(trace.getBlockPos()).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, trace.sideHit);
            if(fluidHandler.fill(new FluidStack(Fluidss.miniaturizationFluid, 125), false) == 125) {
                fluidHandler.fill(new FluidStack(Fluidss.miniaturizationFluid, 125), true);
                itemStack.stackSize--;
                return new ActionResult(EnumActionResult.SUCCESS, itemStack);
            }
        }

        return super.onItemRightClick(itemStack, world, player, hand);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        super.onFoodEaten(stack, worldIn, player);

        int duration = ConfigurationHandler.PotionSettings.onEatDuration;
        int amplifier = ConfigurationHandler.PotionSettings.onEatAmplifier;

        if(duration > 0) {
            PotionEffect active = player.getActivePotionEffect(Potionss.miniaturizationPotion);

            PotionEffect effect = new PotionEffect(Potionss.miniaturizationPotion, duration, amplifier, false, false);
            if (active != null) {
                active.combine(effect);
            } else {
                player.addPotionEffect(effect);
            }
        }

    }
}
