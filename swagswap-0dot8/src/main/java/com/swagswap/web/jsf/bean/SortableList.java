package com.swagswap.web.jsf.bean;

public interface SortableList {

    void sort(String sortColumn);

    String getSort();

    void setSort(String sort);

    boolean isAscending();

    void setAscending(boolean ascending);
            
    void sort(String column, boolean ascending);    
    
}
