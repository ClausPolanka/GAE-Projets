package com.swagswap.web.jsf.bean;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.swagswap.web.jsf.model.SwagItemWrapper;

public class SwagTableImpl implements Serializable, SwagTable, SortableList {

	private static final long serialVersionUID = 1L;

	private static final Integer rowsPerPage = 10;
	
	private List<SwagItemWrapper> swagList;
	private Integer firstRow = 0;
	
	private String page = "1";
	
    private String prevSort;
    private boolean prevSortDirection;
	
	public SwagTableImpl() {
		super();
	}
	
	public SwagTableImpl(List<SwagItemWrapper> swagList) {
		this();
		this.swagList = swagList;
	}
	
	/* (non-Javadoc)
	 * @see com.swagswap.web.jsf.bean.SwagTable#getPage()
	 */
	public String getPage() {
		return page;
	}

	/* (non-Javadoc)
	 * @see com.swagswap.web.jsf.bean.SwagTable#setPage(java.lang.String)
	 */
	public void setPage(String page) {
		this.page = page;
	}

	/* (non-Javadoc)
	 * @see com.swagswap.web.jsf.bean.SwagTable#getSwagList()
	 */
	public List<SwagItemWrapper> getSwagList() {
		return swagList;
	}

	/* (non-Javadoc)
	 * @see com.swagswap.web.jsf.bean.SwagTable#setSwagList(java.util.List)
	 */
	public void setSwagList(List<SwagItemWrapper> swagList) {
		this.swagList = swagList;
	}

	/* (non-Javadoc)
	 * @see com.swagswap.web.jsf.bean.SwagTable#getFirstRow()
	 */
	public Integer getFirstRow() {
		return firstRow;
	}

	/* (non-Javadoc)
	 * @see com.swagswap.web.jsf.bean.SwagTable#setFirstRow(java.lang.Integer)
	 */
	public void setFirstRow(Integer firstRow) {
		this.firstRow = firstRow;
	}
	
	/* (non-Javadoc)
	 * @see com.swagswap.web.jsf.bean.SwagTable#getRowsPerPage()
	 */
	public int getRowsPerPage() {
		return rowsPerPage;
	}
	
	/* (non-Javadoc)
	 * @see com.swagswap.web.jsf.bean.SwagTable#getTableSize()
	 */
	public int getTableSize() {
		return (swagList == null ? 0 : swagList
				.size());
	}

	/* (non-Javadoc)
	 * @see com.swagswap.web.jsf.bean.SwagTable#getLastRow()
	 */
	public int getLastRow() {
		return (swagList.size() < (firstRow + rowsPerPage)) ? swagList.size() : firstRow + rowsPerPage;
	}
	
	/* (non-Javadoc)
	 * @see com.swagswap.web.jsf.bean.SwagTable#actionPage()
	 */
	public void actionPage() {
		// TODO. Refactor this. Scott.
		if (page.equals("last")) {
			firstRow = new Double(
					(Math.floor(getTableSize() / rowsPerPage) * rowsPerPage))
					.intValue();
			return;
		}
		if (page.equals("first")) {
			firstRow = 0;
			return;
		}
		if (page.equals("prev")) {
			firstRow = firstRow - rowsPerPage;
			return;
		}
		if (page.equals("next")) {
			firstRow = firstRow + rowsPerPage;
			return;
		}

		int pageInt = Integer.parseInt(page);
		if (pageInt == -1) {
			// last page

			return;
		}
		firstRow = pageInt * rowsPerPage - rowsPerPage;
	}
	
	
	//  SortableList methods
	   public void sort(final String column, final boolean ascending) {
	        // final int columnIndex = getColumnIndex(column);
	        final int direction = (ascending) ? SortableListImpl.SORT_ASCENDING
	                : SortableListImpl.SORT_DESCENDING;
	        
//	        Comparator comparator = new Comparator() {
//	            public int compare(Object o1, Object o2) {
//
//	                int result = 0;
//
//	                Object column1 = getRowObjectAtColumn(o1, column, true);
//	               Object column2 = getRowObjectAtColumn(o2, column, true);
//
//	                if (column1 == null && column2 != null)
//	                    result = -1 * direction;
//	                else if (column1 == null && column2 == null)
//	                    result = 0;
//	                else if (column1 != null && column2 == null)
//	                    result = 1 * direction;
//	                else
//	                    result = ((Comparable) column1).compareTo(column2) * direction;
//
//	                return result;
//	            }
//	      
//	        };
//	        
//	        sort(column, ascending, comparator);
	    }

	    @SuppressWarnings("unchecked")
		public void sort(final String column, final boolean ascending, Comparator comparator) {
	        // Check if column is null
	        if (column == null)
	            return;

	        if ((prevSort != null) && (prevSort.equals(column)) && (prevSortDirection == ascending)) {
	            // no need to sort
	            return;
	        }
	        
	        // Added by woo-yo

	            Collections.sort(swagList, comparator);
	        
	        prevSort = column;
	        prevSortDirection = ascending;
	    }
	
	
	
	public String getSort() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAscending() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setAscending(boolean ascending) {
		// TODO Auto-generated method stub
		
	}

	public void setSort(String sort) {
		// TODO Auto-generated method stub
		
	}

	public void sort(String sortColumn) {
		// TODO Auto-generated method stub
		
	}



	
}
