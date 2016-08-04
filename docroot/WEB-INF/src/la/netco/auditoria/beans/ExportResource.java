package la.netco.auditoria.beans;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;

import la.netco.auditoria.primefaces.CustomResourceHandler;

public class ExportResource extends javax.faces.application.Resource {

	   public static final String RESOURCE_NAME = "export";


	   public static final String PARAM_NAME_EXPORT_ID = "exportId";
	   public static final String PARAM_NAME_EXPORT_TYPE = "exportContentType";
	   public static final String PARAM_NAME_EXPORT_FILENAME = "exportFilename";

	   // Private Data Members
	   private String requestPath;

	   public ExportResource() {
	      setLibraryName(CustomResourceHandler.LIBRARY_NAME);
	      setResourceName(RESOURCE_NAME);
	      
	      String contentType = JSFUtils.getExternalContext().getRequestParameterMap().get(PARAM_NAME_EXPORT_TYPE);
	      
	      if(contentType == null)
	    	  contentType = (String) JSFUtils.getExternalContext().getRequestMap().get(PARAM_NAME_EXPORT_TYPE);
	      
	      setContentType(contentType);
	   }

	   @Override
	   public boolean userAgentNeedsUpdate(FacesContext context) {
	      // Since this is a list that can potentially change dynamically, always return true.
	      return true;
	   }

	   @Override
	   public InputStream getInputStream() throws IOException {		   
		   
	      String exportId = "exportData" + JSFUtils.getExternalContext().getRequestParameterMap().get(PARAM_NAME_EXPORT_ID).toString();
	      InputStream is = new ByteArrayInputStream((byte[]) JSFUtils.getPortletSession().getAttribute(exportId));
	      
	      JSFUtils.getPortletSession().removeAttribute(exportId);
	      
	      return is;
	   }

	   @Override
	   public String getRequestPath() {

	      if (requestPath == null) {
	         StringBuilder buf = new StringBuilder();
	         buf.append(ResourceHandler.RESOURCE_IDENTIFIER);
	         buf.append("/");
	         buf.append(getResourceName());
	         buf.append("?ln=");
	         buf.append(getLibraryName());

	         buf.append("&");
	         buf.append(PARAM_NAME_EXPORT_ID);
	         buf.append("=");
	         buf.append(JSFUtils.getExternalContext().getRequestMap().get(PARAM_NAME_EXPORT_ID));

	         buf.append("&");
	         buf.append(PARAM_NAME_EXPORT_TYPE);
	         buf.append("=");
	         buf.append(JSFUtils.getExternalContext().getRequestMap().get(PARAM_NAME_EXPORT_TYPE));
	        
	         
	         buf.append("&");
	         buf.append(PARAM_NAME_EXPORT_FILENAME);
	         buf.append("=");
	         buf.append(JSFUtils.getExternalContext().getRequestMap().get(PARAM_NAME_EXPORT_FILENAME));

	         requestPath = buf.toString();
	      }
	      
	      return requestPath;
	   }

	   @Override
	   public Map<String, String> getResponseHeaders() {
	      Map<String, String> headers = new HashMap<String, String>();
	      headers.put("Expires", "0");
	      headers.put("Cache-Control","must-revalidate, post-check=0, pre-check=0");
	      headers.put("Pragma", "public");
	      headers.put("Content-disposition", "attachment;" +
	                      "filename=" + JSFUtils.getExternalContext().getRequestMap().get("exportFilename").toString());

	      return headers;
		//   return null;
	   }

	   @Override
	   public URL getURL() {
	      return null;
	   }

	   @Override
	   public String getContentType() {
	      return JSFUtils.getExternalContext().getRequestParameterMap().get(PARAM_NAME_EXPORT_TYPE).toString();
	   }

	}