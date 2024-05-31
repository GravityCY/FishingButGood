package me.gravityio.multiline_mastery.gui;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.slider.IntegerSliderController;

public class AngleSliderController extends IntegerSliderController {

    public AngleSliderController(Option<Integer> option, int min, int max, int interval) {
        super(option, min, max, interval);
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new AngleWidget(this, screen, widgetDimension, min(), max(), interval());
    }
}
