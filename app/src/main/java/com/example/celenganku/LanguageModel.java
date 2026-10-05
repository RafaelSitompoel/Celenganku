package com.example.celenganku;

public class LanguageModel {
    public String name;
    public String code;

    public LanguageModel(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static LanguageModel[] SUPPORTED_LANGUAGES = {
            new LanguageModel("Bahasa Indonesia", "id"),
            new LanguageModel("English (US)", "en"),
            new LanguageModel("Español (Spanish)", "es"),
            new LanguageModel("Français (French)", "fr"),
            new LanguageModel("Deutsch (German)", "de"),
            new LanguageModel("日本語 (Japanese)", "ja"),
            new LanguageModel("한국어 (Korean)", "ko"),
            new LanguageModel("中文 (Chinese)", "zh"),
            new LanguageModel("العربية (Arabic)", "ar"),
            new LanguageModel("Русский (Russian)", "ru"),
            new LanguageModel("Português (Portuguese)", "pt"),
            new LanguageModel("Italiano (Italian)", "it"),
            new LanguageModel("Nederlands (Dutch)", "nl"),
            new LanguageModel("Polski (Polish)", "pl"),
            new LanguageModel("Türkçe (Turkish)", "tr"),
            new LanguageModel("Tiếng Việt (Vietnamese)", "vi"),
            new LanguageModel("ไทย (Thai)", "th"),
            new LanguageModel("हिन्दी (Hindi)", "hi"),
            new LanguageModel("Bahasa Melayu (Malay)", "ms"),
            new LanguageModel("Basa Jawa (Javanese)", "jv"),
            new LanguageModel("Basa Sunda (Sundanese)", "su"),
            new LanguageModel("Filipino (Tagalog)", "fil"),
            new LanguageModel("Svenska (Swedish)", "sv"),
            new LanguageModel("Dansk (Danish)", "da"),
            new LanguageModel("Suomi (Finnish)", "fi"),
            new LanguageModel("Norsk (Norwegian)", "no"),
            new LanguageModel("Ελληνικά (Greek)", "el"),
            new LanguageModel("Magyar (Hungarian)", "hu"),
            new LanguageModel("Čeština (Czech)", "cs"),
            new LanguageModel("Română (Romanian)", "ro"),
            new LanguageModel("Українська (Ukrainian)", "uk"),
            new LanguageModel("Български (Bulgarian)", "bg"),
            new LanguageModel("Hrvatski (Croatian)", "hr"),
            new LanguageModel("Slovenčina (Slovak)", "sk"),
            new LanguageModel("Slovenščina (Slovenian)", "sl"),
            new LanguageModel("Eesti (Estonian)", "et"),
            new LanguageModel("Latviešu (Latvian)", "lv"),
            new LanguageModel("Lietuvių (Lithuanian)", "lt"),
            new LanguageModel("Shqip (Albanian)", "sq"),
            new LanguageModel("Македонски (Macedonian)", "mk"),
            new LanguageModel("Српski (Serbian)", "sr"),
            new LanguageModel("فارسی (Persian)", "fa"),
            new LanguageModel("Urdu (اردو)", "ur"),
            new LanguageModel("Kiswahili (Swahili)", "sw"),
            new LanguageModel("አማርኛ (Amharic)", "am"),
            new LanguageModel("தமிழ் (Tamil)", "ta"),
            new LanguageModel("తెలుగు (Telugu)", "te"),
            new LanguageModel("ಕನ್ನಡ (Kannada)", "kn"),
            new LanguageModel("മലയാളം (Malayalam)", "ml"),
            new LanguageModel("मराठी (Marathi)", "mr"),
            new LanguageModel("ગુજરાતી (Gujarati)", "gu"),
            new LanguageModel("ਪੰਜਾਬੀ (Punjabi)", "pa"),
            new LanguageModel("မြန်မာ (Burmese)", "my"),
            new LanguageModel("ខ្មែរ (Khmer)", "km"),
            new LanguageModel("ລາວ (Lao)", "lo")
    };
}