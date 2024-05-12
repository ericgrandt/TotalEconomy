package com.ericgrandt.totaleconomy.common.listeners;

import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.common.services.JobService;

public class CommonJobListener {
    private final CommonEconomy economy;
    private final JobService jobService;
    private final int currencyId;

    public CommonJobListener(
        final CommonEconomy economy,
        final JobService jobService,
        final int currencyId
    ) {
        this.economy = economy;
        this.jobService = jobService;
        this.currencyId = currencyId;
    }

    public void handleAction() {

    }
}
