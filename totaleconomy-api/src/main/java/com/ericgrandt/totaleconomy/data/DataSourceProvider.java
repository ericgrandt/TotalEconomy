package com.ericgrandt.totaleconomy.data;

import javax.sql.DataSource;

public interface DataSourceProvider {
    // TODO: Add javadocs
    DataSource getDataSource();
}
