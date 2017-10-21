package za.co.ktpsolutions.www.AppAPIClasses;

/**
 * Created by kayle on 2017/09/07.
 */

public class SQL_INFO {
    private static String DATABASE_URL = "jdbc:jtds:sqlserver://146.230.177.46/group3;instance=ist3";
    private static String DATABASE_USERNAME = "group3";
    private static String DATABASE_PASSWORD = "m7g2k";

    public static String getDatabaseURL(){
        return DATABASE_URL;
    }

    public static String getDatabaseUsername(){
        return DATABASE_USERNAME;
    }

    public static String getDatabasePassword(){
        return DATABASE_PASSWORD;
    }
}
