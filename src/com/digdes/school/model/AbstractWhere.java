package com.digdes.school.model;

import com.digdes.school.exception.UnknownCommandException;
import com.digdes.school.exception.—olumnCommanException;
import com.digdes.school.exception.—omparisonCharacterCommandException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractWhere implements Command {
    List<String> where = new ArrayList<>();

    public List<Map<String, Object>> start(List<Map<String, Object>> data, List<String> line) {
        createWhere(line);
        // ÓÏ‡Ì‰‡ ·ÂÁ ÛÒÎÓ‚ËÈ
        if (where.isEmpty()) {
            editWithoutConditions(data);
        }
        // ÓÏ‡Ì‰‡ Ò Ó‰ÌËÏ ÛÒÎÓ‚ËÂÏ
        else if (where.size() == 3) {
            for (int i = data.size() - 1; i >= 0; i--) {
                ArrayList<String> operation = new ArrayList<>(where);
                compareValues(data.get(i), operation);
                if (Boolean.parseBoolean(operation.get(1))) {
                    editWithOneConditions(data, i);
                }
            }

            // ÓÏ‡Ì‰‡ Ò AND Ë OR
        } else {
            for (int i = 0; i < data.size(); i++) {
                ArrayList<String> operation = new ArrayList<>(where);
                for (int k = 1; k < operation.size(); k += 2) {
                    compareValues(data.get(i), operation.subList(k - 1, k + 2));
                    deleteNearestElementsOperation(operation, k);
                }
                //‚˚ÔÓÎÌÂÌËÂ AND
                for (int k = 1; k < operation.size(); k += 2) {
                    if ((operation.get(k).equalsIgnoreCase("AND"))) {
                        operation.set(k, String.valueOf((Boolean.parseBoolean(operation.get(k - 1)) & Boolean.parseBoolean(operation.get(k + 1)))));
                        deleteNearestElementsOperation(operation, k);
                        k -= 2;
                    }
                }
                //‚˚ÔÓÎÌÂÌËÂ OR
                for (int k = 1; k < operation.size(); k += 2) {
                    if ((operation.get(k).equalsIgnoreCase("OR"))) {
                        operation.set(k, Boolean.parseBoolean((operation.get(k - 1))) || Boolean.parseBoolean(operation.get(k + 1)) ? "true" : "false");
                        deleteNearestElementsOperation(operation, k);
                        k -= 2;
                    }
                }
                i = editWithMoreConditions(data, operation, i);
            }
        }
        return data;
    }

    protected int searchPositionWhere(List<String> line) {
        int positionWhere = -1;
        for (int i = 0; i < line.size(); i++) {
            if (line.get(i).equalsIgnoreCase("WHERE")) {
                positionWhere = i;
            }
        }
        return positionWhere;
    }

    protected void createWhere(List<String> line) {
        int positionWhere = searchPositionWhere(line);
        create(positionWhere, line);

    }

    protected void deleteNearestElementsOperation(ArrayList<String> operation, int k) {
        operation.remove(k + 1);
        operation.remove(k - 1);
    }

    protected Object castType(List<String> where) {
        switch (where.get(0).toUpperCase()) {
            case "'ID'", "'AGE'" -> {
                try {
                    return Long.valueOf(where.get(2));
                } catch (Exception ex) {
                    try {
                        return Double.valueOf(where.get(2));
                    } catch (NumberFormatException e) {
                        throw new —omparisonCharacterCommandException();
                    }
                }
            }
            case "'LASTNAME'" -> {
                return String.valueOf(where.get(2));
            }
            case "'COST'" -> {
                try {
                    return Double.valueOf(where.get(2));
                } catch (NumberFormatException e) {
                    throw new —omparisonCharacterCommandException();
                }

            }
            case "'ACTIVE'" -> {
                try {
                    return Boolean.valueOf(where.get(2));
                }
                catch (NumberFormatException e) {
                    throw new —omparisonCharacterCommandException();
                }
            }
            default -> throw new —olumnCommanException();
        }
    }


    protected void compareValues(Map<String, Object> map, List<String> where) {
        switch (String.valueOf(where.get(1))) {
            case "=" -> {
                if (map.get(where.get(0)) == null) {
                    where.set(1, "false");
                } else if (map.get(where.get(0)) instanceof Boolean || map.get(where.get(0)) instanceof String
                        || map.get(where.get(0)) instanceof Long || map.get(where.get(0)) instanceof Double) {
                    where.set(1, String.valueOf(map.get(where.get(0)).equals(castType(where))));
                }
            }
            case "!=" -> {
                if (map.get(where.get(0)) == null) {
                    where.set(1, "true");
                } else if (map.get(where.get(0)) instanceof Boolean || map.get(where.get(0)) instanceof String
                        || map.get(where.get(0)) instanceof Long || map.get(where.get(0)) instanceof Double) {
                    where.set(1, String.valueOf(!(map.get(where.get(0)).equals(castType(where)))));
                }
            }
            case "like" -> {
                try {
                    String reg = (String) castType(where);
                    if (reg.charAt(0) != '\'' | reg.charAt(reg.length() - 1) != '\'') {
                        throw new —omparisonCharacterCommandException();
                    } else if (map.get(where.get(0)) == null) {
                        where.set(1, "false");
                    } else if (reg.charAt(1) == '%' && reg.charAt(reg.length() - 2) == '%') {
                        reg = reg.replaceAll("[%']*", "");
                        where.set(1, String.valueOf(map.get(where.get(0)).toString().contains(reg)));
                    } else if (reg.charAt(1) == '%') {
                        reg = reg.replaceAll("[%']*", "");
                        String s = map.get(where.get(0)).toString().replaceAll("[']*", "");
                        where.set(1, String.valueOf(s.endsWith(reg)));
                    } else if (reg.charAt(reg.length() - 2) == '%') {
                        reg = reg.replaceAll("[%']*", "");
                        String s = map.get(where.get(0)).toString().replaceAll("[']*", "");
                        where.set(1, String.valueOf(s.startsWith(reg)));
                    } else {
                        reg = reg.replaceAll("'*", "");
                        String s = map.get(where.get(0)).toString().replaceAll("[']*", "");
                        where.set(1, String.valueOf(s.equals(reg)));
                    }
                } catch (Exception ec) {
                    throw new —omparisonCharacterCommandException();
                }

            }
            case "ilike" -> {
                try {
                    String reg = (String) castType(where);
                    if (reg.charAt(0) != '\'' | reg.charAt(reg.length() - 1) != '\'') {
                        throw new —omparisonCharacterCommandException();
                    } else if (map.get(where.get(0)) == null) {
                        where.set(1, "false");
                    } else if (reg.charAt(1) == '%' && reg.charAt(reg.length() - 2) == '%') {
                        reg = reg.replaceAll("[%']*", "");
                        where.set(1, String.valueOf(map.get(where.get(0)).toString().toLowerCase().contains(reg.toLowerCase())));
                    } else if (reg.charAt(1) == '%') {
                        reg = reg.replaceAll("[%']*", "");
                        String s = map.get(where.get(0)).toString().replaceAll("[']*", "");
                        where.set(1, String.valueOf(s.toLowerCase().endsWith(reg.toLowerCase())));
                    } else if (reg.charAt(reg.length() - 2) == '%') {
                        reg = reg.replaceAll("[%']*", "");
                        String s = map.get(where.get(0)).toString().replaceAll("[']*", "");
                        where.set(1, String.valueOf(s.toLowerCase().startsWith(reg.toLowerCase())));
                    } else {
                        reg = reg.replaceAll("'*", "");
                        String s = map.get(where.get(0)).toString().replaceAll("[']*", "");
                        where.set(1, String.valueOf(s.equalsIgnoreCase(reg)));
                    }
                } catch (Exception ec) {
                    throw new —omparisonCharacterCommandException();
                }
            }
            case ">=" -> {
                castType(where);
                if (map.get(where.get(0)) == null) {
                    where.set(1, "false");
                } else if (map.get(where.get(0)) instanceof Long) {
                    if (castType(where) instanceof Long) {
                        where.set(1, String.valueOf((Long) map.get(where.get(0)) >= (Long) castType(where)));
                    } else if (castType(where) instanceof Double) {
                        where.set(1, String.valueOf((Long) map.get(where.get(0)) >= (Double) castType(where)));
                    }
                } else if (map.get(where.get(0)) instanceof Double) {
                    if (castType(where) instanceof Long) {
                        where.set(1, String.valueOf((Double) map.get(where.get(0)) >= (Long) castType(where)));
                    } else if (castType(where) instanceof Double) {
                        where.set(1, String.valueOf((Double) map.get(where.get(0)) >= (Double) castType(where)));
                    }
                } else {
                    throw new —omparisonCharacterCommandException();
                }
            }
            case "<=" -> {
                castType(where);
                if (map.get(where.get(0)) == null) {
                    where.set(1, "false");
                } else if (map.get(where.get(0)) instanceof Long) {
                    if (castType(where) instanceof Long) {
                        where.set(1, String.valueOf((Long) map.get(where.get(0)) <= (Long) castType(where)));
                    } else if (castType(where) instanceof Double) {
                        where.set(1, String.valueOf((Long) map.get(where.get(0)) <= (Double) castType(where)));
                    }
                } else if (map.get(where.get(0)) instanceof Double) {
                    if (castType(where) instanceof Long) {
                        where.set(1, String.valueOf((Double) map.get(where.get(0)) <= (Long) castType(where)));
                    } else if (castType(where) instanceof Double) {
                        where.set(1, String.valueOf((Double) map.get(where.get(0)) <= (Double) castType(where)));
                    }
                } else {
                    throw new —omparisonCharacterCommandException();
                }
            }
            case "<" -> {
                castType(where);
                if (map.get(where.get(0)) == null) {
                    where.set(1, "false");
                } else if (map.get(where.get(0)) instanceof Long) {
                    if (castType(where) instanceof Long) {
                        where.set(1, String.valueOf((Long) map.get(where.get(0)) < (Long) castType(where)));
                    } else if (castType(where) instanceof Double) {
                        where.set(1, String.valueOf((Long) map.get(where.get(0)) < (Double) castType(where)));
                    }
                } else if (map.get(where.get(0)) instanceof Double) {
                    if (castType(where) instanceof Long) {
                        where.set(1, String.valueOf((Double) map.get(where.get(0)) < (Long) castType(where)));
                    } else if (castType(where) instanceof Double) {
                        where.set(1, String.valueOf((Double) map.get(where.get(0)) < (Double) castType(where)));
                    }
                } else {
                    throw new —omparisonCharacterCommandException();
                }
            }
            case ">" -> {
                castType(where);
                if (map.get(where.get(0)) == null) {
                    where.set(1, "false");
                } else if (map.get(where.get(0)) instanceof Long) {
                    if (castType(where) instanceof Long) {
                        where.set(1, String.valueOf((Long) map.get(where.get(0)) > (Long) castType(where)));
                    } else if (castType(where) instanceof Double) {
                        where.set(1, String.valueOf((Long) map.get(where.get(0)) > (Double) castType(where)));
                    }
                } else if (map.get(where.get(0)) instanceof Double) {
                    if (castType(where) instanceof Long) {
                        where.set(1, String.valueOf((Double) map.get(where.get(0)) > (Long) castType(where)));
                    } else if (castType(where) instanceof Double) {
                        where.set(1, String.valueOf((Double) map.get(where.get(0)) > (Double) castType(where)));
                    }
                } else {
                    throw new —omparisonCharacterCommandException();
                }
            }
            default -> throw new —omparisonCharacterCommandException();
        }
    }


    protected abstract void create(int positionWhere, List<String> line);

    protected abstract void editWithoutConditions(List<Map<String, Object>> data);

    protected abstract void editWithOneConditions(List<Map<String, Object>> data, int i);

    protected abstract int editWithMoreConditions(List<Map<String, Object>> data, ArrayList<String> operation, int i);
}
