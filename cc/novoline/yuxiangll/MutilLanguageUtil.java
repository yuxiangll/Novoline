package cc.novoline.yuxiangll;

import net.minecraft.client.Minecraft;

/**
 * @author yuxiangll
 * @package cc.novoline.yuxiangll
 * don't mind
 * @date 2023/8/15 22:38
 */
public class MutilLanguageUtil {

    //多语言支持 English (UK) 简体中文 (中国)
    // System.out.println(mc.mcLanguageManager.getCurrentLanguage().toString());
    public static String getString(String chinese,String english){
        if (Minecraft.getInstance().mcLanguageManager.getCurrentLanguage().toString().contains("简体中文")){
            return chinese;
        }else {
            return english;
        }
    }
}
