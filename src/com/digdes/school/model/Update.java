package com.digdes.school.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Update extends AbstractWhere{
    List<String> values;
    @Override
    protected void create(int positionWhere, List<String> line) {
        if (positionWhere != -1) {
            values = line.subList(2, positionWhere);
            where = line.subList(positionWhere + 1, line.size());
        } else {
            values = line.subList(2, line.size());
            where.clear();
        }
    }

    public void editWithoutConditions(List<Map<String, Object>> data){
        for (int i = 0; i < data.size(); i++) {
            for (int k = 0; k < values.size(); k += 3) {
                data.get(i).put(values.get(k), values.get(k + 2));
            }
        }
    }
    public void editWithOneConditions(List<Map<String, Object>> data, int i){
        for (int k = 0; k < values.size(); k += 3) {
            data.get(i).put(values.get(k), values.get(k + 2));
        }
    }
    public int editWithMoreConditions(List<Map<String, Object>> data, ArrayList<String> operation, int i) {
        if (Boolean.parseBoolean(operation.get(0))) {
            for (int k = 0; k < values.size(); k += 3) {
                if (values.get(k + 2).equalsIgnoreCase("null")){
                    data.get(i).put(values.get(k), null);
                } else {
                    data.get(i).put(values.get(k), values.get(k + 2));
                }
            }
            //удаление записи если все значения null
            int j = 0;
            for(Iterator<Map.Entry<String, Object>> it = data.get(i).entrySet().iterator(); it.hasNext();){
                Map.Entry<String, Object> entry = it.next();
                if((entry.getValue() == null)){
                    j++;
                }
                if(j == data.get(i).size()){
                    data.remove(i);
                }
            }
        }
    return i;
    }

}
