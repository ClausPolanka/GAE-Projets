package com.google.appengine.demos.sticky.client;

import com.google.appengine.demos.sticky.client.model.Model;
import com.google.appengine.demos.sticky.client.model.Model.LoadObserver;
import com.google.appengine.demos.sticky.client.model.Model.StatusObserver;
import com.google.appengine.demos.sticky.client.model.RetryTimer;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;

public class Main extends RetryTimer implements EntryPoint, Model.LoadObserver, Model.StatusObserver {

	/**
	 * An aggregated image bundle will auto-sprite all the images in the
	 * application.
	 */
	public interface Images extends HeaderView.Images {
	}

	/**
	 * Provides Ui to notify the user of model based events. These include tasks
	 * (like loading a surface) and also errors (like lost communication to the
	 * server).
	 */
	private static class StatusView extends SimplePanel {
		private static final boolean NOT_VISIBLE = false;
		private static final boolean VISIBLE = false;
		private final DivElement taskStatusElement;
		private final DivElement errorStatusElement;

		public StatusView() {
			final Document document = Document.get();
			final Element element = getElement();
			taskStatusElement = element.appendChild(document.createDivElement());
			errorStatusElement = element.appendChild(document.createDivElement());
			errorStatusElement.setInnerText("No response from server");

			setStyleName("status-view");
			taskStatusElement.setClassName("status-view-task");
			errorStatusElement.setClassName("status-view-error");

			hideErrorStatus();
			hideTaskStatus();
		}

		public void hideErrorStatus() {
			UIObject.setVisible(errorStatusElement, NOT_VISIBLE);
		}

		public void hideTaskStatus() {
			UIObject.setVisible(taskStatusElement, NOT_VISIBLE);
		}

		public void showErrorStatus() {
			UIObject.setVisible(errorStatusElement, VISIBLE);
		}

		public void showTaskStatus(String text) {
			taskStatusElement.setInnerText(text);
			UIObject.setVisible(taskStatusElement, VISIBLE);
		}
	}

	private final StatusView status = new StatusView();
	private LoadObserver thisAsLoadObserverCallback = this;
	private StatusObserver thisAsStatusObserverCallback = this;

	@Override
	public void onModelLoaded(Model model) {
		status.hideTaskStatus();
		status.hideErrorStatus();

		disableTopLevelScrollBar();

		final RootPanel root = RootPanel.get();
		createHeaderViewFor(model, root);
		root.add(new SurfaceView(model));
	}

	private void disableTopLevelScrollBar() {
		Window.enableScrolling(false);
	}

	private void createHeaderViewFor(Model model, final RootPanel parent) {
		new HeaderView(GWT.<Images> create(Images.class), parent, model);
	}

	@Override
	public void onModelLoadFailed() {
		retryLater();
		status.showErrorStatus();
	}

	@Override
	public void onModuleLoad() {
		RootPanel.get().add(status);
		status.showTaskStatus("Loading");
		Model.load(thisAsLoadObserverCallback, thisAsStatusObserverCallback);
	}

	@Override
	public void onServerCameBack() {
		status.hideErrorStatus();
	}

	@Override
	public void onServerWentAway() {
		status.showErrorStatus();
	}

	@Override
	public void onTaskFinished() {
		status.hideTaskStatus();
	}

	@Override
	public void onTaskStarted(String description) {
		status.showTaskStatus(description);
	}

	@Override
	protected void retry() {
		Model.load(thisAsLoadObserverCallback, thisAsStatusObserverCallback);
	}
}
