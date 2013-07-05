package com.babytree.apps.comm.model;


public class Location {
    public Location(){
        
    }
    public Location(int id, String n, String t){
        _id =id;
        name = n;
        type = t;
    }
    public int _id;
    public String name = "";
    public String type = "";
    public String longname = "";
    public String active = "";
    public String province = "";
    public String order = "";
    public String postalcode = "";
    public String dropdownorder = "";
    public String idverifyorder = "";
    public int status;
}
