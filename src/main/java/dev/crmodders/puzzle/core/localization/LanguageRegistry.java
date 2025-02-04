package dev.crmodders.puzzle.core.localization;

import dev.crmodders.puzzle.core.Identifier;
import dev.crmodders.puzzle.core.localization.files.MergedLanguageFile;
import dev.crmodders.puzzle.core.registries.MapRegistry;

import java.util.HashMap;
import java.util.Map;

public class LanguageRegistry extends MapRegistry<Language> {

    private final Map<Identifier, ILanguageFile> files = new HashMap<>();

    public LanguageRegistry(Identifier identifier) {
        super(identifier, new HashMap<>(), true, false);
    }

    public void register(ILanguageFile lang) {
        TranslationLocale locale = lang.locale();
        Identifier localeIdentifier = locale.toIdentifier();

        if (files.get(localeIdentifier) instanceof MergedLanguageFile merged) {
            merged.addLanguageFile(lang);
        } else {
            MergedLanguageFile merged = new MergedLanguageFile(locale);
            merged.addLanguageFile(lang);
            files.put(localeIdentifier, merged);
        }

        if (!values.containsKey(localeIdentifier)) {
            Language language = new Language(files.get(localeIdentifier));
            values.put(localeIdentifier, language);
        }
    }

}
