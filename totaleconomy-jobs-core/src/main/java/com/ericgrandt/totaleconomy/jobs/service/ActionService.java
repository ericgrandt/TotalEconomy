package com.ericgrandt.totaleconomy.jobs.service;

import com.ericgrandt.totaleconomy.jobs.dto.HandleActionDto;

import java.util.UUID;

public interface ActionService {
    HandleActionDto handleAction(UUID playerId, String jobName, String blockName);
}
