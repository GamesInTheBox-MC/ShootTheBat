package me.hsgamer.gamesinthebox.shootthebat;

import me.hsgamer.gamesinthebox.game.simple.feature.SimplePointFeature;
import me.hsgamer.gamesinthebox.game.template.TemplateGame;
import me.hsgamer.gamesinthebox.game.template.TemplateGameArena;
import me.hsgamer.gamesinthebox.game.template.TemplateGameArenaLogic;
import me.hsgamer.gamesinthebox.game.template.TemplateGameEditor;
import me.hsgamer.gamesinthebox.game.template.expansion.TemplateGameExpansion;
import me.hsgamer.gamesinthebox.util.UpdateUtil;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ShootTheBat extends TemplateGameExpansion {
    public static final SimplePointFeature.PointValue POINT_KILL = new SimplePointFeature.PointValue("kill", 1, false);
    private final MessageConfig messageConfig = ConfigGenerator.newInstance(MessageConfig.class, new BukkitConfig(new File(getDataFolder(), "messages.yml")));

    @Override
    protected void enable() {
        UpdateUtil.notifyUpdate(this, "GamesInTheBox-MC/ShootTheBat");
    }

    @Override
    protected @NotNull String @NotNull [] getGameType() {
        return new String[]{"shoot-the-bat", "stb"};
    }

    @Override
    public TemplateGameArenaLogic createArenaLogic(TemplateGameArena arena) {
        return new GameArenaLogic(this, arena);
    }

    @Override
    public String getDisplayName() {
        return messageConfig.getDisplayName();
    }

    @Override
    public List<SimplePointFeature.PointValue> getPointValues() {
        return Collections.singletonList(POINT_KILL);
    }

    @Override
    public List<String> getDefaultHologramLines(String name) {
        return Optional.ofNullable(messageConfig.getDefaultHologramLines().get(name))
                .map(CollectionUtils::createStringListFromObject)
                .orElseGet(() -> super.getDefaultHologramLines(name));
    }

    @Override
    public TemplateGameEditor getEditor(TemplateGame game) {
        return new GameEditor(game);
    }
}
