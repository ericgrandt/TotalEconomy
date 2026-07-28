package com.ericgrandt.totaleconomy.api.infra;

import com.ericgrandt.totaleconomy.api.service.EconomyService;

import javax.sql.DataSource;

/**
 * Provides access to the shared database connection pool managed by the core Total Economy plugin. Intended for
 * first-party add-ons that need direct database access for their own tables. Third-party plugins should use
 * {@link EconomyService} instead.
 */
public interface DataSourceProvider {
    DataSource getDataSource();
}
