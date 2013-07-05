/*
 * Copyright (C) 2010 Makas Tzavellas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.babytree.apps.comm.ui.page;

import java.util.ArrayList;

import android.view.View;

/**
 * 
 * The implementation of the interface is responsible to load data in the background.
 * 
 * @author Makas Tzavellas (makas dot tzavellas at gmail dot com)
 * 
 * @param <T>
 */
public interface AbstractDataLoaderHandler<T> {
    
    public static final int REQUEST_FIRST_SUCCESS = 1;
    public static final int REQUEST_SUCCESS = 2;
    public static final int REQUEST_FIRST_FAILED = 3;
    public static final int REQUEST_FAILED = 4;
    
    public static final int FIRST_START = 1;
    
    /**
     * The maximum item that can be loaded.
     * This method should probably only be called after the initial download has occured.
     * @return The max item available.
     */
    int getMaxItems();
    
    String getName();
    
    /**
     * Returns values that has been loaded or may trigger the download of the first batch.
     * @param callback The callback to notify when data is available.
     */
    void getValues(DataLoadedCallback<T> callback);
    /**
     * Returns the next batch of data.
     * @param callback The callback to notify when data is available.
     */
    void getNext(DataLoadedCallback<T> callback);

    /**
     * True if the implementation is busy loading data in the background.
     * @return
     */
    boolean isLoading();
    
    /**
     * The implementation of this interface will be called when the data has been downloaded.
     * 
     * @author Makas Tzavellas (makas dot tzavellas at gmail dot com)
     * 
     * @param <T>
     */
    interface DataLoadedCallback<T> {
        /**
         * Notifies the implementation that the download has completed with a list of values.
         * @param values The new values downloaded.
         */
        void dataLoaded(ArrayList<T> values);
        void dataLoadedDuplicate(ArrayList<T> values);
        void refreshTop(ArrayList<T> values);
        void showLoading(boolean bool);
        void showReloading(boolean bool);
        View getReloadingView();
        void clear();
        void addTop(T t);
        void addFooter(T t);
        void removeItem(T t);
    }
   
}
