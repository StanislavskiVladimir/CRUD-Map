package com.digdes.school.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Select extends AbstractWhere{
    List<Map<String, Object>> result;

    public void editWithoutConditions(List<Map<String, Object>> data){
        for (int i = 0; i < data.size(); i++) {
            result.add(data.get(i));
        }
    }
    public void editWithOneConditions(List<Map<String, Object>> data, int i){
        result.add(data.get(i));
    }
    public int editWithMoreConditions(List<Map<String, Object>> data, ArrayList<String> operation, int i){
        if (Boolean.parseBoolean(operation.get(0))) {
            result.add(data.get(i));
        }
        return  i;
    }

    @Override
    public List<Map<String, Object>> start(List<Map<String, Object>> data, List<String> line){
        super.start(data, line);
        return result;
    }


    @Override
    protected void create(int positionWhere, List<String> line) {
        if (positionWhere != -1) {
            where = line.subList(positionWhere + 1, line.size());
        } else {
            where.clear();
        }
        result = new ArrayList<>();
    }
}
