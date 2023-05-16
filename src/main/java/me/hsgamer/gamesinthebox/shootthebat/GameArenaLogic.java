package me.hsgamer.gamesinthebox.shootthebat;

import me.hsgamer.gamesinthebox.game.feature.BoundingFeature;
import me.hsgamer.gamesinthebox.game.feature.BoundingOffsetFeature;
import me.hsgamer.gamesinthebox.game.feature.GameConfigFeature;
import me.hsgamer.gamesinthebox.game.feature.PointFeature;
import me.hsgamer.gamesinthebox.game.simple.feature.SimpleBoundingFeature;
import me.hsgamer.gamesinthebox.game.simple.feature.SimpleBoundingOffsetFeature;
import me.hsgamer.gamesinthebox.game.simple.feature.SimpleRewardFeature;
import me.hsgamer.gamesinthebox.game.template.TemplateGameArena;
import me.hsgamer.gamesinthebox.game.template.TemplateGameArenaLogic;
import me.hsgamer.gamesinthebox.shootthebat.feature.BatFeature;
import me.hsgamer.gamesinthebox.shootthebat.feature.ListenerFeature;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.minigamecore.base.Feature;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameArenaLogic extends TemplateGameArenaLogic {
    private final ShootTheBat expansion;
    private int maxSpawn = 10;

    public GameArenaLogic(ShootTheBat expansion, TemplateGameArena arena) {
        super(arena);
        this.expansion = expansion;
    }

    public int getMaxSpawn() {
        return maxSpawn;
    }

    @Override
    public void forceEnd() {
        arena.getFeature(ListenerFeature.class).unregister();

        BatFeature batFeature = arena.getFeature(BatFeature.class);
        batFeature.setClearAllEntities(false);
        batFeature.stopTask();
        batFeature.clearAllEntities();
    }

    @Override
    public void postInit() {
        GameConfigFeature configFeature = arena.getFeature(GameConfigFeature.class);

        maxSpawn = Optional.ofNullable(configFeature.getString("bat.max-spawn"))
                .flatMap(Validate::getNumber)
                .map(Number::intValue)
                .orElse(maxSpawn);

        BoundingFeature boundingFeature = arena.getFeature(BoundingFeature.class);
        arena.getFeature(BatFeature.class).addEntityClearCheck(entity -> !boundingFeature.checkBounding(entity.getLocation(), true));
    }

    @Override
    public List<Feature> loadFeatures() {
        SimpleBoundingFeature boundingFeature = new SimpleBoundingFeature(arena);
        return Arrays.asList(
                boundingFeature,
                new SimpleBoundingOffsetFeature(arena, boundingFeature, true),
                new BatFeature(arena),
                new ListenerFeature(expansion, arena)
        );
    }

    @Override
    public void onInGameStart() {
        arena.getFeature(ListenerFeature.class).register();
        arena.getFeature(BatFeature.class).startTask();
    }

    @Override
    public void onInGameUpdate() {
        BoundingOffsetFeature boundingOffsetFeature = arena.getFeature(BoundingOffsetFeature.class);
        BatFeature batFeature = arena.getFeature(BatFeature.class);

        long size = batFeature.countValid() + batFeature.countSpawnRequests();

        long toSpawn = maxSpawn - (int) size;
        for (int i = 0; i < toSpawn; i++) {
            batFeature.spawn(boundingOffsetFeature.getRandomLocation());
        }
    }

    @Override
    public void onEndingStart() {
        List<UUID> topList = arena.getFeature(PointFeature.class).getTopUUID().collect(Collectors.toList());
        arena.getFeature(SimpleRewardFeature.class).tryReward(topList);

        arena.getFeature(BatFeature.class).setClearAllEntities(true);
    }

    @Override
    public boolean isEndingOver() {
        return super.isEndingOver() && arena.getFeature(BatFeature.class).isAllEntityCleared();
    }

    @Override
    public void onEndingOver() {
        arena.getFeature(ListenerFeature.class).unregister();

        BatFeature batFeature = arena.getFeature(BatFeature.class);
        batFeature.setClearAllEntities(false);
        batFeature.stopTask();
        batFeature.clearAllEntities();
    }
}
