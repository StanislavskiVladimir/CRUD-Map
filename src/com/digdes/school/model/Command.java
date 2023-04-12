package com.digdes.school.model;

import java.util.List;
import java.util.Map;

public interface Command {
    public List<Map<String, Object>> start(List<Map<String, Object>> data, List<String> line);
}
