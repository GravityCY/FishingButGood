package me.gravityio.multiline_mastery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import me.gravityio.multiline_mastery.api.Watched;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModConfig {

    public static final ConfigClassHandler<ModConfig> HANDLER = ConfigClassHandler.createBuilder(ModConfig.class)
            .id(Identifier.of(MultilineMasteryMod.MOD_ID, "my_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("multiline_mastery.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .appendGsonBuilder(builder -> builder.registerTypeHierarchyAdapter(Watched.class, new Watched.Adapter<>(new Gson().getAdapter(Integer.class))))
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry
    public Watched<Integer> angle = new Watched<>(15);
    @SerialEntry
    public boolean highlight = true;

    public Screen getScreen(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER, (defaults, config, builder) -> {
            var category = ConfigCategory.createBuilder();
            category.name(Text.translatable("yacl.multilinemastery.title"));

            var angleOpt = Option.<Integer>createBuilder()
                    .name(Text.translatable("yacl.multilinemastery.angle.label"))
                    .description(OptionDescription.of(Text.translatable("yacl.multilinemastery.angle.description")))
                    .binding(defaults.angle.get(), config.angle::get, config.angle::set)
                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).step(1).range(1, 90).formatValue(v -> Text.literal(v + "deg")));

            var highlightOpt = Option.<Boolean>createBuilder()
                    .name(Text.translatable("yacl.multilinemastery.highlight.label"))
                    .description(OptionDescription.of(Text.translatable("yacl.multilinemastery.highlight.description")))
                    .binding(defaults.highlight, () -> config.highlight, v -> config.highlight = v)
                    .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true).yesNoFormatter());

            category.option(angleOpt.build());
            category.option(highlightOpt.build());

            builder.title(Text.translatable("yacl.multilinemastery.title"));
            builder.category(category.build());

            return builder;
        }).generateScreen(parent);
    }
}
