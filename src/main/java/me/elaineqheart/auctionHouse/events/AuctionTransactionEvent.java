package me.elaineqheart.auctionHouse.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Bukkit event fired after a successful Auction House purchase.
 * Consumed by GalaxyTransactionLogger (or any other listener).
 */
public class AuctionTransactionEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player buyer;
    private final String sellerName;
    private final String itemName;
    private final String itemType;
    private final ItemStack item;
    private final int quantity;
    private final double price;
    private final boolean isBidAuction;

    public AuctionTransactionEvent(Player buyer, String sellerName, String itemName,
                                   String itemType, ItemStack item, int quantity, double price, boolean isBidAuction) {
        this.buyer = buyer;
        this.sellerName = sellerName;
        this.itemName = itemName;
        this.itemType = itemType;
        this.item = item.clone(); // clone to avoid mutation after event fires
        this.quantity = quantity;
        this.price = price;
        this.isBidAuction = isBidAuction;
    }

    public Player getBuyer() { return buyer; }
    public String getSellerName() { return sellerName; }
    public String getItemName() { return itemName; }
    public String getItemType() { return itemType; }
    public ItemStack getItem() { return item; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public boolean isBidAuction() { return isBidAuction; }

    @Override
    public HandlerList getHandlers() { return HANDLER_LIST; }
    public static HandlerList getHandlerList() { return HANDLER_LIST; }
}
