package dev.isxander.adaptivetooltips.config;

import com.google.gson.GsonBuilder;
import dev.isxander.adaptivetooltips.config.gui.KeyCodeController;
import dev.isxander.yacl.api.*;
import dev.isxander.yacl.config.ConfigEntry;
import dev.isxander.yacl.config.GsonConfigInstance;
import dev.isxander.yacl.gui.controllers.LabelController;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import dev.isxander.yacl.gui.controllers.cycling.EnumController;
import dev.isxander.yacl.gui.controllers.slider.FloatSliderController;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class AdaptiveTooltipConfig {
    public static final GsonConfigInstance<AdaptiveTooltipConfig> INSTANCE = new GsonConfigInstance<>(AdaptiveTooltipConfig.class, FabricLoader.getInstance().getConfigDir().resolve("adaptive-tooltips.json"), GsonBuilder::setPrettyPrinting);

    @ConfigEntry public WrapTextBehaviour wrapText = WrapTextBehaviour.SCREEN_WIDTH;
    @ConfigEntry public boolean overwriteVanillaWrapping = false;
    @ConfigEntry public boolean prioritizeTooltipTop = true;
    @ConfigEntry public boolean bedrockCentering = true;
    @ConfigEntry public boolean bestCorner = false;
    @ConfigEntry public boolean alwaysBestCorner = false;
    @ConfigEntry public boolean preventVanillaClamping = true;
    @ConfigEntry public boolean onlyRepositionHoverTooltips = true;
    @ConfigEntry public boolean useYACLTooltipPositioner = false;
    @ConfigEntry public int scrollKeyCode = InputUtil.GLFW_KEY_LEFT_ALT;
    @ConfigEntry public int horizontalScrollKeyCode = InputUtil.GLFW_KEY_LEFT_CONTROL;
    @ConfigEntry public boolean smoothScrolling = true;
    @ConfigEntry public ScrollDirection scrollDirection = Util.getOperatingSystem() == Util.OperatingSystem.OSX ? ScrollDirection.NATURAL : ScrollDirection.REVERSE;
    @ConfigEntry public int verticalScrollSensitivity = 10;
    @ConfigEntry public int horizontalScrollSensitivity = 10;
    @ConfigEntry public float tooltipTransparency = 1f;
    @ConfigEntry public boolean removeFirstLinePadding = false;

    public static Screen makeScreen(Screen parent) {
        ConfigCategory.Builder categoryBuilder = ConfigCategory.createBuilder()
                .name(Text.translatable("adaptivetooltips.title"));

        OptionGroup.Builder contentManipulationGroup = OptionGroup.createBuilder()
                .name(Text.translatable("adaptivetooltips.group.content_manipulation.title"))
                .tooltip(Text.translatable("adaptivetooltips.group.content_manipulation.desc"));
        var textWrappingOpt = Option.createBuilder(WrapTextBehaviour.class)
                .name(Text.translatable("adaptivetooltips.opt.text_wrapping.title"))
                .tooltip(Text.translatable("adaptivetooltips.opt.text_wrapping.desc"))
                .tooltip(value -> {
                    MutableText tooltip = Text.translatable("options.generic_value", value.getDisplayName(), value.getTooltip());
                    if (value == WrapTextBehaviour.REMAINING_WIDTH)
                        tooltip.append("\n").append(Text.translatable("adaptivetooltips.wrap_text_behaviour.remaining_width.warning").formatted(Formatting.RED));
                    return tooltip.formatted(Formatting.GRAY);
                })
                .binding(
                        INSTANCE.getDefaults().wrapText,
                        () -> INSTANCE.getConfig().wrapText,
                        val -> INSTANCE.getConfig().wrapText = val
                )
                .controller(EnumController::new)
                .build();
        var preventVanillaWrappingOpt = Option.createBuilder(boolean.class)
                .name(Text.translatable("adaptivetooltips.opt.overwrite_vanilla_wrapping.title"))
                .tooltip(Text.translatable("adaptivetooltips.opt.overwrite_vanilla_wrapping.desc"))
                .binding(
                        INSTANCE.getDefaults().overwriteVanillaWrapping,
                        () -> INSTANCE.getConfig().overwriteVanillaWrapping,
                        val -> INSTANCE.getConfig().overwriteVanillaWrapping = val
                )
                .controller(TickBoxController::new)
                .build();
        contentManipulationGroup.option(textWrappingOpt);
        contentManipulationGroup.option(preventVanillaWrappingOpt);
        categoryBuilder.group(contentManipulationGroup.build());

        OptionGroup.Builder positioningGroup = OptionGroup.createBuilder()
                .name(Text.translatable("adaptivetooltips.group.positioning.title"))
                .tooltip(Text.translatable("adaptivetooltips.group.positioning.desc"));
        var prioritizeTooltipTopOpt = Option.createBuilder(boolean.class)
                .name(Text.translatable("adaptivetooltips.opt.prioritize_tooltip_top.title"))
                .tooltip(Text.translatable("adaptivetooltips.opt.prioritize_tooltip_top.desc"))
                .binding(
                        INSTANCE.getDefaults().prioritizeTooltipTop,
                        () -> INSTANCE.getConfig().prioritizeTooltipTop,
                        val -> INSTANCE.getConfig().prioritizeTooltipTop = val
                )
                .controller(TickBoxController::new)
                .build();
        var bedrockCenteringOpt = Option.createBuilder(boolean.class)
                .name(Text.translatable("adaptivetooltips.opt.bedrock_centering.title"))
                .tooltip(Text.translatable("adaptivetooltips.opt.bedrock_centering.desc"))
                .tooltip(Text.translatable("adaptivetooltips.gui.require_opt.on", Text.translatable("adaptivetooltips.opt.prevent_vanilla_clamping.title")).formatted(Formatting.RED))
                .binding(
                        INSTANCE.getDefaults().bedrockCentering,
                        () -> INSTANCE.getConfig().bedrockCentering,
                        val -> INSTANCE.getConfig().bedrockCentering = val
                )
                .controller(TickBoxController::new)
                .build();
        var alwaysAlignToCornerOpt = Option.createBuilder(boolean.class)
                .name(Text.translatable("adaptivetooltips.opt.always_align_corner.title"))
                .tooltip(Text.translatable("adaptivetooltips.opt.always_align_corner.desc"))
                .tooltip(Text.translatable("adaptivetooltips.gui.require_opt.on", Text.translatable("adaptivetooltips.opt.align_to_corner.title")).formatted(Formatting.RED))
                .binding(
                        INSTANCE.getDefaults().alwaysBestCorner,
                        () -> INSTANCE.getConfig().alwaysBestCorner,
                        val -> INSTANCE.getConfig().alwaysBestCorner = val
                )
                .controller(TickBoxController::new)
                .listener((opt, pendingVal) -> {
                    prioritizeTooltipTopOpt.setAvailable(!pendingVal);
                    bedrockCenteringOpt.setAvailable(!pendingVal);
                    if (pendingVal) {
                        prioritizeTooltipTopOpt.requestSet(false);
                        bedrockCenteringOpt.requestSet(false);
                    }
                })
                .build();
        var alignToCornerOpt = Option.createBuilder(boolean.class)
                .name(Text.translatable("adaptivetooltips.opt.align_to_corner.title"))
                .tooltip(Text.translatable("adaptivetooltips.opt.align_to_corner.desc"))
                .binding(
                        INSTANCE.getDefaults().bestCorner,
                        () -> INSTANCE.getConfig().bestCorner,
                        val -> INSTANCE.getConfig().bestCorner = val
                )
                .controller(TickBoxController::new)
                .listener((opt, pendingVal) -> {
                    alwaysAlignToCornerOpt.setAvailable(pendingVal);
                    if (!pendingVal)
                        alwaysAlignToCornerOpt.requestSet(false);
                })
                .build();
        var preventVanillaClampingOpt = Option.createBuilder(boolean.class)
                .name(Text.translatable("adaptivetooltips.opt.prevent_vanilla_clamping.title"))
                .tooltip(Text.translatable("adaptivetooltips.opt.prevent_vanilla_clamping.desc"))
                .binding(
                        INSTANCE.getDefaults().preventVanillaClamping,
                        () -> INSTANCE.getConfig().preventVanillaClamping,
                        val -> INSTANCE.getConfig().preventVanillaClamping = val
                )
                .controller(TickBoxController::new)
                .listener((opt, val) -> {
                    bedrockCenteringOpt.setAvailable(val);
                    if (!val) bedrockCenteringOpt.requestSet(false);
                })
                .build();
        var applyTweaksToAllPositioners = Option.createBuilder(boolean.class)
                .name(Text.translatable("adaptivetooltips.opt.only_reposition_hover_tooltips.title"))
                .tooltip(Text.translatable("adaptivetooltips.opt.only_reposition_hover_tooltips.desc"))
                .binding(
                        INSTANCE.getDefaults().onlyRepositionHoverTooltips,
                        () -> INSTANCE.getConfig().onlyRepositionHoverTooltips,
                        val -> INSTANCE.getConfig().onlyRepositionHoverTooltips = val
                )
                .controller(TickBoxController::new)
                .build();
        var useYACLTooltipPositionerOpt = Option.createBuilder(boolean.class)
                .name(Text.translatable("adaptivetooltips.opt.use_yacl_tooltip_positioner.title"))
                .tooltip(Text.translatable("adaptivetooltips.opt.use_yacl_tooltip_positioner.desc"))
                .binding(
                        INSTANCE.getDefaults().useYACLTooltipPositioner,
                        () -> INSTANCE.getConfig().useYACLTooltipPositioner,
                        val -> INSTANCE.getConfig().useYACLTooltipPositioner = val
                )
                .controller(TickBoxController::new)
                .build();
        positioningGroup.option(prioritizeTooltipTopOpt);
        positioningGroup.option(bedrockCenteringOpt);
        positioningGroup.option(alignToCornerOpt);
        positioningGroup.option(alwaysAlignToCornerOpt);
        positioningGroup.option(preventVanillaClampingOpt);
        positioningGroup.option(applyTweaksToAllPositioners);
        positioningGroup.option(useYACLTooltipPositionerOpt);
        categoryBuilder.group(positioningGroup.build());

        OptionGroup.Builder scrollingGroup = OptionGroup.createBuilder()
                .name(Text.translatable("adaptivetooltips.group.scrolling.title"))
                .tooltip(Text.translatable("adaptivetooltips.group.scrolling.desc"));
        var scrollingInstructions = Option.createBuilder(Text.class)
                .binding(Binding.immutable(Text.translatable("adaptivetooltips.label.scrolling_instructions", KeyCodeController.DEFAULT_FORMATTER.apply(INSTANCE.getConfig().scrollKeyCode), KeyCodeController.DEFAULT_FORMATTER.apply(INSTANCE.getConfig().horizontalScrollKeyCode))))
                .controller(LabelController::new)
                .build();
        var scrollKeyOpt = Option.createBuilder(int.class)
                .name(Text.translatable("adaptivetooltips.bind.scroll"))
                .binding(
                        INSTANCE.getDefaults().scrollKeyCode,
                        () -> INSTANCE.getConfig().scrollKeyCode,
                        val -> INSTANCE.getConfig().scrollKeyCode = val
                )
                .controller(KeyCodeController::new)
                .build();
        var horizontalScrollKeyOpt = Option.createBuilder(int.class)
                .name(Text.translatable("adaptivetooltips.bind.horizontal_scroll"))
                .binding(
                        INSTANCE.getDefaults().horizontalScrollKeyCode,
                        () -> INSTANCE.getConfig().horizontalScrollKeyCode,
                        val -> INSTANCE.getConfig().horizontalScrollKeyCode = val
                )
                .controller(KeyCodeController::new)
                .build();
        var smoothScrollingOpt = Option.createBuilder(boolean.class)
                .name(Text.translatable("adaptivetooltips.opt.smooth_scrolling.title"))
                .tooltip(Text.translatable("adaptivetooltips.opt.smooth_scrolling.desc"))
                .binding(
                        INSTANCE.getDefaults().smoothScrolling,
                        () -> INSTANCE.getConfig().smoothScrolling,
                        val -> INSTANCE.getConfig().smoothScrolling = val
                )
                .controller(TickBoxController::new)
                .build();
        var scrollDirectionOpt = Option.createBuilder(ScrollDirection.class)
                .name(Text.translatable("adaptivetooltips.opt.scroll_direction.title"))
                .tooltip(Text.translatable("adaptivetooltips.opt.scroll_direction.desc"))
                .binding(
                        INSTANCE.getDefaults().scrollDirection,
                        () -> INSTANCE.getConfig().scrollDirection,
                        val -> INSTANCE.getConfig().scrollDirection = val
                )
                .controller(EnumController::new)
                .build();
        var verticalScrollSensOpt = Option.createBuilder(int.class)
                .name(Text.translatable("adaptivetooltips.opt.vertical_scroll_sensitivity.title"))
                .tooltip(Text.translatable("adaptivetooltips.opt.vertical_scroll_sensitivity.desc"))
                .binding(
                        INSTANCE.getDefaults().verticalScrollSensitivity,
                        () -> INSTANCE.getConfig().verticalScrollSensitivity,
                        val -> INSTANCE.getConfig().verticalScrollSensitivity = val
                )
                .controller(opt -> new IntegerSliderController(opt, 5, 20, 1, val -> Text.translatable("adaptivetooltips.format.pixels", val)))
                .build();
        var horizontalScrollSensOpt = Option.createBuilder(int.class)
                .name(Text.translatable("adaptivetooltips.opt.horizontal_scroll_sensitivity.title"))
                .tooltip(Text.translatable("adaptivetooltips.opt.horizontal_scroll_sensitivity.desc"))
                .binding(
                        INSTANCE.getDefaults().horizontalScrollSensitivity,
                        () -> INSTANCE.getConfig().horizontalScrollSensitivity,
                        val -> INSTANCE.getConfig().horizontalScrollSensitivity = val
                )
                .controller(opt -> new IntegerSliderController(opt, 5, 20, 1, val -> Text.translatable("adaptivetooltips.format.pixels", val)))
                .build();
        scrollingGroup.option(scrollingInstructions);
        scrollingGroup.option(scrollKeyOpt);
        scrollingGroup.option(horizontalScrollKeyOpt);
        scrollingGroup.option(smoothScrollingOpt);
        scrollingGroup.option(scrollDirectionOpt);
        scrollingGroup.option(verticalScrollSensOpt);
        scrollingGroup.option(horizontalScrollSensOpt);
        categoryBuilder.group(scrollingGroup.build());

        OptionGroup.Builder styleGroup = OptionGroup.createBuilder()
                .name(Text.translatable("adaptivetooltips.group.style.title"))
                .tooltip(Text.translatable("adaptivetooltips.group.style.desc"));
        var tooltipTransparencyOpt = Option.createBuilder(float.class)
                .name(Text.translatable("adaptivetooltips.opt.tooltip_transparency.title"))
                .tooltip(Text.translatable("adaptivetooltips.opt.tooltip_transparency.desc"))
                .binding(
                        INSTANCE.getDefaults().tooltipTransparency,
                        () -> INSTANCE.getConfig().tooltipTransparency,
                        val -> INSTANCE.getConfig().tooltipTransparency = val
                )
                .controller(opt -> new FloatSliderController(opt, 0f, 1.5f, 0.05f, val -> val == 1f ? Text.translatable("adaptivetooltips.format.vanilla") : Text.of(String.format("%+,.0f%%", (val - 1) * 100))))
                .build();
        var removeFirstLinePaddingOpt = Option.createBuilder(boolean.class)
                .name(Text.translatable("adaptivetooltips.opt.remove_first_line_padding.title"))
                .tooltip(Text.translatable("adaptivetooltips.opt.remove_first_line_padding.desc"))
                .binding(
                        INSTANCE.getDefaults().removeFirstLinePadding,
                        () -> INSTANCE.getConfig().removeFirstLinePadding,
                        val -> INSTANCE.getConfig().removeFirstLinePadding = val
                )
                .controller(TickBoxController::new)
                .build();
        styleGroup.option(tooltipTransparencyOpt);
        styleGroup.option(removeFirstLinePaddingOpt);
        categoryBuilder.group(styleGroup.build());

        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("adaptivetooltips.title"))
                .category(categoryBuilder.build())
                .save(INSTANCE::save)
                .build()
                .generateScreen(parent);
    }
}
