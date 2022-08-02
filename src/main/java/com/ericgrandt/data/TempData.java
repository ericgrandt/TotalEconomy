package com.ericgrandt.data;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.math.BigDecimal;
import java.util.Optional;

@ConfigSerializable
public class TempData {

    public TempData(){}

    public TempData(Currency currency, BigDecimal amount) {
        this.currency = toPlain(currency.displayName());
        update(amount);
    }
    @Setting("Currency")
    private String currency;
    @Setting("Amount")
    private double amount;
    @Setting
    private long expire;

    public Currency getCurrency() {
        Optional<Currency> optionalCurrency = Sponge.game().registry(RegistryTypes.CURRENCY).stream().filter(currency1 -> (toPlain(currency1.displayName()).equals(currency) || toPlain(currency1.displayName()).contains(currency))).findFirst();
        return optionalCurrency.isPresent() ? optionalCurrency.get() : Sponge.game().registry(RegistryTypes.CURRENCY).stream().filter(currency1 -> (currency1.isDefault())).findFirst().get();
    }

    public BigDecimal getAmount() {
        return BigDecimal.valueOf(amount);
    }

    public boolean isExpire() {
        return expire <= System.currentTimeMillis();
    }

    public void update(BigDecimal amount) {
        this.amount = amount.doubleValue();
        expire = System.currentTimeMillis() + 432000000l;
    }

    private String toPlain(Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TempData tempData = (TempData) o;

        return currency.equals(tempData.currency);
    }

    @Override
    public int hashCode() {
        return currency.hashCode();
    }
}
