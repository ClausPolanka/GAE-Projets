package com.swagswap.web.jsf.bean;

import java.util.List;

import com.swagswap.web.jsf.model.SwagItemWrapper;

public interface SwagTable {

	public abstract String getPage();

	public abstract void setPage(String page);

	public abstract List<SwagItemWrapper> getSwagList();

	public abstract void setSwagList(List<SwagItemWrapper> swagList);

	public abstract Integer getFirstRow();

	public abstract void setFirstRow(Integer firstRow);

	public abstract int getRowsPerPage();

	public abstract int getTableSize();

	public abstract int getLastRow();

	public abstract void actionPage();

}