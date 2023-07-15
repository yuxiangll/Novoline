package cc.novoline.yuxiangll.render.shader.shaders;

import cc.novoline.yuxiangll.render.RenderUtils;
import cc.novoline.yuxiangll.render.shader.Shader;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.Display;

public final class BackgroundShader extends Shader {

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
        final ScaledResolution scaledResolution = new ScaledResolution(mc);

        final int resolutionID = getUniform("iResolution");
        if(resolutionID > -1)
            GL20.glUniform2f(resolutionID, (float) Display.getWidth(), (float) Display.getHeight());
        final int timeID = getUniform("iTime");
        if(timeID > -1) GL20.glUniform1f(timeID, time);

        time += 0.005F * RenderUtils.deltaTime;
    }

}
