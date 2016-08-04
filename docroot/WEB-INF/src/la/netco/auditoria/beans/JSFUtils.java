package la.netco.auditoria.beans;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.util.PortalUtil;

public class JSFUtils {

	  public static FacesContext getFacesContext() {
	      return FacesContext.getCurrentInstance();
	   }

	   public static Application getApplication() {
	      return getFacesContext().getApplication();
	   }

	   public static ExternalContext getExternalContext() {
	      return getFacesContext().getExternalContext();
	   }

	   public static ELContext getELContext() {
	      return getFacesContext().getELContext();
	   }

	   public static HttpServletRequest getServletRequest() {
	      return PortalUtil.getHttpServletRequest(getPortletRequest());
	   }

	   public static PortletRequest getPortletRequest() {
	      return LiferayFacesContext.getInstance().getPortletRequest();
	   }

	   public static HttpSession getPortletSession() {
	      return (HttpSession) getServletRequest().getSession();
	   }

	   public static ValueExpression createValueExpression(String expression) {
	      return getApplication().getExpressionFactory().createValueExpression(
	           getELContext(), 
	           "#{" + expression + "}", 
	           Object.class);
	   }

	   public static Object getExpressionValue(String expression) {
	      return createValueExpression(expression).getValue(getELContext());
	   }
}
