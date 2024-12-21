package com.example.potholepatrol.Language;

public class LanguageItem {
    private int flagResource;
    private String languageName;
    private String languageCode;

    public LanguageItem(int flagResource, String languageName, String languageCode) {
        this.flagResource = flagResource;
        this.languageName = languageName;
        this.languageCode = languageCode;
    }

    public int getFlagResource() { return flagResource; }
    public String getLanguageName() { return languageName; }
    public String getLanguageCode() { return languageCode; }
}