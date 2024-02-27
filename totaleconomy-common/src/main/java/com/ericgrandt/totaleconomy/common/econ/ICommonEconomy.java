package com.ericgrandt.totaleconomy.common.econ;

import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import net.kyori.adventure.text.Component;

public interface ICommonEconomy {
    double getBalance(CommonPlayer player);

    Component formatBalance(double balance);
}
