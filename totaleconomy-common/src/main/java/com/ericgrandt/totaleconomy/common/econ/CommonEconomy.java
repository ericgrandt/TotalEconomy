package com.ericgrandt.totaleconomy.common.econ;

import com.ericgrandt.totaleconomy.common.game.CommonPlayer;

public interface CommonEconomy {
    double getBalance(CommonPlayer player);

    String format(double balance);
}
