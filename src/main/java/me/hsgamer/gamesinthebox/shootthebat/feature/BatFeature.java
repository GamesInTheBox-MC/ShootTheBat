package me.hsgamer.gamesinthebox.shootthebat.feature;

import me.hsgamer.gamesinthebox.game.feature.EntityFeature;
import me.hsgamer.gamesinthebox.game.feature.GameConfigFeature;
import me.hsgamer.gamesinthebox.game.simple.SimpleGameArena;
import me.hsgamer.gamesinthebox.util.Util;
import me.hsgamer.hscore.common.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BatFeature extends EntityFeature {
    private final SimpleGameArena arena;
    private List<String> nameTags = Collections.emptyList();

    public BatFeature(SimpleGameArena arena) {
        this.arena = arena;
    }

    @Override
    public void postInit() {
        GameConfigFeature config = arena.getFeature(GameConfigFeature.class);

        nameTags = Optional.ofNullable(config.get("bat.name-tag"))
                .map(CollectionUtils::createStringListFromObject)
                .orElse(nameTags);
    }

    @Override
    protected @Nullable Entity createEntity(Location location) {
        return location.getWorld().spawn(location, Bat.class, bat -> {
            bat.setHealth(2.0D);
            bat.setGlowing(true);

            String nameTag = Util.getRandomColorizedString(nameTags, "");
            if (!nameTag.isEmpty()) {
                bat.setCustomName(nameTag);
                bat.setCustomNameVisible(true);
            }
        });
    }

    public List<String> getNameTags() {
        return nameTags;
    }
}
