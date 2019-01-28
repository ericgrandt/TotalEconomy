package com.erigitic.migrate.util;

import com.erigitic.except.TERuntimeException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public abstract class ConstraintBuilder {

    public static PrimaryKeyBuilder primary() {
        return new PrimaryKeyBuilder();
    }

    public static ForeignKeyBuilder foreignKey() {
        return new ForeignKeyBuilder();
    }

    public static UniqueKeyBuilder unique() {
        return new UniqueKeyBuilder();
    }

    public static IndexBuilder index() {
        return new IndexBuilder();
    }

    protected String name = null;

    public abstract String build();

    public static class PrimaryKeyBuilder extends ConstraintBuilder {

        private List<String> localColumns = new LinkedList<>();

        public PrimaryKeyBuilder name(String name) {
            this.name = name;
            return this;
        }

        public PrimaryKeyBuilder column(String columnName) {
            this.localColumns.add(columnName);
            return this;
        }

        @Override
        public String build() {
            if (localColumns.isEmpty()) {
                throw new TERuntimeException("Column name may not be null for a primary key!");
            }

            if (name == null) {
                name = "pk_" + localColumns.hashCode();
            }

            return String.format("PRIMARY KEY %s(%s)", name, String.join(",", localColumns));
        }
    }

    public static class ForeignKeyBuilder extends ConstraintBuilder {

        private String localColumn = null;
        private String foreignTable = null;
        private String foreignColumn = null;

        public ForeignKeyBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ForeignKeyBuilder column(String localColumn) {
            this.localColumn = localColumn;
            return this;
        }

        public ForeignKeyBuilder references(String foreignColumn) {
            this.foreignColumn = foreignColumn;
            return this;
        }

        public ForeignKeyBuilder on(String foreignTable) {
            this.foreignTable = foreignTable;
            return this;
        }

        @Override
        public String build() {
            Objects.requireNonNull(localColumn, "Local localColumn may not be null for a fk!");
            Objects.requireNonNull(foreignTable, "Foreign table may not be null for a fk!");
            Objects.requireNonNull(foreignColumn, "Foreign localColumn may not be null for a fk!");

            if (name == null) {
                name = "fk_" + localColumn + "_" + foreignColumn;
            }

            return String.format("FOREIGN KEY %s (%s) REFERENCES %s(%s) ON UPDATE CASCADE ON DELETE CASCADE", name, localColumn, foreignTable, foreignColumn);
        }
    }

    public static class UniqueKeyBuilder extends ConstraintBuilder {

        private List<String> localColumns = new LinkedList<>();

        public UniqueKeyBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UniqueKeyBuilder column(String columnName) {
            Objects.requireNonNull(columnName, "Column name may not be null for a unique constraint!");
            localColumns.add(columnName);
            return this;
        }

        @Override
        public String build() {
            if (localColumns.isEmpty()) {
                throw new TERuntimeException("Column names for a unique constraint may not be empty!");
            }

            if (name == null) {
                name = "unique_" + localColumns.hashCode();
            }

            return String.format("UNIQUE %s (%s)", name, String.join(",", localColumns));
        }

        public UniqueKeyBuilder columns(String... columns) {
            for (String column : columns) {
                column(column);
            }
            return this;
        }
    }

    public static class IndexBuilder extends ConstraintBuilder {

        private String localColumn = null;

        public IndexBuilder name(String name) {
            this.name = name;
            return this;
        }

        public IndexBuilder column(String columnName) {
            this.localColumn = columnName;
            return this;
        }

        @Override
        public String build() {
            Objects.requireNonNull(localColumn,"Column name may not be null for an index constraint!");

            if (name == null) {
                name = "index_" + localColumn;
            }

            return String.format("INDEX %s (%s)", name, localColumn);
        }
    }
}
