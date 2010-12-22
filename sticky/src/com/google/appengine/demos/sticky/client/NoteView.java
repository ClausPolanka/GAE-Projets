package com.google.appengine.demos.sticky.client;

import com.google.appengine.demos.sticky.client.model.Model;
import com.google.appengine.demos.sticky.client.model.Note;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;

public class NoteView extends FlowPanel implements Note.Observer, MouseUpHandler, MouseDownHandler, MouseMoveHandler, ValueChangeHandler<String> {
	private final Note note;

	private final DivElement titleElement;

	private final TextArea content = new TextArea();

	// Dragging state.
	private boolean dragging;

	private int dragOffsetX, dragOffsetY;

	private SurfaceView surfaceView;

	private Model model;

	/**
	 * @param note
	 *            the note to render
	 */
	public NoteView(Note note, SurfaceView surfaceView, Model model) {
		this.model = model;
		this.surfaceView = surfaceView;
		this.note = note;
		setStyleName("note");
		note.setObserver(this);

		// Build simple DOM Structure.
		final Element elem = getElement();
		elem.getStyle().setProperty("position", "absolute");
		titleElement = elem.appendChild(Document.get().createDivElement());
		titleElement.setClassName("note-title");

		content.setStyleName("note-content");
		content.addValueChangeHandler(this);
		// setWidget(content);
		add(content);

		render();

		addDomHandler(this, MouseDownEvent.getType());
		addDomHandler(this, MouseMoveEvent.getType());
		addDomHandler(this, MouseUpEvent.getType());

		FileUpload fileUpload = new FileUpload();
		add(fileUpload);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		surfaceView.select(this);
		if (!note.isOwnedByCurrentUser()) {
			return;
		}

		final EventTarget target = event.getNativeEvent().getEventTarget();
		assert Element.is(target);
		if (!Element.is(target)) {
			return;
		}

		if (titleElement.isOrHasChild(Element.as(target))) {
			dragging = true;
			final Element elem = getElement().cast();
			dragOffsetX = event.getX();
			dragOffsetY = event.getY();
			DOM.setCapture(elem);
			event.preventDefault();
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (dragging) {
			setPixelPosition(event.getX() + getAbsoluteLeft() - dragOffsetX, event.getY() + getAbsoluteTop() - dragOffsetY);
			event.preventDefault();
		}
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if (dragging) {
			dragging = false;
			DOM.releaseCapture(getElement());
			event.preventDefault();
			model.updateNotePosition(note, getAbsoluteLeft(), getAbsoluteTop(), note.getWidth(), note.getHeight());
		}
	}

	@Override
	public void onUpdate(Note note) {
		render();
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		model.updateNoteContent(note, event.getValue());
	}

	public void setPixelPosition(int x, int y) {
		final Style style = getElement().getStyle();
		style.setPropertyPx("left", x);
		style.setPropertyPx("top", y);
	}

	@Override
	public void setPixelSize(int width, int height) {
		content.setPixelSize(width, height);
	}

	private void render() {
		setPixelPosition(note.getX(), note.getY());

		setPixelSize(note.getWidth(), note.getHeight());

		titleElement.setInnerHTML(note.getAuthorName());

		final String noteContent = note.getContent();
		content.setText((noteContent == null) ? "" : noteContent);

		content.setReadOnly(!note.isOwnedByCurrentUser());
	}

	public void select() {
		getElement().getStyle().setProperty("zIndex", "" + surfaceView.nextZIndex());
	}
}
