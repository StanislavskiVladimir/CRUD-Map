package com.digdes.school.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Insert implements Command{

    public List<Map<String, Object>> start(List<Map<String, Object>> data, List<String> line) {
        List<String> values = line.subList(2, line.size());
        Map<String, Object> row = createRow(values);
        data.add(row);
        return data;
    }

    protected Map<String, Object> createRow(List<String> values) {
        Map<String, Object> row = new HashMap<>();
        for (int i = 0; i < values.size(); i += 3) {
            switch (values.get(i).toUpperCase()) {
                case "'ID'" -> row.put("'id'", Long.valueOf(values.get(i + 2)));
                case "'LASTNAME'" -> row.put("'lastName'", String.valueOf(values.get(i + 2)));
                case "'AGE'" -> row.put("'age'", Long.valueOf(values.get(i + 2)));
                case "'COST'" -> row.put("'cost'", Double.valueOf(values.get(i + 2)));
                case "'ACTIVE'" -> row.put("'active'", Boolean.valueOf(values.get(i + 2)));
                default -> throw new RuntimeException();
            }
        }
        row.putIfAbsent("'id'", null);
        row.putIfAbsent("'lastName'", null);
        row.putIfAbsent("'age'", null);
        row.putIfAbsent("'cost'", null);
        row.putIfAbsent("'active'", null);
        return row;
    }
}
