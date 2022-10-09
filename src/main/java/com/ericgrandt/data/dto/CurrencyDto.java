package com.ericgrandt.data.dto;

public class CurrencyDto {
    private final int id;
    private final String nameSingular;
    private final String namePlural;
    private final String symbol;
    private final int numFractionDigits;
    private final boolean isDefault;

    public CurrencyDto(int id, String nameSingular, String namePlural, String symbol, int numFractionDigits, boolean isDefault) {
        this.id = id;
        this.nameSingular = nameSingular;
        this.namePlural = namePlural;
        this.symbol = symbol;
        this.numFractionDigits = numFractionDigits;
        this.isDefault = isDefault;
    }

    public int getId() {
        return id;
    }

    public String getNameSingular() {
        return nameSingular;
    }

    public String getNamePlural() {
        return namePlural;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getNumFractionDigits() {
        return numFractionDigits;
    }

    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CurrencyDto that = (CurrencyDto) o;

        if (id != that.id) {
            return false;
        }

        if (numFractionDigits != that.numFractionDigits) {
            return false;
        }

        if (isDefault != that.isDefault) {
            return false;
        }

        if (!nameSingular.equals(that.nameSingular)) {
            return false;
        }

        if (!namePlural.equals(that.namePlural)) {
            return false;
        }

        return symbol.equals(that.symbol);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + nameSingular.hashCode();
        result = 31 * result + namePlural.hashCode();
        result = 31 * result + symbol.hashCode();
        result = 31 * result + numFractionDigits;
        result = 31 * result + (isDefault ? 1 : 0);
        return result;
    }
}
