package com.ericgrandt.config;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.api.event.Listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import sawfowl.localeapi.api.ConfigTypes;
import sawfowl.localeapi.event.LocaleServiseEvent;
import sawfowl.localeapi.utils.AbstractLocaleUtil;

public class Locales {

	private LocaleService localeService;
	private boolean json;

	public Locales() {}
	public Locales(LocaleService localeService) {
		this.localeService = localeService;
		localeService.locales = this;
	}

	void generate() {
		localeService.get().createPluginLocale("totaleconomy", ConfigTypes.HOCON, org.spongepowered.api.util.locale.Locales.DEFAULT);
		localeService.get().createPluginLocale("totaleconomy", ConfigTypes.HOCON, org.spongepowered.api.util.locale.Locales.RU_RU);
		generateDefault();
		generateRu();
	}

	public Map<String, String> createReplaceMap(List<String> keys, List<Object> values) {
		Map<String, String> map = new HashMap<String, String>();
		int i = 0;
		for(String key : keys) {
			if(i >= keys.size() || i >= values.size()) break;
			map.put(key, values.get(i).toString());
			i++;
		}
		return map;
	}

	public Component getText(String defaultText, Locale locale, Object[] path) {
		if(localeService == null) return toText(defaultText);
		return getAbstractLocaleUtil(locale).getComponent(json, path);
	}

	public Component getTextWhithReplacers(String defaultText, Locale locale, Map<String, String> replaceMap, Object[] path) {
		if(localeService == null) return replace(toText(defaultText), replaceMap);
		return replace(getAbstractLocaleUtil(locale).getComponent(json, path), replaceMap);
	}

	private void generateDefault() {
		Locale locale = org.spongepowered.api.util.locale.Locales.DEFAULT;
		boolean check = check(locale, toText("&aOnly players can use this command."), null, LocalePaths.ONLY_PLAYER);
		check = check(locale, toText("That currency does not exist."), null, LocalePaths.CURRENCY_NOT_EXIST) || check;
		check = check(locale, toText("Player argument is missing."), null, LocalePaths.PLAYER_MISSING) || check;
		check = check(locale, toText("&aBalance&f: &6" + Placeholders.CURRENCY + Placeholders.BALANCE + "&a."), null, LocalePaths.BALANCE) || check;
		check = check(locale, toText("&aYour balance in the currency &6" + Placeholders.CURRENCY + " &ais increased by &6" + Placeholders.CURRENCY + Placeholders.VALUE + "&a. Current balance&f: &6" + Placeholders.CURRENCY + Placeholders.BALANCE + "&a."), null, LocalePaths.BALANCE_ADD) || check;
		check = check(locale, toText("&aYour balance in the currency &6" + Placeholders.CURRENCY + " &ais reduced by &6" + Placeholders.CURRENCY + Placeholders.VALUE + "&a. Current balance&f: &6" + Placeholders.CURRENCY + Placeholders.BALANCE + "&a."), null, LocalePaths.BALANCE_REMOVE) || check;
		check = check(locale, toText("&aYour balance in the currency &6" + Placeholders.CURRENCY + " &ais set to &6" + Placeholders.CURRENCY + Placeholders.BALANCE + "&a."), null, LocalePaths.BALANCE_SET) || check;
		check = check(locale, toText("Account not found. Writing temporary data."), null, LocalePaths.WRITE_TEMP_DATA) || check;
		check = check(locale, toText("Failed to run command: invalid account(s)."), null, LocalePaths.INVALID_ACCOUNT) || check;
		check = check(locale, toText("&aThe balance of the player &6" + Placeholders.PLAYER + " &aincreased by &6" + Placeholders.CURRENCY + Placeholders.VALUE + "&a. Current balance&f: &6" + Placeholders.CURRENCY + Placeholders.BALANCE + "&a."), null, LocalePaths.BALANCE_ADD_ADMIN) || check;
		check = check(locale, toText("&aThe balance of the player &6" + Placeholders.PLAYER + " &areduced by &6" + Placeholders.CURRENCY + Placeholders.VALUE + "&a. Current balance&f: &6" + Placeholders.CURRENCY + Placeholders.BALANCE + "&a."), null, LocalePaths.BALANCE_REMOVE_ADMIN) || check;
		check = check(locale, toText("&aThe balance of the player &6" + Placeholders.PLAYER + " &aset to &6" + Placeholders.CURRENCY + Placeholders.BALANCE + "&a."), null, LocalePaths.BALANCE_SET_ADMIN) || check;
		check = check(locale, toText("&aYou have sent &6" + Placeholders.CURRENCY + Placeholders.VALUE + " &ato &6" + Placeholders.PLAYER + "&a."), null, LocalePaths.PAY_SENDER) || check;
		check = check(locale, toText("&aYou have received &6" + Placeholders.CURRENCY + Placeholders.VALUE + " &afrom  &6" + Placeholders.PLAYER + "&a."), null, LocalePaths.PAY_RECIPIENT) || check;
		check = check(locale, toText("Failed to run command: unable to set balances."), null, LocalePaths.PAY_FAIL) || check;
		check = check(locale, toText("Amount argument is missing."), null, LocalePaths.PAY_MISSING_AMOUNT) || check;
		check = check(locale, toText("Amount must be greater than 0."), null, LocalePaths.PAY_ZERO_OR_BELOW) || check;
		check = check(locale, toText("You cannot pay yourself."), null, LocalePaths.PAY_SELF) || check;
		if(check) save(locale);
	}

	private void generateRu() {
		Locale locale = org.spongepowered.api.util.locale.Locales.RU_RU;
		boolean check = check(locale, toText("&aТолько игроки могут использовать эту команду."), null, LocalePaths.ONLY_PLAYER);
		check = check(locale, toText("Не удалось найти указанную валюту."), null, LocalePaths.CURRENCY_NOT_EXIST) || check;
		check = check(locale, toText("Нужно указать ник игрока."), null, LocalePaths.PLAYER_MISSING) || check;
		check = check(locale, toText("&aБаланс&f: &6" + Placeholders.CURRENCY + Placeholders.BALANCE + "&a."), null, LocalePaths.BALANCE) || check;
		check = check(locale, toText("&aВаш баланс в валюте &6" + Placeholders.CURRENCY + " &aувеличен на &6" + Placeholders.CURRENCY + Placeholders.VALUE + "&a. Текущий баланс&f: &6" + Placeholders.CURRENCY + Placeholders.BALANCE + "&a."), null, LocalePaths.BALANCE_ADD) || check;
		check = check(locale, toText("&aВаш баланс в валюте &6" + Placeholders.CURRENCY + " &aуменьшен на &6" + Placeholders.CURRENCY + Placeholders.VALUE + "&a. Текущий баланс&f: &6" + Placeholders.CURRENCY + Placeholders.BALANCE + "&a."), null, LocalePaths.BALANCE_REMOVE) || check;
		check = check(locale, toText("&aВаш баланс в валюте &6" + Placeholders.CURRENCY + " &aтеперь равен &6" + Placeholders.CURRENCY + Placeholders.BALANCE + "&a."), null, LocalePaths.BALANCE_SET) || check;
		check = check(locale, toText("Аккаунт не найден. Запись временных данных."), null, LocalePaths.WRITE_TEMP_DATA) || check;
		check = check(locale, toText("Не удалось выполнить команду: недействительный счет(а)."), null, LocalePaths.INVALID_ACCOUNT) || check;
		check = check(locale, toText("&aБаланс игрока &6" + Placeholders.PLAYER + " &aувеличен на &6" + Placeholders.CURRENCY + Placeholders.VALUE + "&a. Текущий баланс&f: &6" + Placeholders.CURRENCY + Placeholders.BALANCE + "&a."), null, LocalePaths.BALANCE_ADD_ADMIN) || check;
		check = check(locale, toText("&aБаланс игрока &6" + Placeholders.PLAYER + " &aуменьшен на &6" + Placeholders.CURRENCY + Placeholders.VALUE + "&a. Текущий баланс&f: &6" + Placeholders.CURRENCY + Placeholders.BALANCE + "&a."), null, LocalePaths.BALANCE_REMOVE_ADMIN) || check;
		check = check(locale, toText("&aБаланс игрока &6" + Placeholders.PLAYER + " &aтеперь равен &6" + Placeholders.CURRENCY + Placeholders.BALANCE + "&a."), null, LocalePaths.BALANCE_SET_ADMIN) || check;
		check = check(locale, toText("&aВы передали &6" + Placeholders.CURRENCY + Placeholders.VALUE + " &aигроку &6" + Placeholders.PLAYER + "&a."), null, LocalePaths.PAY_SENDER) || check;
		check = check(locale, toText("&aВы получили &6" + Placeholders.CURRENCY + Placeholders.VALUE + " &aот игрока  &6" + Placeholders.PLAYER + "&a."), null, LocalePaths.PAY_RECIPIENT) || check;
		check = check(locale, toText("Не удалось выполнить команду: невозможно установить баланс."), null, LocalePaths.PAY_FAIL) || check;
		check = check(locale, toText("Вы не указали сумму."), null, LocalePaths.PAY_MISSING_AMOUNT) || check;
		check = check(locale, toText("Сумма должна быть больше 0."), null, LocalePaths.PAY_ZERO_OR_BELOW) || check;
		check = check(locale, toText("Нельзя заплатить самому себе."), null, LocalePaths.PAY_SELF) || check;
		if(check) save(locale);
	}

	private Component replace(Component component, Map<String, String> map) {
		for(Entry<String, String> entry : map.entrySet()) {
			component = component.replaceText(TextReplacementConfig.builder().match(entry.getKey()).replacement(Component.text(entry.getValue())).build());
		}
		return component;
	}

	private AbstractLocaleUtil getAbstractLocaleUtil(Locale locale) {
		return localeService.get().getPluginLocales("totaleconomy").containsKey(locale) ? localeService.get().getPluginLocales("totaleconomy").get(locale) : localeService.get().getPluginLocales("totaleconomy").get(org.spongepowered.api.util.locale.Locales.DEFAULT);
	}

	private Component toText(String string) {
		return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
	}

	private boolean check(Locale locale, Component value, String comment, Object... path) {
		return getAbstractLocaleUtil(locale).checkComponent(json, value, comment, path);
	}

	private void save(Locale locale) {
		getAbstractLocaleUtil(locale).saveLocaleNode();
	}

	public class Placeholders {
		
		public static final String PLAYER = "%player%";
		
		public static final String BALANCE = "%balance%";
		
		public static final String CURRENCY = "%currency%";
		
		public static final String VALUE = "%value%";
		
	}

}
