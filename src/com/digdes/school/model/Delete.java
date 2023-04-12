package com.digdes.school.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Delete extends AbstractWhere {
    @Override
    protected void create(int positionWhere, List<String> line) {
        if (positionWhere != -1) {
            where = line.subList(positionWhere + 1, line.size());
        } else {
            where.clear();
        }
    }

    public void editWithoutConditions(List<Map<String, Object>> data) {
        for (int i = 0; i < data.size(); i++) {
            data.remove(i);
            i--;
        }
    }

    public void editWithOneConditions(List<Map<String, Object>> data, int i) {
        data.remove(i);
    }

    public int editWithMoreConditions(List<Map<String, Object>> data, ArrayList<String> operation, int i){
        if (Boolean.parseBoolean(operation.get(0))) {
            data.remove(i);
            i--;
        }
        return  i;
    }

}
