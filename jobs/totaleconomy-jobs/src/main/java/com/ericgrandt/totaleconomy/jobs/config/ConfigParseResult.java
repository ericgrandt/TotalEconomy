package com.ericgrandt.totaleconomy.jobs.config;

import java.util.List;

public record ConfigParseResult<T>(T result, List<String> errors) {
}
