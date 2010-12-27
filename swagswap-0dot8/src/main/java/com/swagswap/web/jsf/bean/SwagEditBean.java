package com.swagswap.web.jsf.bean;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.swagswap.domain.SwagItem;
import com.swagswap.web.jsf.model.SwagItemWrapper;

/**
 * @author scott
 * 
 *         View scope bean to store SwagItem being edited or inserted.
 *         Simplifies state saving.
 * 
 */
@ManagedBean(name = "swagEditBean")
@ViewScoped
public class SwagEditBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private SwagItemWrapper editSwagItem;
	private String newComment = "";
	private Long selectedRowId;
	private String lastPage;
	private String uploadMessage = "";

	public String getUploadMessage() {
		return uploadMessage;
	}

	public void setUploadMessage(String uploadMessage) {
		this.uploadMessage = uploadMessage;
	}

	public String getLastPage() {
		return lastPage;
	}

	public void setLastPage(String lastPage) {
		this.lastPage = lastPage;
	}

	public Long getSelectedRowId() {
		return selectedRowId;
	}

	public void setSelectedRowId(Long selectedRowId) {
		this.selectedRowId = selectedRowId;
	}

	public String getNewComment() {
		return newComment;
	}

	public void setNewComment(String newComment) {
		this.newComment = newComment;
	}

	public SwagItemWrapper getEditSwagItem() {
		return editSwagItem;
	}

	public void setEditSwagItem(SwagItemWrapper editSwagItem) {
		this.editSwagItem = editSwagItem;
	}

	public void initialiseSwagItem() {
		if (editSwagItem == null) {
			editSwagItem = new SwagItemWrapper(new SwagItem(), 0, true);
		}
	}

}
