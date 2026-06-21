package com.ericgrandt.totaleconomy.dto;

import java.util.UUID;

public record GetAccountRequest(UUID playerId, String currencyCode) {
}
