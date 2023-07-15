package cc.novoline.yuxiangll;

import cc.novoline.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.Display;

public final class BackgroundShader extends Shader {
    private long lastFrame=0;

    public final static BackgroundShader BACKGROUND_SHADER = new BackgroundShader();

    private float time;

    public BackgroundShader() {
        super("background.frag");
    }

    @Override
    public void setupUniforms() {
        setupUniform("iResolution");
        setupUniform("iTime");
    }

    @Override
    public void updateUniforms() {
        lastFrame = getTime();

        final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getInstance());

        final int resolutionID = getUniform("iResolution");
        if(resolutionID > -1)
            GL20.glUniform2f(resolutionID, (float) Display.getWidth(), (float) Display.getHeight());
        final int timeID = getUniform("iTime");
        if(timeID > -1) GL20.glUniform1f(timeID, time);

        time += 0.005F * (int) (getTime() - lastFrame);
    }
    public long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

}
