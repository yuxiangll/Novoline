package cc.novoline.yuxiangll.fontRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;

@SuppressWarnings("unused")
public class FontLoaders {
    public UFontRenderer arial14;
    public UFontRenderer arial16;
    public UFontRenderer arial18;
    public UFontRenderer arial22;
    public UFontRenderer arial24;
    public UFontRenderer syFont18;
    public UFontRenderer syFont14;
    public UFontRenderer syFont16;
    public UFontRenderer syFont36;
    public UFontRenderer syFont72;
    public UFontRenderer default14, default16, default18, default22, default24, default36, default72;
    public UFontRenderer syFont20;
    public UFontRenderer micon = getFont("client/fonts/micon.ttf", 24, true);
    public UFontRenderer PingFang12 = getFont("client/fonts/PingFang_Normal.ttf", 12, true);
    public UFontRenderer PingFang14 = getFont("client/fonts/PingFang_Normal.ttf", 14, true);
    public UFontRenderer PingFang16 = getFont("client/fonts/PingFang_Normal.ttf", 16, true);
    public UFontRenderer PingFang17 = getFont("client/fonts/PingFang_Normal.ttf", 17, true);
    public UFontRenderer PingFang18 = getFont("client/fonts/PingFang_Normal.ttf", 18, true);
    public UFontRenderer PingFang19 = getFont("client/fonts/PingFang_Normal.ttf", 19, true);
    public UFontRenderer PingFang20 = getFont("client/fonts/PingFang_Normal.ttf", 20, true);
    public UFontRenderer PingFang21 = getFont("client/fonts/PingFang_Normal.ttf", 21, true);
    public UFontRenderer PingFang22 = getFont("client/fonts/PingFang_Normal.ttf", 22, true);
    public UFontRenderer PingFang23 = getFont("client/fonts/PingFang_Normal.ttf", 23, true);
    public UFontRenderer PingFang24 = getFont("client/fonts/PingFang_Normal.ttf", 24, true);
    public UFontRenderer PingFang25 = getFont("client/fonts/PingFang_Normal.ttf", 25, true);
    public UFontRenderer PingFang26 = getFont("client/fonts/PingFang_Normal.ttf", 26, true);

    public UFontRenderer PingFang36 = getFont("client/fonts/PingFang_Normal.ttf", 36, true);
    public UFontRenderer PingFang72 = getFont("client/fonts/PingFang_Normal.ttf", 72, true);
    public UFontRenderer PingFangBold14 = getFont("client/fonts/PingFang Bold.ttf", 18, true);

    public UFontRenderer PingFangBold16 = getFont("client/fonts/PingFang Bold.ttf", 16, true);

    public UFontRenderer PingFangBold18 = getFont("client/fonts/PingFang Bold.ttf", 18, true);
    public UFontRenderer PingFangBold24 = getFont("client/fonts/PingFang Bold.ttf", 24, true);
    public UFontRenderer PingFangBold36 = getFont("client/fonts/PingFang Bold.ttf", 36, true);
    public UFontRenderer PingFangBold72 = getFont("client/fonts/PingFang Bold.ttf", 72, true);
//    public UFontRenderer FLUXICON14 = getFont("client/fonts/fluxicon.ttf", 14, true);
//    public UFontRenderer FLUXICON16 = getFont("client/fonts/fluxicon.ttf", 16, true);

    public FontLoaders() {
        System.out.println("Started loading fonts");
        long t1 = System.currentTimeMillis();
        arial14 = getArial(14, true);
        arial16 = getArial(16, true);
        arial18 = getArial(18, true);
        arial22 = getArial(22, true);
        arial24 = getArial(24, true);
        syFont14 = getMiSans(14, true);
        syFont16 = getMiSans(16, true);
        syFont18 = getMiSans(18, true);
        syFont20 = getMiSans(20, true);
        syFont36 = getMiSans(36, true);
        syFont72 = getMiSans(72, true);
        default14 = getDefault(14, true);
        default16 = getDefault(16, true);
        default18 = getDefault(18, true);
        default22 = getDefault(22, true);
        default24 = getDefault(24, true);
        default36 = getDefault(36, true);
        default72 = getDefault(72, true);
        System.out.println("Fonts loaded:" + (System.currentTimeMillis() - t1) + "ms");
    }

    public UFontRenderer getDefault(int size, boolean antiAlias) {
        Font font = new Font("default", Font.PLAIN, size);
        return new UFontRenderer(font, size, antiAlias);
    }

    public UFontRenderer getMiSans(int size, boolean antiAlias) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("client/fonts/misans.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }

        return new UFontRenderer(font, size, antiAlias);
    }

    private UFontRenderer getClientFont(int size, boolean antiAlias) {
        return getFont("HarmonyOS_Sans_SC_Regular.ttf", size, antiAlias, false);
    }

    public UFontRenderer getFont(String fontName, int size, boolean antiAlias, boolean bold) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("client/fonts/" + fontName)).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(bold ? Font.BOLD : Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            font = new Font("default", bold ? Font.BOLD : Font.PLAIN, size);
        }

        return new UFontRenderer(font, size, antiAlias);
    }

    public UFontRenderer getArial(int size, boolean antiAlias) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("client/fonts/arial.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }

        return new UFontRenderer(font, size, antiAlias);
    }

    public UFontRenderer getFont(String locate, int size, boolean antiAlias) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(locate)).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }

        return new UFontRenderer(font, size, antiAlias);
    }

}

