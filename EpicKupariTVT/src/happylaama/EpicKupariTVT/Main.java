package happylaama.EpicKupariTVT;




import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;



import org.bukkit.configuration.file.FileConfiguration;
public class Main extends JavaPlugin implements Listener{
	
	private boolean GameRunning = false;
	private boolean GameStarting = false;
	private List<Player> TeamKupari = new ArrayList<>();
	private List<Player> TeamKulta = new ArrayList<>();
	private List<Player> TeamKuparik = new ArrayList<>();
	private List<Player> TeamKultak = new ArrayList<>();
	private List<Player> LATEPLAYERS = new ArrayList<>();
	private FileConfiguration config = getConfig();
	private static final Random RANDOM = new Random();
	private boolean Frozen = false;
	@Override
	public void onEnable() {
		saveDefaultConfig();
		
        Bukkit.getPluginManager().registerEvents(this, this);
        
        getCommand("STARTGAME").setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cTämän komennon voi suorittaa vain pelaaja!");
                return true;
            }
            if(GameRunning) {
		    	GameRunning = true;
		    	sender.sendMessage("Peli aloitettu!");
		    	
		    	for (Player player : Bukkit.getOnlinePlayers()) {
		            player.sendTitle("PELI ALKAMASSA!", "Odotellaan pelaajia!", 10, 70, 20); 
		            
		        }
		    } else if(GameStarting) {
		    	sender.sendMessage("odotellaan pelaajia! Lähetä komento uudestaan jotta peli alkaa");
		    
		    GameStarting = true;
		    GAME();
		    }
            
            return true;
        

        });
        getCommand("TASOITA").setExecutor((sender, command, label, args) -> {
            
            
            if(TeamKupari.size() < TeamKulta.size()) {
            	int temp = TeamKulta.size() - TeamKupari.size();
            	for (int i = 0; i < temp; i++) {
            		Player player = getRandomPlayer(TeamKulta);
            		TeamKulta.remove(player);
            		TeamKupari.add(player);
            		player.sendTitle("Sinut siirrettiin Kupari tiimiin!", "Sinut siirrettiin kupari tiimiin koska pelaaja määrä tiimien välissä ei ollut tasainen");
            		
    				
    			}
            	sender.sendMessage("§"+temp + " Pelaajaa siirrettiin!");
            	
            }
            if(TeamKupari.size() > TeamKulta.size()) {
            	int temp = TeamKupari.size() - TeamKulta.size();
            	for (int i = 0; i < temp; i++) {
            		Player player = getRandomPlayer(TeamKupari);
            		TeamKupari.remove(player);
            		TeamKulta.add(player);
            		player.sendTitle("Sinut siirrettiin Kulta tiimiin!", "Sinut siirrettiin kulta tiimiin koska pelaaja määrä tiimien välissä ei ollut tarpeeksi tasainen");
            		
    				
    			}
            	sender.sendMessage("§"+temp + " Pelaajaa siirrettiin!");
            }
            return true;
        });
        getCommand("FREEZE").setExecutor((sender, command, label, args) -> {
        	Player target = Bukkit.getPlayer(args[0]);
        	if(Frozen) {
        	if (args.length < 1) {
        		for (Player player : TeamKupari) {
        			freezePlayer(player);
                }
        		for (Player player : TeamKulta) {
        			freezePlayer(player);
                }
        	} else {
        		freezePlayer(target);
        	}
                return true;
            } else {
            	if (args.length < 1) {
            		for (Player player : TeamKupari) {
            			unfreezePlayer(player);
                    }
            		for (Player player : TeamKulta) {
            			unfreezePlayer(player);
                    }
            	} else {
            		unfreezePlayer(target);
            	}
            }
            return true;
        });
        
	}
	public static void freezePlayer(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 250, false, false)); // Extreme Slowness
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, Integer.MAX_VALUE, 128, false, false)); // No Jumping
        player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, Integer.MAX_VALUE, 10, false, false)); // Slower Attacks
        player.sendTitle("Sinut on jäädytetty", null);
    }

    public static void unfreezePlayer(Player player) {
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        player.removePotionEffect(PotionEffectType.JUMP_BOOST);
        player.removePotionEffect(PotionEffectType.MINING_FATIGUE);
        player.sendTitle("Sinut on sulatettu", null);
    }
	public static <T> T getRandomPlayer(List<T> players) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("Player list cannot be null or empty");
        }
        return players.get(RANDOM.nextInt(players.size()));
    }
	@Override
    public void onDisable() {  
    }
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Valitse tiimi")) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            Material clickedItem = event.getCurrentItem().getType();
           
            if (event.getCurrentItem() == null) return;

            if (clickedItem == Material.COPPER_BLOCK) {
            	player.addAttachment(this).setPermission("EpicKupariTVT.KupariTiimi", true);
            	player.sendMessage("Sinä Valitsit Tiimin Kupari");
                TeamKupari.add(player);
                TeamKuparik.add(player);
                player.closeInventory();
            } else if (clickedItem == Material.GOLD_BLOCK) {
            	player.addAttachment(this).setPermission("EpicKupariTVT.KultaTiimi", true);
            	player.sendMessage("Sinä Valitsit Tiimin Kulta");
                TeamKulta.add(player);
                TeamKultak.add(player);
                player.closeInventory();
            }

            

            
        }
    }
	@EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        
        if (event.getEntity() instanceof Player) {
            Player damagedPlayer = (Player) event.getEntity();  

            
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;

                
                if (damageEvent.getDamager() instanceof Player) {
                    Player attackingPlayer = (Player) damageEvent.getDamager(); 
                    if ((TeamKupari.contains(damagedPlayer) && TeamKupari.contains(attackingPlayer)) || 
                        (TeamKulta.contains(damagedPlayer) && TeamKulta.contains(attackingPlayer))) {
                        event.setCancelled(true);  
                        
                    }
                }
            }
        }
    }
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(GameStarting) {
			event.getPlayer().sendTitle("PELI ALKAMASSA!", "Odotellaan pelaajia!", 10, 70, 20);
			if(event.getPlayer().hasPermission("EpicKupariTVT.KupariTiimi")|| event.getPlayer().hasPermission("EpicKupariTVT.KultaTiimi")) {return;}
			openTeamSelectionMenu(event.getPlayer());
			return;
			
		}
		
		if(GameRunning) {
			event.getPlayer().sendTitle("PELI ON JO ALKANUT", "", 10, 70, 20);
			if (!event.getPlayer().isOp()) {
				event.getPlayer().setGameMode(GameMode.SPECTATOR);
				LATEPLAYERS.add(event.getPlayer());
				return;
			}
		} 
	}
	@EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
		if (TeamKupari.contains(event.getPlayer()) || TeamKulta.contains(event.getPlayer())) {
			return;
		} else {
			if (event.getView().getTitle().equals("Valitse tiimi")) {
	            Player player = (Player) event.getPlayer();

	                openTeamSelectionMenu(player);
	            
	        }
		}

    }

	
	@EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
		if (event.getEntity() instanceof Player) {
        if(TeamKupari.contains(event.getEntity())) {
        	TeamKupari.remove(event.getEntity());
        	
        }
        if(TeamKulta.contains(event.getEntity())) {
        	TeamKulta.remove(event.getEntity());
        }
		}
		event.getEntity().setGameMode(GameMode.SPECTATOR);
		
    }
	
	
	public void openTeamSelectionMenu(Player player) {

	    Inventory gui = Bukkit.createInventory(null, 9, "Valitse tiimi");


	    ItemStack redTeam = new ItemStack(Material.COPPER_BLOCK);
	    ItemMeta redMeta = redTeam.getItemMeta();
	    redMeta.setDisplayName("§cLiity Kupari tiimiin jossa on " + TeamKupari.size() + " pelaajaa");
	    redTeam.setItemMeta(redMeta);


	    ItemStack blueTeam = new ItemStack(Material.GOLD_BLOCK);
	    ItemMeta blueMeta = blueTeam.getItemMeta();
	    blueMeta.setDisplayName("§9Liity Kulta tiimiin jossa on " + TeamKulta.size() + " pelaajaa");
	    blueTeam.setItemMeta(blueMeta);
	    
	    ItemStack Empty = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
	    ItemMeta EmptyMeta = Empty.getItemMeta();
	    EmptyMeta.setDisplayName("Valitse Tiimi");
	    Empty.setItemMeta(EmptyMeta);

	    gui.setItem(3, redTeam);
	    gui.setItem(5, blueTeam);
	    gui.setItem(1, Empty);
	    gui.setItem(2, Empty);
	    gui.setItem(4, Empty);
	    gui.setItem(6, Empty);
	    gui.setItem(7, Empty);
	    gui.setItem(8, Empty);
	    gui.setItem(9, Empty);

	    player.openInventory(gui);
	    
	}
	public int getRandomNumber(int min, int max) {
        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
	
	private void GAME() {
		 
		 BossBar TeamKuparibar = Bukkit.createBossBar("§6Pelaajia Kupari tiimissä Jäljellä!", BarColor.RED, BarStyle.SOLID);
		 BossBar TeamKultabar = Bukkit.createBossBar("§ePelaajia Kulta tiimissä jäljellä!", BarColor.YELLOW, BarStyle.SOLID);
		 int TotalKupari = TeamKupari.size();
		 int TotalKulta = TeamKulta.size();
		 
		for (Player player : TeamKupari) {
			int x1 = config.getInt("KupariTiimiSpawnArea.X1");
			int z1 = config.getInt("KupariTiimiSpawnArea.Z1");
			int x2 = config.getInt("KupariTiimiSpawnArea.X2");
			int z2 = config.getInt("KupariTiimiSpawnArea.Z2");
			
			
			player.teleport(new Location(player.getWorld(), getRandomNumber(x1, x2), config.getInt("KupariTiimiSpawnArea.Y"), getRandomNumber(z1, z2)));
			player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_2, 1.0f, 1.0f);
			player.sendTitle("PELI ON ALKANUT!", "", 10, 70, 20);
            
        }
		for (Player player : TeamKulta) {
			int x1 = config.getInt("KultaTiimiSpawnArea.X1");
			int z1 = config.getInt("KultaTiimiSpawnArea.Z1");
			int x2 = config.getInt("KultaTiimiSpawnArea.X2");
			int z2 = config.getInt("KultaTiimiSpawnArea.Z2");
			
			
			player.teleport(new Location(player.getWorld(), getRandomNumber(x1, x2), config.getInt("KultaTiimiSpawnArea.Y"), getRandomNumber(z1, z2)));
			player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_2, 1.0f, 1.0f);
			player.sendTitle("PELI ON ALKANUT!", "", 10, 70, 20);
           
		}
		
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            TeamKuparibar.addPlayer(player);
            TeamKultabar.addPlayer(player);
            TeamKuparibar.setVisible(false);
            
        }
		new BukkitRunnable() {
            @Override
            public void run() {
            	TeamKuparibar.setProgress(TeamKupari.size() / TotalKupari);
            	TeamKuparibar.setTitle("§6" + TeamKupari.size() + " pelaajaa jäljellä Kupari tiimissä!");
            	TeamKultabar.setProgress(TeamKulta.size() / TotalKulta);
            	TeamKultabar.setTitle("§e" + TeamKulta.size() + " pelaajaa jäljellä Kulta tiimissä!");
            	
            	if(TeamKuparibar.isVisible()) {
                	TeamKuparibar.setVisible(false);
                	TeamKultabar.setVisible(true);
                }else {
                	TeamKuparibar.setVisible(true);
                	TeamKultabar.setVisible(false);
                }
            	if (GameRunning) {
            		if (TeamKulta.size() == 0) {
            			getLogger().info("Kupari tiimi voitti siinä oli seuraavat pelaajat");
            			for (Player player : TeamKuparik) {
            				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            		            console.sendMessage(player.getName());
            		    }
            			getLogger().info("Joista seuraavat pelaajat selvisivät hengissä loppuun asti");
            			for (Player player : TeamKupari) {
            				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            		        
            		            console.sendMessage(player.getName());
            		        
            		    }
            			getLogger().info("Kuitenkin kiitos kaikille pelaamisesta");
            			for (Player player : TeamKuparik) {
            				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            		        
            		            console.sendMessage(player.getName());
            		        
            		    }
            			for (Player player : TeamKultak) {
            				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            		        
            		            console.sendMessage(player.getName());
            		        
            		    }
            			
            			getLogger().info("Myöhässä olevia pelaajia oli " + LATEPLAYERS);
            			
            			
            			TeamKuparibar.setVisible(true);
            			TeamKuparibar.setTitle("§6Kupari tiimi on voittanut!!!");
            			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            		        
            		        player.sendTitle("§6Kupari tiimi on voittanut!!!", "", 20, 60, 20);
            		    }
            			for (int i = 0; i < 5; i++) {
            				for (Player player : TeamKupari) {
            					spawnRandomFirework(player.getLocation());
            				}
            				
            			}
                        this.cancel();
                    }
            		if(TeamKupari.size() == 0) {
            			getLogger().info("Kulta tiimi voitti siinä oli seuraavat pelaajat");
            			for (Player player : TeamKultak) {
            				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            		            console.sendMessage(player.getName());
            		    }
            			getLogger().info("Joista seuraavat pelaajat selvisivät hengissä loppuun asti");
            			for (Player player : TeamKulta) {
            				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            		        
            		            console.sendMessage(player.getName());
            		        
            		    }
            			getLogger().info("Kuitenkin kiitos kaikille pelaamisesta");
            			for (Player player : TeamKuparik) {
            				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            		        
            		            console.sendMessage(player.getName());
            		        
            		    }
            			for (Player player : TeamKultak) {
            				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            		        
            		            console.sendMessage(player.getName());
            		        
            		    }
            			getLogger().info("Myöhässä olevia pelaajia oli " + LATEPLAYERS);
            			TeamKuparibar.setVisible(true);
            			TeamKuparibar.setTitle("§eKulta tiimi on voittanut!!!");
            			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            		        player.sendTitle("§eKulta tiimi on voittanut!!!", "", 20, 60, 20);
            		    }
            			for (int i = 0; i < 5; i++) {
            				for (Player player : TeamKulta) {
            					spawnRandomFirework(player.getLocation());
            				}
            				
            			}
            			this.cancel();
            		}
            		
            	}
                
            }
        }.runTaskTimerAsynchronously(this, 0L, 20L);  
    }
	
	public void spawnRandomFirework(Location location) {
	    Firework firework = location.getWorld().spawn(location, Firework.class);

	    FireworkEffect.Builder fireworkEffectBuilder = FireworkEffect.builder();

	    Random random = new Random();
	    FireworkEffect.Type[] types = FireworkEffect.Type.values();
	    FireworkEffect.Type effectType = types[random.nextInt(types.length)];
	    fireworkEffectBuilder.with(effectType);

	    Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.PURPLE, Color.ORANGE};
	    Color color1 = colors[random.nextInt(colors.length)];
	    Color color2 = colors[random.nextInt(colors.length)];
	    
	    fireworkEffectBuilder.withColor(color1);
	    fireworkEffectBuilder.withFade(color2);
	    

	    fireworkEffectBuilder.trail(random.nextBoolean());
	    fireworkEffectBuilder.flicker(random.nextBoolean());
	    

	    FireworkEffect fireworkEffect = fireworkEffectBuilder.build();
	    FireworkMeta fireworkMeta = firework.getFireworkMeta();
	    fireworkMeta.addEffect(fireworkEffect);
	    fireworkMeta.setPower(random.nextInt(3) + 1);
	    
	    firework.setFireworkMeta(fireworkMeta);
	}
	}

