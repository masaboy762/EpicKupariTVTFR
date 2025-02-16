package happylaama.EpicKupariTVT;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.configuration.file.FileConfiguration;
public class Main extends JavaPlugin implements Listener{
	
	private boolean GameRunning = false;
	private boolean GameStarting = false;
	private List<Player> TeamKupari = new ArrayList<>();
	private List<Player> TeamKulta = new ArrayList<>();
	private List<Player> PVPON = new ArrayList<>();
	private FileConfiguration config = getConfig();
	@Override
	public void onEnable() {
		saveDefaultConfig();

        // Get config
        
	}
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Valitse tiimi.")) {
            event.setCancelled(true); // Prevent moving items

            Player player = (Player) event.getWhoClicked();
            Material clickedItem = event.getCurrentItem().getType();
            String selectedTeam = "None";
            if (event.getCurrentItem() == null) return;

            if (clickedItem == Material.COPPER_BLOCK) {
                selectedTeam = "Kupari";
                TeamKupari.add(player);
            } else if (clickedItem == Material.GOLD_BLOCK) {
                selectedTeam = "Kulta";
                TeamKulta.add(player);
            }

            player.closeInventory(); // Close the inventory

            // Now, return the selected team
            player.sendMessage("Sinä Valitsit: " + selectedTeam);
        }
    }
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		
		if (event.getPlayer().hasPermission("EpicKupariTVT.OP")) { return;}
		
		if(GameStarting) {
			if(event.getPlayer().hasPermission("EpicKupariTVT.KupariTiimi")|| event.getPlayer().hasPermission("EpicKupariTVT.KultaTiimi")) {return;}
			openTeamSelectionMenu(event.getPlayer());
			return;
			
		}
		
		if(GameRunning) {
			if(event.getPlayer().hasPermission("EpicKupariTVT.KupariTiimi")|| event.getPlayer().hasPermission("EpicKupariTVT.KultaTiimi")) {event.getPlayer().setGameMode(GameMode.SPECTATOR);}
		} 
	}
	@EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals("Select Your Team")) {
            Player player = (Player) event.getPlayer();

            // If the player closes the inventory without selecting, reopen the menu
            // Example condition, can be more complex
                openTeamSelectionMenu(player);
            
        }
    }

	
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "This is a message to the console!");

		}
		
	}
	public void UpdateBossbar() {
		
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	    if (!(sender instanceof Player)) {
	        sender.sendMessage("Only players can use this command!");
	        return true;
	    }

	    

	    if(GameRunning) {
	    	GameStarting = true;
	    	for (Player player : Bukkit.getOnlinePlayers()) {
	            // Send the title to each player
	            player.sendTitle("PELI ALKAMASSA!", "Odotellaan pelaajia!", 10, 70, 20);  // 10 = fade in, 70 = stay, 20 = fade out
	        }
	    } else if(GameStarting) {
	    GameRunning = true;
	    GAME();
	    }
	    
	    return true;
	}
	public void openTeamSelectionMenu(Player player) {
	    // Create the inventory (chest GUI)
	    Inventory gui = Bukkit.createInventory(null, 9, "Valitse tiimi");

	    // Team Red Item
	    ItemStack redTeam = new ItemStack(Material.COPPER_BLOCK);
	    ItemMeta redMeta = redTeam.getItemMeta();
	    redMeta.setDisplayName("§cLiity Kupari tiimiin");
	    redTeam.setItemMeta(redMeta);

	    // Team Blue Item
	    ItemStack blueTeam = new ItemStack(Material.GOLD_BLOCK);
	    ItemMeta blueMeta = blueTeam.getItemMeta();
	    blueMeta.setDisplayName("§9Liity Kulta tiimiin");
	    blueTeam.setItemMeta(blueMeta);

	    // Place items in the GUI
	    gui.setItem(3, redTeam);
	    gui.setItem(5, blueTeam);

	    // Open GUI for the player
	    player.openInventory(gui);

	    // Return a default value while waiting for the player's selection
	    
	}
	private void GAME() {
		
		for (World world : Bukkit.getWorlds()) {
            world.setPVP(true);
        }
		
		new BukkitRunnable() {
            @Override
            public void run() {
                // Check if player is still online before continuing
                
            }
        }.runTaskTimerAsynchronously(this, 0L, 20L);  // Runs every 20 ticks (1 second)
    }
	}

