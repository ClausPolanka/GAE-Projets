package com.swagswap.web.jsf.bean;

public class SortableListImpl {
    
    private String _sort = "";
    
    private boolean _ascending;   
    
    public static final int SORT_ASCENDING = 1;

    public static final int SORT_DESCENDING = -1;
    
    private SortableList sortableList;

    public SortableListImpl(SortableList sortableList) {
        this.sortableList = sortableList; 
    }   

    public SortableListImpl(SortableList sortableList, String defaultSortColumn, boolean defaultAscending) {
        _sort = defaultSortColumn;
        _ascending = defaultAscending;
        this.sortableList = sortableList;       
    }

    public void sort(String column, boolean ascending) {
        sortableList.sort(column, ascending);
    }
    
    public void sort(String sortColumn) {

        if (sortColumn == null) {
            throw new IllegalArgumentException(
                    "Argument sortColumn must not be null.");
        }

        if (_sort.equals(sortColumn)) {
            // current sort equals new sortColumn -> reverse sort order
            _ascending = !_ascending;
        } else {
            // sort new column in default direction
            _sort = sortColumn;
            // _ascending = isDefaultAscending(_sort);
        }

        sort(_sort, _ascending);

    }


    public String getSort() {
        return _sort;
    }

    public void setSort(String sort) {  
        _sort = sort;
    }

    public boolean isAscending() {
        return _ascending;
    }

    public void setAscending(boolean ascending) {
        _ascending = ascending;
    }    

}
