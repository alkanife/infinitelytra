package fr.alkanife.infinitelytra;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Infinitelytra extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        giveElytras(player);
        giveFireworks(player);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        ItemStack itemStack = event.getItemDrop().getItemStack();
        if (isElytras(itemStack) || isFireworks(itemStack))
            event.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (event.getKeepInventory())
            return;

        event.getDrops().removeIf(this::isElytras);
        event.getDrops().removeIf(this::isFireworks);
    }

    @EventHandler
    public void onRespawn(PlayerPostRespawnEvent event) {
        Player player = event.getPlayer();
        giveElytras(player);
        giveFireworks(player);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();

        if (itemStack == null)
            return;

        if (itemStack.getType().equals(Material.FIREWORK_ROCKET))
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR))
                if (itemStack.getAmount() < 3)
                    event.getPlayer().getInventory().addItem(new ItemStack(Material.FIREWORK_ROCKET));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getWhoClicked().getOpenInventory().getType().equals(InventoryType.CRAFTING)) {
            if (isElytras(event.getCurrentItem()) || isFireworks(event.getCurrentItem()))
                event.setCancelled(true);
        }
    }

    private boolean haveElytras(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents())
            if (isElytras(itemStack))
                return true;

        return isElytras(player.getInventory().getChestplate());
    }

    private void giveElytras(Player player) {
        if (!haveElytras(player)) {
            ItemStack elytras = new ItemStack(Material.ELYTRA);
            ItemMeta itemMeta = elytras.getItemMeta();
            itemMeta.setUnbreakable(true);
            itemMeta.displayName(Component.text(player.getName() + "'s elytras").color(TextColor.color(195, 143, 247)));
            elytras.setItemMeta(itemMeta);
            player.getInventory().addItem(elytras);
        }
    }

    private boolean isElytras(ItemStack itemStack) {
        if (itemStack == null)
            return false;

        return itemStack.getType().equals(Material.ELYTRA) && itemStack.getItemMeta().isUnbreakable();
    }

    private boolean haveFireworks(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents())
            if (isFireworks(itemStack))
                return true;
        return false;
    }

    private void giveFireworks(Player player) {
        if (!haveFireworks(player))
            player.getInventory().addItem(new ItemStack(Material.FIREWORK_ROCKET, 3));
    }

    private boolean isFireworks(ItemStack itemStack) {
        if (itemStack == null)
            return false;

        return itemStack.getType().equals(Material.FIREWORK_ROCKET);
    }
}
