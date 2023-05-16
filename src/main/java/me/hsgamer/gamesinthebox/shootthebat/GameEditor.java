package me.hsgamer.gamesinthebox.shootthebat;

import me.hsgamer.gamesinthebox.game.GameArena;
import me.hsgamer.gamesinthebox.game.simple.action.NumberAction;
import me.hsgamer.gamesinthebox.game.simple.feature.SimpleBoundingFeature;
import me.hsgamer.gamesinthebox.game.simple.feature.SimpleBoundingOffsetFeature;
import me.hsgamer.gamesinthebox.game.template.TemplateGame;
import me.hsgamer.gamesinthebox.game.template.TemplateGameArenaLogic;
import me.hsgamer.gamesinthebox.game.template.TemplateGameEditor;
import me.hsgamer.gamesinthebox.game.template.feature.ArenaLogicFeature;
import me.hsgamer.gamesinthebox.shootthebat.feature.BatFeature;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GameEditor extends TemplateGameEditor {
    private final SimpleBoundingFeature.Editor simpleBoundingFeatureEditor = SimpleBoundingFeature.editor(true);
    private final SimpleBoundingOffsetFeature.Editor simpleBoundingOffsetFeatureEditor = SimpleBoundingOffsetFeature.editor();
    private final List<String> nameTags = new ArrayList<>();
    private Integer maxSpawn;

    public GameEditor(@NotNull TemplateGame game) {
        super(game);
    }

    @Override
    protected @NotNull Map<String, SimpleAction> createActionMap() {
        Map<String, SimpleAction> map = super.createActionMap();

        map.putAll(simpleBoundingFeatureEditor.getActions());
        map.putAll(simpleBoundingOffsetFeatureEditor.getActions());

        map.put("set-max-spawn", new NumberAction() {
            @Override
            protected boolean performAction(@NotNull CommandSender sender, @NotNull Number value, String... args) {
                maxSpawn = value.intValue();
                return true;
            }

            @Override
            public @NotNull String getDescription() {
                return "Set the maximum number of bats that can be spawned";
            }
        });
        map.put("add-name-tag", new SimpleAction() {
            @Override
            public @NotNull String getDescription() {
                return "Add a name tag to the list";
            }

            @Override
            public boolean performAction(@NotNull CommandSender sender, @NotNull String... args) {
                if (args.length == 0) {
                    return false;
                }
                nameTags.add(String.join(" ", args));
                return true;
            }

            @Override
            public @NotNull String getArgsUsage() {
                return "<name tag>";
            }
        });
        map.put("clear-name-tags", new SimpleAction() {
            @Override
            public @NotNull String getDescription() {
                return "Clear the name tags list";
            }

            @Override
            public boolean performAction(@NotNull CommandSender sender, @NotNull String... args) {
                nameTags.clear();
                return true;
            }
        });

        return map;
    }

    @Override
    protected @NotNull List<@NotNull SimpleEditorStatus> createEditorStatusList() {
        List<@NotNull SimpleEditorStatus> list = super.createEditorStatusList();
        list.add(simpleBoundingFeatureEditor.getStatus());
        list.add(simpleBoundingOffsetFeatureEditor.getStatus());
        list.add(new SimpleEditorStatus() {
            @Override
            public void sendStatus(@NotNull CommandSender sender) {
                MessageUtils.sendMessage(sender, "&6&lShoot the Bat");
                MessageUtils.sendMessage(sender, "&6Max Spawn: &f" + (maxSpawn == null ? "Default" : maxSpawn));
                MessageUtils.sendMessage(sender, "&6Name Tags: ");
                nameTags.forEach(nameTag -> MessageUtils.sendMessage(sender, "&f- " + nameTag));
            }

            @Override
            public void reset(@NotNull CommandSender sender) {
                maxSpawn = null;
                nameTags.clear();
            }

            @Override
            public boolean canSave(@NotNull CommandSender sender) {
                return true;
            }

            @Override
            public Map<String, Object> toPathValueMap(@NotNull CommandSender sender) {
                Map<String, Object> map = new LinkedHashMap<>();
                if (!nameTags.isEmpty()) {
                    map.put("bat.name-tag", nameTags);
                }
                if (maxSpawn != null) {
                    map.put("bat.max-spawn", maxSpawn);
                }
                return map;
            }
        });
        return list;
    }

    @Override
    public boolean migrate(@NotNull CommandSender sender, @NotNull GameArena gameArena) {
        ArenaLogicFeature arenaLogicFeature = gameArena.getFeature(ArenaLogicFeature.class);
        if (arenaLogicFeature == null) {
            return false;
        }
        TemplateGameArenaLogic templateGameArenaLogic = arenaLogicFeature.getArenaLogic();
        if (!(templateGameArenaLogic instanceof GameArenaLogic)) {
            return false;
        }
        GameArenaLogic gameArenaLogic = (GameArenaLogic) templateGameArenaLogic;

        nameTags.clear();
        nameTags.addAll(gameArena.getFeature(BatFeature.class).getNameTags());

        maxSpawn = gameArenaLogic.getMaxSpawn();
        simpleBoundingFeatureEditor.migrate(gameArena.getFeature(SimpleBoundingFeature.class));
        simpleBoundingOffsetFeatureEditor.migrate(gameArena.getFeature(SimpleBoundingOffsetFeature.class));
        return super.migrate(sender, gameArena);
    }
}
