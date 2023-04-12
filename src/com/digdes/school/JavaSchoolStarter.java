package com.digdes.school;

import com.digdes.school.exception.CommandException;
import com.digdes.school.exception.UnknownCommandException;
import com.digdes.school.model.Delete;
import com.digdes.school.model.Insert;
import com.digdes.school.model.Select;
import com.digdes.school.model.Update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaSchoolStarter {
    public static Insert insert = new Insert();
    public static Update update = new Update();
    public static Delete delete = new Delete();
    public static Select select = new Select();
    //Дефолтный конструктор
    List<Map<String, Object>> data = new ArrayList<>();
    public JavaSchoolStarter(){

    }

    public List<Map<String,Object>> execute(String request) throws Exception {
        Pattern p = Pattern.compile("[!><=]{1,2}|like|ilike");
        Matcher m = p.matcher(request);
        StringBuilder sb = new StringBuilder();
        while (m.find())
            m.appendReplacement(sb, "\s" + m.group() + "\s");
        m.appendTail(sb);
        List<String> line = new ArrayList<>(Arrays.asList(sb.toString().split("[\s,]+")));
        switch (line.get(0).toUpperCase()){
            case "INSERT" -> {
                return insert.start(data, line);
            }
            case "UPDATE" -> {
                return update.start(data,line);
            }
            case "DELETE" -> {
                return delete.start(data,line);
            }
            case "SELECT" -> {
                return select.start(data,line);
            }
            default -> throw new UnknownCommandException(line.get(0).toUpperCase());
        }
    }

}
