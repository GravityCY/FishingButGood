package me.gravityio.multiline_mastery.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public class RenderHelper {

    /**
     * Draws a line from point a to point b
     */
    public static void drawLine(DrawContext context, float x1, float x2, float y1, float y2, float width) {
        Matrix4f m = context.getMatrices().peek().getPositionMatrix();
        VertexConsumer v = context.getVertexConsumers().getBuffer(RenderLayer.getGui());

        Vector2f from = new Vector2f(x1, y1);
        Vector2f to = new Vector2f(x2, y2);

        Vector2f dir = new Vector2f();
        from.sub(to, dir);
        dir.normalize();

        Vector2f ortho = new Vector2f(-dir.y, dir.x).mul(width);

        Vector2f a = new Vector2f(from).add(ortho);
        Vector2f b = new Vector2f(from).sub(ortho);
        Vector2f c = new Vector2f(to).sub(ortho);
        Vector2f d = new Vector2f(to).add(ortho);

        v.vertex(m, a.x, a.y, 0).color(0xffffffff).next();
        v.vertex(m, b.x, b.y, 0).color(0xffffffff).next();
        v.vertex(m, c.x, c.y, 0).color(0xffffffff).next();
        v.vertex(m, d.x, d.y, 0).color(0xffffffff).next();

        context.draw();
    }


}
