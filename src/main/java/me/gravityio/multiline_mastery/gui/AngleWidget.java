package me.gravityio.multiline_mastery.gui;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.slider.ISliderController;
import dev.isxander.yacl3.gui.controllers.slider.SliderControllerElement;
import me.gravityio.multiline_mastery.helper.Helper;
import me.gravityio.multiline_mastery.render.RenderHelper;
import net.minecraft.client.gui.DrawContext;
import org.joml.Vector2f;

public class AngleWidget extends SliderControllerElement {

    public static final float NEG_HALF_PI = (float) (-Math.PI / 2);
    public static final int OFFSET = 2;
    public static final float WIDTH = 0.5f;

    protected Vector2f origin;
    protected Vector2f linea;
    protected Vector2f lineb;

    protected int length;
    protected float halfAngle;
    protected String current;
    protected int index = 0;
    private int previousValue;

    public AngleWidget(ISliderController<?> option, YACLScreen screen, Dimension<Integer> dim, double min, double max, double interval) {
        super(option, screen, dim, min, max, interval);
        this.updateAngle();
    }

    private void updateAngle() {
        this.halfAngle = (float) (Math.toRadians(super.control.pendingValue()) / 2);
    }

    private void updateLines() {
        float tx = (float) (Math.cos(NEG_HALF_PI - this.halfAngle) * this.length);
        float ty = (float) (Math.sin(NEG_HALF_PI - this.halfAngle) * this.length);
        this.linea = new Vector2f(tx, ty);

        tx = (float) (Math.cos(NEG_HALF_PI + this.halfAngle) * this.length);
        ty = (float) (Math.sin(NEG_HALF_PI + this.halfAngle) * this.length);
        this.lineb = new Vector2f(tx, ty);
    }

    public void setValue(String s) {
        Integer v = Helper.toInt(s);
        if (v == null) {
            v = Integer.MIN_VALUE;
        }

        this.setValue(v);
    }

    public void setValue(int v) {
        if (v < super.control.min()) v = (int) super.control.min();
        if (v > super.control.max()) v = (int) super.control.max();
        super.control.setPendingValue(v);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        if (keyCode >= 48 && keyCode <= 57) {
            int num = keyCode - 48;
            int s = this.index - 1;
            int e = this.index + 1;
            String s1 = s < 0 ? "" : this.current.substring(s, this.index);
            String s2 = e > this.current.length() ? "" : this.current.substring(e);
            this.setValue(s1 + num + s2);
            this.index = (this.index + 1) % this.current.length();
            return true;
        }
        return false;
    }

    @Override
    public void setDimension(Dimension<Integer> dim) {
        super.setDimension(dim);
        var newLength = dim.height() - this.OFFSET * 2;
        if (this.length != newLength) {
            this.length = newLength;
            this.updateLines();
        }
        this.origin = new Vector2f(dim.x() + (float) dim.width() + 33, dim.y() + dim.height() - this.OFFSET);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (super.control.pendingValue() != this.previousValue) {
            this.updateAngle();
            this.updateLines();
            this.previousValue = (int) super.control.pendingValue();
            this.current = String.valueOf(this.previousValue);
        }

        RenderHelper.drawLine(context, this.origin.x, this.origin.x + this.linea.x, this.origin.y, this.origin.y + this.linea.y, this.WIDTH);
        RenderHelper.drawLine(context, this.origin.x, this.origin.x + this.lineb.x, this.origin.y, this.origin.y + this.lineb.y, this.WIDTH);
    }
}
