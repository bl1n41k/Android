package com.example.voiceassistent.translateapi;

public enum Languages {

	AUTO_DETECT("auto"),
	ENGLISH("en"),
	RUSSIAN("ru");

    private final String languageCode;

    private Languages(final String languageCode){
    	this.languageCode = languageCode;
    }

	public String getLanguageCode() {
		return languageCode;
	}
	
	@Override
    public String toString(){
    	return getLanguageCode();
    }
}
