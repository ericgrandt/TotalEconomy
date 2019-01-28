package com.erigitic.migrate.util;

import java.util.LinkedList;
import java.util.List;

public class TableBuilder {

    private List<String> columns = new LinkedList<>();
    private List<String> constraints = new LinkedList<>();

    public TableBuilder column(ColumnBuilder column) {
        return addColumn(column.build());
    }

    TableBuilder addColumn(String columnText) {
        columns.add(columnText);
        return this;
    }

    public TableBuilder constraint(ConstraintBuilder builder) {
        return addConstraint(builder.build());
    }

    TableBuilder addConstraint(String constraintText) {
        constraints.add(constraintText);
        return this;
    }

    public String build(String name) {
        LinkedList<String> createStatementElements = new LinkedList<>();

        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE ").append(name).append("( \n");
        builder.append(String.join(",\n", createStatementElements));
        builder.append(");");
        return builder.toString();
    }
}
