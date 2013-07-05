
package com.babytree.apps.comm.ui.category;

public class PinnedHeaderListViewBean {
    public Object item;

    public String title;

    public PinnedHeaderListViewBean(Object item, String title) {
        this.item = item;
        this.title = title;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public Object getObject(){
        return item;
    }
    public void setObject(Object item){
        this.item = item;
    }
    @Override
    public String toString() {
        return item.toString();
    }
    
}
