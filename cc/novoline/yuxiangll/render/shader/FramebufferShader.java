/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package cc.novoline.yuxiangll.render.shader;

import net.minecraft.client.shader.Framebuffer;

/**
 * @author TheSlowly
 */
public abstract class FramebufferShader extends Shader {

    private static Framebuffer framebuffer;

    protected float red, green, blue, alpha = 1F;
    protected float radius = 2F;
    protected float quality = 1F;

    private boolean entityShadows;

    public FramebufferShader(final String fragmentShader) {
        super(fragmentShader);
    }



    /**
     * @param frameBuffer
     * @return frameBuffer
     * @author TheSlowly
     */
    public Framebuffer setupFrameBuffer(Framebuffer frameBuffer) {
        if(frameBuffer != null)
            frameBuffer.deleteFramebuffer();

        frameBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);

        return frameBuffer;
    }

}
