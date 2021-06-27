package raff.treefeller;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import raff.treefeller.Utility.TogglePlugin;

import java.util.*;

public final class TreeFeller extends JavaPlugin implements Listener {


    HashSet<Material> validBlocks;
    public static ArrayList<UUID> enabledPlayers = new ArrayList<>();


    HashSet<Material> normalBlocks = new HashSet<>(Arrays.asList(
            Material.OAK_LOG, Material.SPRUCE_LOG,
            Material.BIRCH_LOG, Material.JUNGLE_LOG,
            Material.DARK_OAK_LOG, Material.ACACIA_LOG,
            Material.WARPED_STEM, Material.CRIMSON_STEM
    ));


    HashSet<Material> strippedBlocks = new HashSet<>(Arrays.asList(
            Material.STRIPPED_OAK_LOG, Material.STRIPPED_SPRUCE_LOG,
            Material.STRIPPED_BIRCH_LOG, Material.STRIPPED_JUNGLE_LOG,
            Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_ACACIA_LOG,
            Material.STRIPPED_WARPED_STEM, Material.STRIPPED_CRIMSON_STEM
    ));


    public HashSet<Material> setupBlocks(Boolean strippedLogs) {
        if (strippedLogs) {
            return new HashSet<Material>() {
                {
                    addAll(normalBlocks);
                    addAll(strippedBlocks);
                }
            };
        }
        return normalBlocks;
    }


    HashSet<Material> validTools = new HashSet<>(Arrays.asList(
            Material.DIAMOND_AXE, Material.WOODEN_AXE,
            Material.GOLDEN_AXE, Material.NETHERITE_AXE,
            Material.STONE_AXE, Material.IRON_AXE
    ));


    @Override
    public void onEnable() {
        saveDefaultConfig();
        Boolean strippedLogsSupport = getConfig().getBoolean("strippedLogsSupport");
        validBlocks = setupBlocks(strippedLogsSupport);
        getServer().getConsoleSender().sendMessage("> Loaded " + ChatColor.GOLD + "TreeFeller");
        getServer().getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(getCommand("tf")).setExecutor(new TogglePlugin());

    }


    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Block brokenBlock = event.getBlock();
        Player _player = event.getPlayer();
        if (enabledPlayers.contains(_player.getUniqueId())) {
            if (validBlocks.contains(brokenBlock.getType())) {
                ItemStack itemInHand = _player.getInventory().getItemInMainHand();
                if (validTools.contains(itemInHand.getType())) {
                    fellTree(_player, brokenBlock, itemInHand);
                }
            }
        }
    }


    public void fellTree(Player p, Block b, ItemStack toolUsed) {
        int brokenBlocks = 0;
        Location initialBlockLocation = b.getLocation();
        for (int i = initialBlockLocation.getBlockY(); i < b.getWorld().getMaxHeight(); ++i) {
            Location nextLocation = new Location(initialBlockLocation.getWorld(), initialBlockLocation.getX(), i+1, initialBlockLocation.getZ());
            if (!validBlocks.contains(nextLocation.getBlock().getType())) { break; }
                ItemMeta itemMeta = toolUsed.getItemMeta();
                Damageable toolDamage = (Damageable) itemMeta;
                if (toolDamage != null) {
                    toolDamage.setDamage(toolDamage.getDamage()+1);
                    toolUsed.setItemMeta(itemMeta);
                    if (toolDamage.getDamage() >= toolUsed.getType().getMaxDurability()) {
                        toolUsed.setType(Material.AIR);
                        break;
                    }
                    nextLocation.getBlock().breakNaturally(toolUsed);
                }
            ++brokenBlocks;
        }
        if (brokenBlocks > 0) {
            getServer().getConsoleSender().sendMessage(
                    ChatColor.GOLD + "TreeFeller" +
                    ChatColor.WHITE + " > " +
                    ChatColor.GREEN + p.getName() +
                    ChatColor.WHITE + " broke " +
                    ChatColor.RED + brokenBlocks +
                    ChatColor.WHITE + " extra blocks"
            );
        }
    }

}
