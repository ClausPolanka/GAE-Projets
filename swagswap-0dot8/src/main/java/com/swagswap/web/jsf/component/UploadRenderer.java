package com.swagswap.web.jsf.component;

import java.io.IOException;

import javax.el.ValueExpression;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import javax.servlet.http.HttpServletRequest;

@FacesRenderer(rendererType="com.swagswap.web.jsf.component.UploadRenderer", componentFamily="javax.faces.Input")
public class UploadRenderer extends Renderer {

	public void encodeBegin(FacesContext context, UIComponent component)

	throws IOException {

		if (!component.isRendered())
			return;

		ResponseWriter writer = context.getResponseWriter();
		String clientId = component.getClientId(context);
		writer.startElement("input", component);
		writer.writeAttribute("type", "file", "type");
		writer.writeAttribute("name", clientId, "clientId");
		writer.endElement("input");
		writer.flush();
	}

	public void decode(FacesContext context, UIComponent component) {
		ExternalContext external = context.getExternalContext();
		HttpServletRequest request = (HttpServletRequest) external.getRequest();
		String clientId = component.getClientId(context);

		byte[] item = (byte[]) request.getAttribute(clientId);
		ValueExpression expression = component.getValueExpression("value");
		if (expression != null) {
			((EditableValueHolder) component).setSubmittedValue(item);
		}
	}
}
