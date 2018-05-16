/*
 * This file is part of Total Economy, licensed under the MIT License (MIT).
 *
 * Copyright (c) Eric Grandt <https://www.ericgrandt.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.erigitic.config;

import java.math.BigDecimal;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

public class TECurrency implements Currency {

    private Text singular;
    private Text plural;
    private Text symbol;
    private int numFractionDigits;
    private boolean isDefault;
    private boolean prefixSymbol;
    boolean isTransferable;
    private BigDecimal startBalance;

    public TECurrency(Text singular, Text plural, Text symbol, int numFractionDigits, boolean defaultCurrency, boolean prefixSymbol, boolean isTransferable, BigDecimal startBalance) {
        this.singular = singular;
        this.plural = plural;
        this.symbol = symbol;
        this.numFractionDigits = numFractionDigits;
        this.isDefault = defaultCurrency;
        this.prefixSymbol = prefixSymbol;
        this.isTransferable = isTransferable;
        this.startBalance = startBalance;
    }

    @Override
    public String getName() {
        return singular.toPlain();
    }

    @Override
    public String getId() {
        return "totaleconomy:" + singular.toPlain().toLowerCase();
    }

    @Override
    public Text getDisplayName() {
        return singular;
    }

    @Override
    public Text getPluralDisplayName() {
        return plural;
    }

    @Override
    public Text getSymbol() {
        return symbol;
    }

    @Override
    public Text format(BigDecimal amount, int numFractionDigits) {
        if (prefixSymbol) {
            return Text.of(symbol, amount.setScale(numFractionDigits, BigDecimal.ROUND_HALF_UP));
        } else {
            return Text.of(amount.setScale(numFractionDigits, BigDecimal.ROUND_HALF_UP), symbol);

        }
    }

    @Override
    public int getDefaultFractionDigits() {
        return numFractionDigits;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    public boolean isTransferable() {
        return isTransferable;
    }

    public BigDecimal getStartingBalance() {
        return startBalance;
    }
}
