package me.hsgamer.gamesinthebox.shootthebat.feature;

import me.hsgamer.gamesinthebox.game.simple.SimpleGameArena;
import me.hsgamer.gamesinthebox.game.simple.feature.SimplePointFeature;
import me.hsgamer.gamesinthebox.shootthebat.ShootTheBat;
import me.hsgamer.minigamecore.base.Feature;
import org.bukkit.Bukkit;
import org.bukkit.entity.Bat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class ListenerFeature implements Feature, Listener {
    private final ShootTheBat expansion;
    private final SimpleGameArena arena;
    private BatFeature batFeature;
    private SimplePointFeature pointFeature;

    public ListenerFeature(ShootTheBat expansion, SimpleGameArena arena) {
        this.expansion = expansion;
        this.arena = arena;
    }

    @Override
    public void init() {
        this.batFeature = this.arena.getFeature(BatFeature.class);
        this.pointFeature = this.arena.getFeature(SimplePointFeature.class);
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, expansion.getPlugin());
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBatDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Bat)) return;

        Player player = entity.getKiller();
        if (player == null) return;

        if (!batFeature.contains(entity)) return;
        pointFeature.applyPoint(player.getUniqueId(), ShootTheBat.POINT_KILL);
    }
}
