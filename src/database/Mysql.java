package database;
import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Mysql {
    private static Mysql mysql=null;
    // JDBC 驱动名及数据库 URL
    private final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private final String DB_URL = "jdbc:mysql://184.170.217.107:3306/calendar?useUnicode=true&characterEncoding=utf8";

    // 数据库的用户名与密码，需要根据自己的设置
    private final String USER = "root";
    private final String PASS = "password";
    private Connection conn = null;
    private Statement stmt = null;
    private Mysql() {
        // 注册 JDBC 驱动
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            stmt = conn.createStatement();
        } catch (ClassNotFoundException e) {
            System.out.println("class not found");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //静态工厂方法
    public static Mysql getInstance() {
        if (mysql == null) {
            mysql = new Mysql();
        }
        return mysql;
    }

    public int addSchedule(HashMap<String,String> hashMap){
        String scheduleID = "";
        try{
            String sql;
            String type=hashMap.get("type");
            String priority = hashMap.get("priority");
            String status = hashMap.get("status");
            String isFather = hashMap.get("isFather");
            String FatherID = hashMap.get("FatherID");
            String promptStatus = hashMap.get("promptStatus");
            String minutesAhead = hashMap.get("minutesAhead");
            String showOnStage = hashMap.get("showOnStage");
            String minutesDelta = hashMap.get("minuteDelta");
            String startTime,endTime,content,place,topic,people,startDay,anniversaryType;
            String name,duration,teacher,remark,day,way,number,company,job;
            sql ="INSERT INTO schedule  (type, priority,status,isFather,FatherID,promptStatus," +
                    "minutesAhead,showOnStage,minuteDelta)VALUES " +
                    "('"+type+"','"+priority+"','"+status+"','"+isFather+"','"+FatherID+"','"+promptStatus+"'," +
                    "'"+minutesAhead+"','"+showOnStage+"','"+minutesDelta+"')";
            stmt.execute(sql);
            scheduleID = getMaxID(stmt);
            switch(type){
                case "anniversary":
                    anniversaryType = hashMap.get("anniversaryType");
                    content = hashMap.get("content");
                    startDay = hashMap.get("startDay");
                    sql ="INSERT INTO anniversary  (anniversaryType,content,scheduleID,startDay)VALUES " +
                            "('"+anniversaryType+"','"+content+"','"+scheduleID+"','"+startDay+"')";
                    stmt.execute(sql);
                    break;
                case "course":
                    name = hashMap.get("name");
                    content = hashMap.get("content");
                    startDay = hashMap.get("startDay");
                    duration = hashMap.get("duration");
                    teacher = hashMap.get("teacher");
                    remark = hashMap.get("remark");
                    place = hashMap.get("place");
                    day = hashMap.get("day");
                    startTime = hashMap.get("startTime");
                    endTime = hashMap.get("endTime");
                    sql ="INSERT INTO course (scheduleID,name,content,startDay,duration,teacher," +
                            "remark,place,day,startTime,endTime)VALUES " +
                            "('"+scheduleID+"','"+name+"','"+content+"','"+startDay+"','"+duration+"','"+teacher+"'" +
                            ",'"+remark+"','"+place+"','"+day+"','"+startTime+"','"+endTime+"')";
                    stmt.execute(sql);
                    break;
                case "date":
                    place = hashMap.get("place");
                    people = hashMap.get("people");
                    content = hashMap.get("content");
                    startTime = hashMap.get("startTime");
                    endTime = hashMap.get("endTime");
                    sql ="INSERT INTO date (place,people,scheduleID,content,startTime,endTime)VALUES " +
                            "('"+place+"','"+people+"','"+scheduleID+"','"+content+"','"+startTime+"','"+endTime+"')";
                    stmt.execute(sql);
                    break;
                case "interview":
                    place = hashMap.get("place");
                    company = hashMap.get("company");
                    job = hashMap.get("job");
                    remark = hashMap.get("remark");
                    startTime = hashMap.get("startTime");
                    endTime = hashMap.get("endTime");
                    sql ="INSERT INTO interview (scheduleID,place,company,job,remark,startTime,endTime)VALUES " +
                            "('"+scheduleID+"','"+place+"','"+company+"','"+job+"','"+remark+"','"+startTime+"','"+endTime+"')";
                    stmt.execute(sql);
                    break;
                case "meeting":
                    place = hashMap.get("place");
                    topic = hashMap.get("topic");
                    content = hashMap.get("content");
                    startTime = hashMap.get("startTime");
                    endTime = hashMap.get("endTime");
                    sql ="INSERT INTO meeting (place,topic,scheduleID,content,startTime,endTime)VALUES " +
                            "('"+place+"','"+topic+"','"+scheduleID+"','"+content+"','"+startTime+"','"+endTime+"')";
                    stmt.execute(sql);
                    break;
                case "travel":
                    way = hashMap.get("way");
                    place = hashMap.get("place");
                    number = hashMap.get("number");
                    remark = hashMap.get("remark");
                    startTime = hashMap.get("startTime");
                    endTime = hashMap.get("endTime");
                    sql ="INSERT INTO travel (scheduleID,way,place,number,remark,startTime,endTime)VALUES " +
                            "('"+scheduleID+"','"+way+"','"+place+"','"+number+"','"+remark+"','"+startTime+"','"+endTime+"')";
                    stmt.execute(sql);
                    break;
                case "custom":
                    content = hashMap.get("content");
                    startTime = hashMap.get("startTime");
                    endTime = hashMap.get("endTime");
                    sql ="INSERT INTO custom (scheduleID,content,startTime,endTime)VALUES " +
                            "('"+scheduleID+"','"+content+"','"+startTime+"','"+endTime+"')";
                    stmt.execute(sql);
                    break;
            }
        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }
        return Integer.parseInt(scheduleID);
    }//测试完毕

    public ArrayList<HashMap<String,String>> queryByTime(String startTime,String endTime){
        //处理传入startTime endTime都为空，此时查找custom表中startTime，endTime都为空字符串的待办事项
        if(startTime.equals("") && endTime.equals("")){
            ArrayList<HashMap<String,String>> result = new ArrayList<HashMap<String, String>>();
            String sql;
            ResultSet rs= null;     //将sql语句传至数据库，返回的值为一个字符集用一个变量接收
            sql = "SELECT * FROM custom WHERE startTime = '' and endTime = ''";
            try {
                rs = stmt.executeQuery(sql);
                while(rs.next()){    //next（）获取里面的内容
                    HashMap hashMap = new HashMap();
                    hashMap.put("content",rs.getString("content"));
                    hashMap.put("startTime",rs.getString("startTime") );
                    hashMap.put("endTime",rs.getString("endTime"));
                    hashMap.put("scheduleID",rs.getString("scheduleID"));
                    result.add(hashMap);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            getScheduleInfoAndDeleteSon(result);
            return result;
        }
        ArrayList<HashMap<String,String>> result = new ArrayList<HashMap<String, String>>();
        Calendar queryStartTime = strToCal(startTime);//查询开始时间
        Calendar queryEndTime = strToCal(endTime);//查询结束时间
        String sql = "";
        ResultSet rs = null;     //将sql语句传至数据库，返回的值为一个字符集用一个变量接收
        //查询travel表
        queryTravel(queryStartTime, queryEndTime, result);
        //查询meeting表
        queryMeeting(queryStartTime, queryEndTime, result);
        //查询interview表
        queryInterview(queryStartTime, queryEndTime, result);
        //查询custom表
        queryCustom(queryStartTime, queryEndTime, result);
        //查询course表
        queryCourse(queryStartTime, queryEndTime, result);
        //查找anniversary表
        queryAnniversary(queryStartTime, queryEndTime, result);
        //查询date表
        queryDate(queryStartTime, queryEndTime, result);
        //得到schedule表共有的信息，删除子待办事项
        getScheduleInfoAndDeleteSon(result);
        return result;
    }

    public ArrayList<HashMap<String,String>> queryByFatherID(int fatherID){
        ArrayList<HashMap<String,String>> result = new ArrayList<HashMap<String, String>>();
        String sql = "SELECT * FROM schedule WHERE fatherID = '"+fatherID+"'";
        ResultSet rs= null;     //将sql语句传至数据库，返回的值为一个字符集用一个变量接收
        try {
            rs = stmt.executeQuery(sql);
            while(rs.next()){    //next（）获取里面的内容
                HashMap hashMap = new HashMap();
                hashMap.put("ID", rs.getString("ID"));
                hashMap.put("type",rs.getString("type"));
                hashMap.put("priority",rs.getString("priority"));
                hashMap.put("status",rs.getString("status"));
                hashMap.put("isFather",rs.getString("isFather"));
                hashMap.put("FatherID",rs.getString("fatherID") );
                hashMap.put("promptStatus",rs.getString("promptStatus"));
                hashMap.put("minutesAhead",rs.getString("minutesAhead"));
                hashMap.put("showOnStage",rs.getString("showOnStage"));
                hashMap.put("minuteDelta",rs.getString("minuteDelta") );
                result.add(hashMap);
            }
            for(int i=0;i<result.size();i++){
                String scheduleID = result.get(i).get("ID");
                String type = result.get(i).get("type");
                switch (type){
                    case "date":
                        sql="SELECT * FROM calendar.date WHERE scheduleID = '"+scheduleID+"'";
                        rs = stmt.executeQuery(sql);
                        while(rs.next()){    //next（）获取里面的内容
                            result.get(i).put("place", rs.getString("place"));
                            result.get(i).put("people",rs.getString("people"));
                            result.get(i).put("content",rs.getString("content"));
                            result.get(i).put("startTime",rs.getString("startTime") );
                            result.get(i).put("endTime",rs.getString("endTime"));
                        }
                        break;
                    case "anniversary":
                        sql="SELECT * FROM calendar.anniversary WHERE scheduleID = '"+scheduleID+"'";
                        rs = stmt.executeQuery(sql);
                        while(rs.next()){    //next（）获取里面的内容
                            result.get(i).put("anniversaryType", rs.getString("anniversaryType"));
                            result.get(i).put("content",rs.getString("content"));
                            result.get(i).put("startDay",rs.getString("startDay") );
                        }
                        break;
                    case "course":
                        sql="SELECT * FROM calendar.course WHERE scheduleID = '"+scheduleID+"'";
                        rs = stmt.executeQuery(sql);
                        while(rs.next()){    //next（）获取里面的内容
                            result.get(i).put("name", rs.getString("name"));
                            result.get(i).put("teacher",rs.getString("teacher"));
                            result.get(i).put("content",rs.getString("content"));
                            result.get(i).put("startDay",rs.getString("startDay") );
                            result.get(i).put("duration",rs.getString("duration"));
                            result.get(i).put("remark",rs.getString("remark"));
                            result.get(i).put("startTime",rs.getString("startTime") );
                            result.get(i).put("endTime",rs.getString("endTime"));
                            result.get(i).put("place",rs.getString("place") );
                            result.get(i).put("day",rs.getString("day"));
                        }
                        break;
                    case "custom":
                        sql="SELECT * FROM calendar.custom WHERE scheduleID = '"+scheduleID+"'";
                        rs = stmt.executeQuery(sql);
                        while(rs.next()){    //next（）获取里面的内容
                            result.get(i).put("content",rs.getString("content"));
                            result.get(i).put("startTime",rs.getString("startTime") );
                            result.get(i).put("endTime",rs.getString("endTime"));
                        }
                        break;
                    case "interview":
                        sql="SELECT * FROM calendar.interview WHERE scheduleID = '"+scheduleID+"'";
                        rs = stmt.executeQuery(sql);
                        while(rs.next()){    //next（）获取里面的内容
                            result.get(i).put("place",rs.getString("place"));
                            result.get(i).put("company",rs.getString("company") );
                            result.get(i).put("job",rs.getString("job"));
                            result.get(i).put("remark",rs.getString("remark"));
                            result.get(i).put("startTime",rs.getString("startTime") );
                            result.get(i).put("endTime",rs.getString("endTime"));
                        }
                        break;
                    case "meeting":
                        sql="SELECT * FROM calendar.meeting WHERE scheduleID = '"+scheduleID+"'";
                        rs = stmt.executeQuery(sql);
                        while(rs.next()){    //next（）获取里面的内容
                            result.get(i).put("place",rs.getString("place"));
                            result.get(i).put("topic",rs.getString("topic") );
                            result.get(i).put("content",rs.getString("content"));
                            result.get(i).put("startTime",rs.getString("startTime") );
                            result.get(i).put("endTime",rs.getString("endTime"));
                        }
                        break;
                    case "travel":
                        sql="SELECT * FROM calendar.travel WHERE scheduleID = '"+scheduleID+"'";
                        rs = stmt.executeQuery(sql);
                        while(rs.next()){    //next（）获取里面的内容
                            result.get(i).put("place",rs.getString("place"));
                            result.get(i).put("way",rs.getString("way") );
                            result.get(i).put("number",rs.getString("number"));
                            result.get(i).put("remark",rs.getString("remark") );
                            result.get(i).put("startTime",rs.getString("startTime") );
                            result.get(i).put("endTime",rs.getString("endTime"));
                        }
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }//测试完毕

    public int deleteSchedule(int scheduleID){
        String type="";
        String sql = "SELECT type FROM schedule WHERE ID = '"+scheduleID+"'";
        ResultSet rs= null;     //将sql语句传至数据库，返回的值为一个字符集用一个变量接收
        try {
            rs = stmt.executeQuery(sql);
            while(rs.next()){    //next（）获取里面的内容
                type = rs.getString("type");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(type);
        switch (type){
            case "date":
                sql="DELETE FROM calendar.date WHERE scheduleID = '"+scheduleID+"'";   //删除分表中的行
                break;
            case "anniversary":
                sql="DELETE FROM calendar.anniversary WHERE scheduleID = '"+scheduleID+"'";   //删除分表中的行
                break;
            case "course":
                sql="DELETE FROM calendar.course WHERE scheduleID = '"+scheduleID+"'";   //删除分表中的行
                break;
            case "custom":
                sql="DELETE FROM calendar.custom WHERE scheduleID = '"+scheduleID+"'";   //删除分表中的行
                break;
            case "interview":
                sql="DELETE FROM calendar.interview WHERE scheduleID = '"+scheduleID+"'";   //删除分表中的行
                break;
            case "meeting":
                sql="DELETE FROM calendar.meeting WHERE scheduleID = '"+scheduleID+"'";   //删除分表中的行
                break;
            case "travel":
                sql="DELETE FROM calendar.travel WHERE scheduleID = '"+scheduleID+"'";   //删除分表中的行
                break;
            default:
                System.out.println("sql error");
                return -1;
        }
        try {
            stmt.executeUpdate(sql);         //将sql语句上传至数据库执行
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sql="DELETE FROM schedule WHERE ID = '"+scheduleID+"'";   //删除总表中的行
        try {
            stmt.executeUpdate(sql);         //将sql语句上传至数据库执行
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }//测试完毕

    public  int updateState(int scheduleID,int status){
        String sql="UPDATE schedule SET status = '"+status+"'WHERE ID= '"+scheduleID+"' ";   //SQL语句
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }//测试完毕

    private static String getMaxID(Statement stmt) throws SQLException {
        String sql = "SELECT ID FROM schedule ORDER BY ID DESC";
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
       return  rs.getString("ID");
    }

    public void close(){
        try {
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    //判断calA在不在calB前
    private static Boolean before(Calendar calA, Calendar calB) {
        if (calA.get(Calendar.YEAR) < calB.get(Calendar.YEAR)) {
            return true;
        }else if (calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) && calA.get(Calendar.MONTH) < calB.get(Calendar.MONTH)) {
            return true;
        }else if (calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) && calA.get(Calendar.MONTH) == calB.get(Calendar.MONTH) &&
                calA.get(Calendar.DAY_OF_MONTH) < calB.get(Calendar.DAY_OF_MONTH)) {
            return true;
        } else if (calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) && calA.get(Calendar.MONTH) == calB.get(Calendar.MONTH) &&
                calA.get(Calendar.DAY_OF_MONTH) == calB.get(Calendar.DAY_OF_MONTH) && calA.get(Calendar.HOUR_OF_DAY) < calB.get(Calendar.HOUR_OF_DAY)){
            return true;
        }else if(calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) && calA.get(Calendar.MONTH) == calB.get(Calendar.MONTH) &&
                calA.get(Calendar.DAY_OF_MONTH) == calB.get(Calendar.DAY_OF_MONTH) && calA.get(Calendar.HOUR_OF_DAY) ==
                calB.get(Calendar.HOUR_OF_DAY) && calA.get(Calendar.MINUTE) <= calB.get(Calendar.MINUTE)){
            return true;
        }else{
            return false;
        }
    }

    //判断calA在不在calB后
    private static Boolean after(Calendar calA, Calendar calB) {
        if (calA.get(Calendar.YEAR) > calB.get(Calendar.YEAR)) {
            return true;
        }else if (calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) && calA.get(Calendar.MONTH) > calB.get(Calendar.MONTH)) {
            return true;
        }else if (calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) && calA.get(Calendar.MONTH) == calB.get(Calendar.MONTH) &&
                calA.get(Calendar.DAY_OF_MONTH) > calB.get(Calendar.DAY_OF_MONTH)) {
            return true;
        } else if (calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) && calA.get(Calendar.MONTH) == calB.get(Calendar.MONTH) &&
                calA.get(Calendar.DAY_OF_MONTH) == calB.get(Calendar.DAY_OF_MONTH) && calA.get(Calendar.HOUR_OF_DAY) > calB.get(Calendar.HOUR_OF_DAY)){
            return true;
        }else if(calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) && calA.get(Calendar.MONTH) == calB.get(Calendar.MONTH) &&
                calA.get(Calendar.DAY_OF_MONTH) == calB.get(Calendar.DAY_OF_MONTH) && calA.get(Calendar.HOUR_OF_DAY) ==
                calB.get(Calendar.HOUR_OF_DAY) && calA.get(Calendar.MINUTE) >= calB.get(Calendar.MINUTE)){
            return true;
        }else{
            return false;
        }
    }

    //字符串转换成Calendar
    private static Calendar strToCal(String str){//2018-4-14 23:59
        String strArray[] = str.split(" ");
        String strArray1[] = strArray[0].split("-");
        int year = Integer.valueOf(strArray1[0]);
        int month = Integer.valueOf(strArray1[1]);
        int day = Integer.valueOf(strArray1[2]);
        String strArray2[] = strArray[1].split(":");
        int hour = Integer.valueOf(strArray2[0]);
        int minute = Integer.valueOf(strArray2[1]);
        Calendar cal = Calendar.getInstance();
        cal.set(year,month-1,day,hour,minute);
        return cal;
    }

    //Calendar转成string
    private static String calToStr(Calendar cal){
        int month = cal.get(Calendar.MONTH) + 1;
        String s = cal.get(Calendar.YEAR)+"-"+month+"-"+cal.get(Calendar.DAY_OF_MONTH)+" "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE);
        return s;
    }

    //查询schedule表中信息，并删除子待办事项
    private void getScheduleInfoAndDeleteSon(ArrayList<HashMap<String,String>> result){
        String sql;
        ResultSet rs= null;
        for(int i=0;i<result.size();i++){
            sql = "SELECT * FROM calendar.schedule WHERE ID = '"+result.get(i).get("scheduleID")+"'";
            try {
                rs = stmt.executeQuery(sql);
                while(rs.next()){    //next（）获取里面的内容
                    result.get(i).put("type",rs.getString("type"));
                    result.get(i).put("priority",rs.getString("priority") );
                    result.get(i).put("status",rs.getString("status"));
                    result.get(i).put("isFather",rs.getString("isFather"));
                    result.get(i).put("fatherID",rs.getString("fatherID"));
                    result.get(i).put("promptStatus",rs.getString("promptStatus") );
                    result.get(i).put("minutesAhead",rs.getString("minutesAhead"));
                    result.get(i).put("showOnStage",rs.getString("showOnStage"));
                    result.get(i).put("minuteDelta",rs.getString("minuteDelta"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        for(int i=0;i<result.size();i++){
            if(result.get(i).get("isFather").equals("0")){
                result.remove(i);
                i--;
            }
        }
    }

    //查询Date类满足条件的待办事项
    private void queryDate(Calendar queryStartTime,Calendar queryEndTime,ArrayList<HashMap<String,String>> result){
        String sql = "SELECT * FROM date";
        ResultSet rs= null;
        try {
            rs = stmt.executeQuery(sql);
            while(rs.next()){    //next（）获取里面的内容
                Calendar resultStartTime = strToCal(rs.getString("startTime"));
                Calendar resultEndTime = strToCal(rs.getString("endTime"));
                if((before(resultStartTime,queryStartTime) && after(resultEndTime,queryStartTime)) || after(resultStartTime,queryStartTime) && before(resultStartTime,queryEndTime)){
                    HashMap hashMap = new HashMap();
                    hashMap.put("place",rs.getString("place"));
                    hashMap.put("people",rs.getString("people") );
                    hashMap.put("content",rs.getString("content"));
                    hashMap.put("startTime",rs.getString("startTime") );
                    hashMap.put("endTime",rs.getString("endTime"));
                    hashMap.put("scheduleID",rs.getString("scheduleID"));
                    result.add(hashMap);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void queryAnniversary(Calendar queryStartTime,Calendar queryEndTime,ArrayList<HashMap<String,String>> result){
        String sql = "SELECT * FROM anniversary";
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(sql);
            while(rs.next()){
                Calendar resultStartTime = strToCal(rs.getString("startDay")+" "+"0:0");
                Calendar resultEndTime = strToCal(rs.getString("startDay")+" "+"23:59");
                ArrayList<HashMap<String,Calendar>> times = new ArrayList<HashMap<String, Calendar>>();
                for(int i = 0; i < 100;i++){
                    HashMap hashMap = new HashMap();
                    hashMap.put("startTime",resultStartTime.clone());
                    hashMap.put("endTime",resultEndTime.clone());
                    times.add(hashMap);
                    resultStartTime.add(Calendar.YEAR,1);
                    resultEndTime.add(Calendar.YEAR,1);
                }
                //得到此纪念日向后100年的纪念日
                for(int i=0;i<times.size();i++){
                    resultStartTime = times.get(i).get("startTime");
                    resultEndTime = times.get(i).get("endTime");
                    if((before(resultStartTime,queryStartTime) && after(resultEndTime,queryStartTime)) || after(resultStartTime,queryStartTime) && before(resultStartTime,queryEndTime)){
                        HashMap hashMap = new HashMap();
                        hashMap.put("anniversaryType",rs.getString("anniversaryType"));
                        hashMap.put("content",rs.getString("content") );
                        hashMap.put("startDay",rs.getString("startDay"));
                        hashMap.put("startTime",calToStr(resultStartTime));
                        hashMap.put("scheduleID",rs.getString("scheduleID"));
                        hashMap.put("endTime",calToStr(resultEndTime));
                        hashMap.put("year",resultStartTime.get(Calendar.YEAR));
                        result.add(hashMap);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void queryCourse(Calendar queryStartTime,Calendar queryEndTime,ArrayList<HashMap<String,String>> result){
        String sql = "SELECT * FROM course";
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(sql);
            while(rs.next()){    //next（）获取里面的内容
                Calendar resultStartTime = strToCal(rs.getString("startTime"));//得到第一周上课时间
                Calendar resultEndTime = strToCal(rs.getString("endTime"));//得到第一周下课时间
                int duration = Integer.valueOf(rs.getString("duration"));//得到周数
                ArrayList<HashMap<String,Calendar>> times = new ArrayList<HashMap<String, Calendar>>();
                for(int i = 0; i < duration;i++){
                    HashMap hashMap = new HashMap();
                    hashMap.put("startTime",resultStartTime.clone());
                    hashMap.put("endTime",resultEndTime.clone());
                    times.add(hashMap);
                    resultStartTime.add(Calendar.DAY_OF_MONTH,7);
                    resultEndTime.add(Calendar.DAY_OF_MONTH,7);
                }
                for(int i=0;i<times.size();i++){
                    resultStartTime = times.get(i).get("startTime");
                    resultEndTime = times.get(i).get("endTime");
                    if((before(resultStartTime,queryStartTime) && after(resultEndTime,queryStartTime)) || after(resultStartTime,queryStartTime) && before(resultStartTime,queryEndTime)){
                        HashMap hashMap = new HashMap();
                        hashMap.put("name",rs.getString("name"));
                        hashMap.put("content",rs.getString("content") );
                        hashMap.put("startDay",rs.getString("startDay"));
                        hashMap.put("scheduleID",rs.getString("scheduleID"));
                        hashMap.put("duration",rs.getString("duration"));
                        hashMap.put("teacher",rs.getString("teacher") );
                        hashMap.put("remark",rs.getString("remark"));
                        hashMap.put("place",rs.getString("place"));
                        hashMap.put("startTime",calToStr(resultStartTime));
                        hashMap.put("endTime",calToStr(resultEndTime));
                        result.add(hashMap);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void queryCustom(Calendar queryStartTime,Calendar queryEndTime,ArrayList<HashMap<String,String>> result){
        String sql = "SELECT * FROM custom";
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(sql);
            while(rs.next()){    //next（）获取里面的内容
                if(rs.getString("startTime").equals("") && rs.getString("endTime").equals("")){
                    continue;
                }
                Calendar resultStartTime = strToCal(rs.getString("startTime"));
                Calendar resultEndTime = strToCal(rs.getString("endTime"));
                if((before(resultStartTime,queryStartTime) && after(resultEndTime,queryStartTime)) || after(resultStartTime,queryStartTime) && before(resultStartTime,queryEndTime)){
                    HashMap hashMap = new HashMap();
                    hashMap.put("content",rs.getString("content"));
                    hashMap.put("startTime",rs.getString("startTime") );
                    hashMap.put("endTime",rs.getString("endTime"));
                    hashMap.put("scheduleID",rs.getString("scheduleID"));
                    result.add(hashMap);
                }
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private void queryInterview(Calendar queryStartTime,Calendar queryEndTime,ArrayList<HashMap<String,String>> result){
        String sql = "SELECT * FROM interview ";
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(sql);
            while(rs.next()){    //next（）获取里面的内容
                Calendar resultStartTime = strToCal(rs.getString("startTime"));
                Calendar resultEndTime = strToCal(rs.getString("endTime"));
                if((before(resultStartTime,queryStartTime) && after(resultEndTime,queryStartTime)) || after(resultStartTime,queryStartTime) && before(resultStartTime,queryEndTime)){
                    HashMap hashMap = new HashMap();
                    hashMap.put("place",rs.getString("place"));
                    hashMap.put("company",rs.getString("company") );
                    hashMap.put("job",rs.getString("job"));
                    hashMap.put("remark",rs.getString("remark"));
                    hashMap.put("startTime",rs.getString("startTime") );
                    hashMap.put("endTime",rs.getString("endTime"));
                    hashMap.put("scheduleID",rs.getString("scheduleID"));
                    result.add(hashMap);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void queryMeeting(Calendar queryStartTime,Calendar queryEndTime,ArrayList<HashMap<String,String>> result){
        String sql = "SELECT * FROM meeting ";
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(sql);
            while(rs.next()){    //next（）获取里面的内容
                Calendar resultStartTime = strToCal(rs.getString("startTime"));
                Calendar resultEndTime = strToCal(rs.getString("endTime"));
                if((before(resultStartTime,queryStartTime) && after(resultEndTime,queryStartTime)) || after(resultStartTime,queryStartTime) && before(resultStartTime,queryEndTime)){
                    HashMap hashMap = new HashMap();
                    hashMap.put("place",rs.getString("place"));
                    hashMap.put("topic",rs.getString("topic") );
                    hashMap.put("content",rs.getString("content"));
                    hashMap.put("startTime",rs.getString("startTime") );
                    hashMap.put("endTime",rs.getString("endTime"));
                    hashMap.put("scheduleID",rs.getString("scheduleID"));
                    result.add(hashMap);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void queryTravel(Calendar queryStartTime,Calendar queryEndTime,ArrayList<HashMap<String,String>> result){
        String sql = "SELECT * FROM travel ";
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(sql);
            while(rs.next()){    //next（）获取里面的内容
                Calendar resultStartTime = strToCal(rs.getString("startTime"));
                Calendar resultEndTime = strToCal(rs.getString("endTime"));
                if((before(resultStartTime,queryStartTime) && after(resultEndTime,queryStartTime)) || after(resultStartTime,queryStartTime) && before(resultStartTime,queryEndTime)){
                    HashMap hashMap = new HashMap();
                    hashMap.put("place",rs.getString("place"));
                    hashMap.put("way",rs.getString("way") );
                    hashMap.put("number",rs.getString("number"));
                    hashMap.put("remark",rs.getString("remark") );
                    hashMap.put("startTime",rs.getString("startTime") );
                    hashMap.put("endTime",rs.getString("endTime"));
                    hashMap.put("scheduleID",rs.getString("scheduleID"));
                    result.add(hashMap);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
