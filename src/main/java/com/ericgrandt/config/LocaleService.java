package com.ericgrandt.config;

import org.spongepowered.api.event.Listener;
import sawfowl.localeapi.event.LocaleServiseEvent;

public class LocaleService {

    sawfowl.localeapi.api.LocaleService localeService;

    Locales locales;

    public sawfowl.localeapi.api.LocaleService get() {
        return localeService;
    }

    void setLocales(Locales locales) {
        this.locales = locales;
    }

    @Listener
    public void getLocaleService(LocaleServiseEvent.Started event) {
        localeService = event.getLocaleService();
        locales.generate();
    }
}
