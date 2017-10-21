package za.co.ktpsolutions.www.AppAPIClasses;

import java.io.Serializable;

/**
 * Created by kayle on 2017/09/07.
 */

public class Customer implements Serializable{

    public int m_ID;
    public String m_Name;
    public String m_Surname;
    public String m_Email;
    public String m_Cell;
    public String m_StreetAddress;
    public String m_City;
    public String m_AreaCode;
    public String m_Password;

    public Customer(){
    }

}
