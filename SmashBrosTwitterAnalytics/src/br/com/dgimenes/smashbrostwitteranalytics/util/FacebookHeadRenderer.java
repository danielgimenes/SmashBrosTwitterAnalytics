package br.com.dgimenes.smashbrostwitteranalytics.util;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.sun.faces.renderkit.Attribute;
import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.renderkit.html_basic.HeadRenderer;

//<render-kit>
//<renderer>
//	<component-family>javax.faces.Output</component-family>
//	<renderer-type>javax.faces.Head</renderer-type>
//	<renderer-class>br.com.dgimenes.smashbrostwitteranalytics.util.FacebookHeadRenderer</renderer-class>
//</renderer>
//</render-kit>
public class FacebookHeadRenderer extends HeadRenderer {

	private static final Attribute[] EXTRA_HEAD_ATTRIBUTES = { Attribute.attr("prefix") };

	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		super.encodeBegin(context, component);
		ResponseWriter writer = context.getResponseWriter();
		RenderKitUtils.renderPassThruAttributes(context, writer, component, EXTRA_HEAD_ATTRIBUTES);
	}
}