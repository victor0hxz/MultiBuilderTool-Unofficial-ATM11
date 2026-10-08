package igentuman.mbtool.test;
import igentuman.mbtool.Mbtool;
import igentuman.mbtool.item.MultibuilderItem;
import igentuman.mbtool.config.MbtoolConfig;
import igentuman.mbtool.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.capabilities.Capabilities;
@EventBusSubscriber(modid="mbtool")
public class PortFunctionalSmoke {
 static void require(boolean test,String message){if(!test)throw new AssertionError(message);Mbtool.logger.info("PORT PASS: {}",message);}
 @SubscribeEvent public static void run(ServerStartedEvent event){
  if(!Boolean.getBoolean("port.smoke"))return;
  var level=event.getServer().overworld();
  try{
   ItemStack stack=new ItemStack(Mbtool.MBTOOL.get());var item=(MultibuilderItem)stack.getItem();var access=ItemAccess.forStack(stack);var battery=access.getCapability(Capabilities.Energy.ITEM);var inventory=access.getCapability(Capabilities.Item.ITEM);
   require(battery!=null && inventory!=null,"energy and inventory capabilities available");
   try(var tx=Transaction.openRoot()){battery.insert(1000,tx);}require(battery.getAmountAsLong()==0,"energy charging rollback");
   try(var tx=Transaction.openRoot()){require(inventory.insert(0,ItemResource.of(Items.DIAMOND),512,tx)==512,"512 items accepted per inventory slot");tx.commit();}
   require(item.getInventory(stack,level.registryAccess()).getStackInSlot(0).getCount()==512,"oversized inventory survives serialization");
   try(var tx=Transaction.openRoot()){inventory.extract(0,ItemResource.of(Items.DIAMOND),512,tx);}require(inventory.getAmountAsLong(0)==512,"inventory extraction rollback");
   try(var tx=Transaction.openRoot()){inventory.extract(0,ItemResource.of(Items.DIAMOND),512,tx);tx.commit();}
   item.getEnergy(stack).setEnergy(MbtoolConfig.getMaxEnergy());
   CompoundTag blueprint=new CompoundTag();ListTag palette=new ListTag();String[] ids={"induction_casing","induction_port","basic_induction_cell","basic_induction_provider"};
   for(String id:ids){var state=new CompoundTag();state.putString("Name","mekanism:"+id);palette.add(state);}blueprint.put("palette",palette);
   ListTag entries=new ListTag();
   for(int x=0;x<4;x++)for(int y=0;y<3;y++)for(int z=0;z<3;z++){var entry=new CompoundTag();ListTag pos=new ListTag();pos.add(IntTag.valueOf(x));pos.add(IntTag.valueOf(y));pos.add(IntTag.valueOf(z));entry.put("pos",pos);int index=x==0 && y==1 && z==1?1:x==1 && y==1 && z==1?2:x==2 && y==1 && z==1?3:0;entry.putInt("state",index);entries.add(entry);}blueprint.put("blocks",entries);
   var structure=new MultiblockStructure(Identifier.fromNamespaceAndPath("mbtool","port_matrix"),blueprint,"port_matrix");MultiblocksProvider.structures.add(structure);
   var required=new java.util.HashMap<net.minecraft.world.level.block.Block,Integer>();for(var state:structure.getBlocks().values())required.merge(state.getBlock(),1,Integer::sum);var legacy=item.getInventory(stack,level.registryAccess());int slot=0;for(var entry:required.entrySet()){var supplied=new ItemStack(entry.getKey(),entry.getValue());var leftover=legacy.insertItem(slot++,supplied,false);Mbtool.logger.info("PORT supplied {} remainder {} inventory {}",supplied,leftover,stack.get(igentuman.mbtool.registration.MbtoolDataComponents.INVENTORY.get()));}
   var player=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);player.gameMode.changeGameModeForPlayer(GameType.SURVIVAL);player.getInventory().setItem(0,stack);
   var origin=new BlockPos(0,140,0);for(int x=0;x<4;x++)for(int y=0;y<3;y++)for(int z=0;z<3;z++)level.setBlockAndUpdate(origin.offset(x,y,z),Blocks.AIR.defaultBlockState());
   int initialEnergy=item.getEnergy(stack).getEnergyStored();var result=MultiblockBuilder.buildMultiblock(level,player,stack,structure,origin,0);require(result.isSuccess(),"matrix build succeeds: "+result.getMessage().getString());
   int placed=0;for(var pos:structure.getBlocks().keySet())if(!level.getBlockState(origin.offset(pos)).isAir())placed++;require(placed==36,"all 36 matrix blocks placed");require(item.getEnergy(stack).getEnergyStored()==initialEnergy-36*MbtoolConfig.getEnergyPerBlock(),"build consumes exact energy");
   long remaining=0;for(int index=0;index<inventory.size();index++)remaining+=inventory.getAmountAsLong(index);require(remaining==0,"build consumes exact materials");
   var placedStructure=PlacedStructuresManager.getPlacedStructures(stack).getLast();var dismantled=StructureDismantler.dismantleStructure(level,player,stack,placedStructure);require(dismantled.isSuccess(),"matrix dismantling succeeds");
   Mbtool.logger.info("PORT FUNCTIONAL SMOKE COMPLETE; templates={}",MultiblocksProvider.structures.size());
  }catch(Throwable error){Mbtool.logger.error("PORT FUNCTIONAL SMOKE FAILED",error);}
 }
}


