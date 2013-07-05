
package com.babytree.apps.comm.ui.page;

import java.util.ArrayList;

public class AbstractDataResult<T> {
    public int maxItems;
    public int status = -1;
    public String message = "";
    public String error = "";

    public ArrayList<T> values;
}
