package me.elaineqheart.auctionHouse.world.npc;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.HashSet;
import java.util.Set;

public class NPCListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAuctionMasterClick(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "auction_master"))) {
            event.setCancelled(true);
            Player p = event.getPlayer();
            Sounds.npcClick(p);
            AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(p), p);
        }
    }

    //connect the stand with the villager, so if one dies, both die
    @EventHandler
    public void onVillagerDeath(EntityDeathEvent event) {
        if(event.getEntity().getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "auction_master"))) {
            CreateNPC.removeAuctionMaster((Villager) event.getEntity());
        }
        if(event.getEntity().getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "auction_stand"))) {
            CreateNPC.removeAuctionMaster((ArmorStand) event.getEntity());
        }
    }

}
