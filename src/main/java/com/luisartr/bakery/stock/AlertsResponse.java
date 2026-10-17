package com.luisartr.bakery.stock;

import java.util.List;

public record AlertsResponse(List<BalanceResponse> belowMinimum, List<BatchResponse> expiringSoon) {}