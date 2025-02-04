package dev.crmodders.puzzle.game.mixins.refactors.window;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import finalforeach.cosmicreach.lwjgl3.Lwjgl3Launcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Lwjgl3Launcher.class)
public class IconMixin {

    @Redirect(method = "getDefaultConfiguration", at = @At(value = "INVOKE", target = "Lcom/badlogic/gdx/backends/lwjgl3/Lwjgl3ApplicationConfiguration;setWindowIcon([Ljava/lang/String;)V"))
    private static void setIcon(Lwjgl3ApplicationConfiguration instance, String[] strings) {
        instance.setWindowIcon("icons/PuzzleLoaderIconx128.png", "icons/PuzzleLoaderIconx64.png", "icons/PuzzleLoaderIconx48.png", "icons/PuzzleLoaderIconx32.png", "icons/PuzzleLoaderIconx16.png");
    }

}
