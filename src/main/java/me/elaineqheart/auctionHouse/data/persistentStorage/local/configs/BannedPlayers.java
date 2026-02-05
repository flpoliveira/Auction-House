package me.elaineqheart.auctionHouse.data.persistentStorage.local.configs;

import me.elaineqheart.auctionHouse.data.StringUtils;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.Config;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Date;

public class BannedPlayers extends Config {

    public void saveBannedPlayer(Player p, int durationInDays, String reason){
        int timeInMillis = durationInDays * 24 * 60 * 60 * 1000;
        long banEndDate = new Date().getTime() + timeInMillis;
        Date date = new Date(banEndDate);

        String path = "BannedPlayers." + p.getUniqueId();
        String playerName = p.getName();
        FileConfiguration customFile = getCustomFile();
        customFile.set(path + ".Date", date);
        customFile.set(path + ".PlayerName", playerName);
        customFile.set(path + ".Reason", reason);
        save();
    }

    //if the player is banned, send them a message
    public boolean checkIsBannedSendMessage(Player p){
        FileConfiguration customFile = getCustomFile();
        String path = "BannedPlayers." + p.getUniqueId();
        if (customFile.get(path) == null) return false;
        Date banEndDate = (Date) customFile.get(path + ".Date");
        if (banEndDate == null) return false;
        long currentTime = new Date().getTime();
        if (currentTime > banEndDate.getTime()){
            customFile.set(path, null);
            save();
            return false;
        }
        long banDuration = banEndDate.getTime() - currentTime;
        p.sendMessage(ChatColor.WHITE + "You are temporarily banned for " + ChatColor.YELLOW + StringUtils.getTime(banDuration/1000, true)
                + ChatColor.WHITE + " from the auction house.");
        p.sendMessage(ChatColor.GRAY + "Reason: " + customFile.getString(path + ".Reason"));
        return true;
    }


}
