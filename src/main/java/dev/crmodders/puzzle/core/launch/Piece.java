package dev.crmodders.puzzle.core.launch;

import dev.crmodders.puzzle.core.mod.ModLocator;
import dev.crmodders.puzzle.core.providers.api.IGameProvider;
import dev.crmodders.puzzle.core.providers.impl.CosmicReachProvider;
import dev.crmodders.puzzle.utils.MethodUtil;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Piece {
    public String DEFAULT_PROVIDER = CosmicReachProvider.class.getName();
    public static IGameProvider provider;

    public static Map<String, Object> blackboard;
    public static PuzzleClassLoader classLoader;

    public static final Logger logger = LogManager.getLogger("Puzzle | Loader");

    public static void main(String[] args) {
        new Piece().launch(args);
    }

    public static LAUNCH_STATE MOD_LAUNCH_STATE;

    public enum LAUNCH_STATE {
        TRANSFORMER_INJECT,
        PRE_INIT,
        INIT,
        IN_GAME
    }

    private Piece() {
        List<URL> classPath = new ArrayList<>();

        classPath.addAll(ModLocator.getUrlsOnClasspath());
        ModLocator.crawlModsFolder(classPath);

        classLoader = new PuzzleClassLoader(classPath);
        blackboard = new HashMap<>();
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    private void launch(String[] args) {
        final OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();

        final OptionSet options = parser.parse(args);
        try {
            OptionSpec<String> provider_option = parser.accepts("gameProvider").withOptionalArg().ofType(String.class);
            OptionSpec<String> modFolder_option = parser.accepts("modFolder").withOptionalArg().ofType(String.class);

            classLoader.addClassLoaderExclusion(DEFAULT_PROVIDER.substring(0, DEFAULT_PROVIDER.lastIndexOf('.')));
            classLoader.addClassLoaderExclusion("dev.crmodders.puzzle.annotations");
            classLoader.addClassLoaderExclusion("dev.crmodders.puzzle.core.entrypoint");
            classLoader.addClassLoaderExclusion("dev.crmodders.puzzle.core.launch");
            classLoader.addClassLoaderExclusion("dev.crmodders.puzzle.core.mod");
            classLoader.addClassLoaderExclusion("dev.crmodders.puzzle.core.providers");
            classLoader.addClassLoaderExclusion("dev.crmodders.puzzle.core.tags");
            classLoader.addClassLoaderExclusion("dev.crmodders.puzzle.utils");

            if (options.has(provider_option))
                provider = (IGameProvider) Class.forName(provider_option.value(options ), true, classLoader).newInstance();
            else
                provider = (IGameProvider) Class.forName(DEFAULT_PROVIDER, true, classLoader).newInstance();



            provider.registerTransformers(classLoader);
            provider.initArgs(args);
            provider.inject(classLoader);

            String[] providerArgs = provider.getArgs().toArray(new String[0]);

            Class<?> clazz = Class.forName(provider.getEntrypoint(), false, classLoader);
            Method main = MethodUtil.getMethod(clazz,"main", String[].class);
            logger.info("Launching {} version {}", provider.getName(), provider.getRawVersion());
            MethodUtil.runStaticMethod(main, (Object) providerArgs);
        } catch (Exception e) {
            logger.error("Unable To Launch", e);
            System.exit(1);
        }
    }
}