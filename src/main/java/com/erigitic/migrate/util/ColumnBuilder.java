package com.erigitic.migrate.util;

public abstract class ColumnBuilder {

    public static IntColumnBuilder integer(String name) {
        return new IntColumnBuilder(name);
    }

    public static StringColumnBuilder string(String name) {
        return new StringColumnBuilder(name);
    }

    private String name;
    private boolean isNotNull = false;
    private String defaultValue = null;

    private ColumnBuilder(String name) {
        this.name = name;
    }

    public ColumnBuilder notNull() {
        isNotNull = true;
        return this;
    }

    public ColumnBuilder defaultValue(String defaultExpression) {
        this.defaultValue = defaultExpression;
        return this;
    }

    public String build() {
        StringBuilder text = new StringBuilder();
        build(text);
        return text.toString();
    }

    protected String getName() {
        return name;
    }

    protected void build(StringBuilder builder) {
        if (isNotNull) {
            builder.append(" NOT NULL");
        }

        if (defaultValue != null) {
            builder.append(" DEFAULT ").append(defaultValue);
        }
    }

    public static class StringColumnBuilder extends ColumnBuilder {

        private int length = 60;

        private StringColumnBuilder(String name) {
            super(name);
        }

        public StringColumnBuilder length(int length) {
            this.length = length;
            return this;
        }

        @Override
        protected void build(StringBuilder builder) {
            builder.append(getName())
                    .append(" VARCHAR2(").append(length).append(")");

            super.build(builder);
        }
    }

    public static class IntColumnBuilder extends ColumnBuilder {

        private int size = 32;
        private boolean isUnsigned = false;

        private IntColumnBuilder(String name) {
            super(name);
        }

        public IntColumnBuilder unsigned() {
            this.isUnsigned = true;
            return this;
        }

        public IntColumnBuilder size(int size) {
            this.size = size;
            return this;
        }

        @Override
        protected void build(StringBuilder builder) {
            builder.append(getName())
                    .append(" INT(").append(size).append(")");

            if (isUnsigned) {
                builder.append(" UNSIGNED");
            }

            super.build(builder);
        }
    }
}
