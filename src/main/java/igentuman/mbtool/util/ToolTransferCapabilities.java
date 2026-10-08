package igentuman.mbtool.util;
import igentuman.mbtool.Mbtool;
import igentuman.mbtool.config.MbtoolConfig;
import igentuman.mbtool.item.MultibuilderItem;
import igentuman.mbtool.registration.MbtoolDataComponents;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
/** Changes the item through ItemAccess, allowing charging/insertion to roll back atomically. */
public final class ToolTransferCapabilities {
 public static EnergyHandler energy(ItemAccess access){return new EnergyHandler(){
  public long getAmountAsLong(){return access.getResource().getOrDefault(MbtoolDataComponents.ENERGY.get(),0);}
  public long getCapacityAsLong(){return MbtoolConfig.getMaxEnergy();}
  public int insert(int amount,TransactionContext tx){if(amount<=0)return 0;int stored=(int)getAmountAsLong();int moved=Math.min(amount,Math.min(MbtoolConfig.getEnergyTransferRate(),MbtoolConfig.getMaxEnergy()-stored));return update(stored+moved,moved,tx);}
  public int extract(int amount,TransactionContext tx){if(amount<=0)return 0;int stored=(int)getAmountAsLong();int moved=Math.min(amount,Math.min(MbtoolConfig.getEnergyTransferRate(),stored));return update(stored-moved,moved,tx);}
  private int update(int stored,int moved,TransactionContext tx){if(moved<=0)return 0;var changed=access.getResource().with(MbtoolDataComponents.ENERGY.get(),stored);return access.exchange(changed,1,tx)==1?moved:0;}
 };}
 public static ResourceHandler<ItemResource> inventory(ItemAccess access){return new ResourceHandler<>(){
  private HolderLookup.Provider registries(){var server=net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();return server!=null?server.registryAccess():RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);}
  private ItemInventoryHandler current(){var stack=access.getResource().toStack(1);return ((MultibuilderItem)stack.getItem()).getInventoryHandler(stack,registries());}
  public int size(){return MultibuilderItem.getInventorySize();}
  public ItemResource getResource(int slot){return ItemResource.of(current().getStackInSlot(slot));}
  public long getAmountAsLong(int slot){return current().getStackInSlot(slot).getCount();}
  public long getCapacityAsLong(int slot,ItemResource resource){return 512;}
  public boolean isValid(int slot,ItemResource resource){return !resource.isEmpty() && !resource.is(Mbtool.MBTOOL.get());}
  public int insert(int slot,ItemResource resource,int amount,TransactionContext tx){if(amount<=0 || !isValid(slot,resource))return 0;var inv=current();int moved=amount-inv.insertItem(slot,resource.toStack(amount),false).getCount();return commit(inv,moved,tx);}
  public int extract(int slot,ItemResource resource,int amount,TransactionContext tx){if(amount<=0 || resource.isEmpty())return 0;var inv=current();if(!resource.matches(inv.getStackInSlot(slot)))return 0;int moved=inv.extractItem(slot,amount,false).getCount();return commit(inv,moved,tx);}
  private int commit(ItemInventoryHandler inv,int moved,TransactionContext tx){if(moved<=0)return 0;var changed=access.getResource().with(MbtoolDataComponents.INVENTORY.get(),inv.serializeNBT(registries()));return access.exchange(changed,1,tx)==1?moved:0;}
 };}
}
