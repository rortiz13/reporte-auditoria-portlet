package la.netco.auditoria.beans;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import la.netco.liferay.audit.model.AuditLog;
import la.netco.liferay.audit.service.AuditLogLocalServiceUtil;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;


public class HtmlExport extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public HtmlExport() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		   String idAuditoria = request.getParameter("idAuditLog");
		   
		   System.out.println("   idAuditoria " + idAuditoria);
		   try {
			   if(idAuditoria != null){
				   
				  
				   AuditLog audit = AuditLogLocalServiceUtil.getAuditLog( Long.parseLong(idAuditoria));
	
				   byte[] htmlBytes = audit.getHtmlResult().getBytes();
				   PrintWriter out = response.getWriter();
				   if(htmlBytes != null){
					   String html = new String(htmlBytes);
					   html = html.replaceAll("localhost", "190.143.64.177");
					   out.println(html);
					   
				   }else{
					   out.println("NO DISPONIBLE");
				   }
				   
			   }
		   
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (PortalException e) {
				e.printStackTrace();
			} catch (SystemException e) {
				e.printStackTrace();
			}
		   
	}

}
