package la.netco.auditoria.beans;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import la.netco.liferay.audit.model.AuditLog;
import la.netco.liferay.audit.model.AuditedResource;
import la.netco.liferay.audit.service.AuditLogLocalServiceUtil;
import la.netco.liferay.audit.service.AuditedResourceLocalServiceUtil;

import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.model.StreamedContent;

import co.com.tecnocom.auditoria.extra.AdditionalInfoUtil;
import co.com.tecnocom.auditoria.extra.ExporterUtil;

import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.util.PortalUtil;

@ManagedBean
@ViewScoped
public class ConsultarAuditoriaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	//\\\private List<AuditLog> registrosAuditoria;

	private Date fechaInicial;
	private Date fechaFinal;
	private Long userId;

	private String userName;
	private String comando;
	private String url;
	private String idRecurso;
	private AuditoriaDataModel principalDataModel;
	private Long idAuditedResource; 
	private Long idRegSeleccionado;
	private AuditLog registroSelecionado;
	private String selectedRecordUser;
	private List<SelectItem> auditedResourcesItems;
	
	/*@PostConstruct
	public void init() {
		if (registrosAuditoria == null) {

			try {

				registrosAuditoria = AuditLogLocalServiceUtil.getAuditLogs(0,
						100);

			} catch (SystemException e) {
				_log.error("Error leyendo AuditLogLocalServiceUtil ", e);
			}

		}
	}*/

	public String getUrlExport(){
		return (String) JSFUtils.getPortletSession().getAttribute("exportURL");
	}
	public String cargaFiltrosDataModel() {
		
		//DataExporter dataExporte;
		//dataExporte.
		
		List<Criterion> filtros = new ArrayList<Criterion>();
		
		if(userId != null &&  !userId.equals(new Integer(0).longValue())){
    		filtros.add(RestrictionsFactoryUtil.eq("userID",  userId)); 
    	}
		
		if(comando != null &&  !comando.trim().equals("") ){
    		filtros.add(RestrictionsFactoryUtil.eq("command",  comando)); 
    	}
		
		if(userName != null &&  !userName.trim().equals("") ){
    		filtros.add(RestrictionsFactoryUtil.eq("username",  userName)); 
    	}
		
		if(url != null &&  !url.trim().equals("") ){
    		filtros.add(RestrictionsFactoryUtil.eq("layoutURL",  url)); 
    	}
		
		if(idRecurso != null &&  !idRecurso.trim().equals("")){
    		filtros.add(RestrictionsFactoryUtil.eq("resourceId",  idRecurso)); 
    	}
		
		if(idAuditedResource != null &&  !idAuditedResource.equals(new Integer(0).longValue())){
    		filtros.add(RestrictionsFactoryUtil.eq("idAuditedResource",  idAuditedResource)); 
    	}
		
//		System.out.println("fechaInicial  : " + fechaInicial );
		if(fechaInicial != null || fechaFinal!=null){
			
			if(fechaInicial != null && fechaFinal == null ){  
				filtros.add(RestrictionsFactoryUtil.ge("fechaRegistro",  fechaInicial));    			
			}else if(fechaInicial == null && fechaFinal != null ){
				Calendar fecha = Calendar.getInstance();
				fecha.setTime(fechaFinal);
				fecha.set(Calendar.HOUR_OF_DAY, 24);
				filtros.add(RestrictionsFactoryUtil.le("fechaRegistro", fecha.getTime()));    		
			}else{
				Calendar fecha = Calendar.getInstance();
				fecha.setTime(fechaFinal);
				fecha.set(Calendar.HOUR_OF_DAY, 24);
				filtros.add(RestrictionsFactoryUtil.between("fechaRegistro", fechaInicial, fecha.getTime()));    	
			}    		
			
		}
		
		
		if(principalDataModel == null)
			principalDataModel = new AuditoriaDataModel();
			
		principalDataModel.setFiltros(filtros);
		return "";
	}
	
	
	public void cargarObjeto(){	
		try {
			if(idRegSeleccionado == null){
				 FacesContext facesContext = FacesContext.getCurrentInstance();
			     String idRegSeleccionado = facesContext.getExternalContext().getRequestParameterMap().get("idRegSeleccionado");
			     if(idRegSeleccionado != null) this.idRegSeleccionado = Long.parseLong(idRegSeleccionado);
			}
			
			
			if((registroSelecionado == null || registroSelecionado.getId() == 0) &&  (idRegSeleccionado != null && !idRegSeleccionado.equals(""))){
				registroSelecionado = AuditLogLocalServiceUtil.getAuditLog(idRegSeleccionado);
				AdditionalInfoUtil.INSTANCE.processXmlData(registroSelecionado);
				selectedRecordUser = AdditionalInfoUtil.INSTANCE.getUserInfo(registroSelecionado.getUserID());
			}	
			
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
			
	}
	
	public AuditedResource getAuditedResource(long id){
		AuditedResource resoruce = null;
		if (id > 0) {
			try {
				resoruce = AuditedResourceLocalServiceUtil.getAuditedResource(id);
			} catch (PortalException e) {
				e.printStackTrace();
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}
		return resoruce;
	}

	/*public List<AuditLog> getRegistrosAuditoria() {
		return registrosAuditoria;
	}

	public void setRegistrosAuditoria(List<AuditLog> registrosAuditoria) {
		this.registrosAuditoria = registrosAuditoria;
	}
*/
	public Date getFechaInicial() {
		return fechaInicial;
	}

	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	public Date getFechaFinal() {
		return fechaFinal;
	}

	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	//private static Log _log = LogFactoryUtil.getLog(ConsultarAuditoriaBean.class);

	public Long getUserId() {
		return userId;
	}

	public String getComando() {
		return comando;
	}

	public String getUrl() {
		return url;
	}

	public String getIdRecurso() {
		return idRecurso;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setComando(String comando) {
		this.comando = comando;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setIdRecurso(String idRecurso) {
		this.idRecurso = idRecurso;
	}

	private static final class AuditoriaDataModel extends
			PrimeDataModel<AuditLog, Integer> {
		private static final long serialVersionUID = 1L;

		private AuditoriaDataModel() {
			super(AuditLog.class);			
			setOrderBy(OrderFactoryUtil.desc("fechaRegistro"));
		}

		@Override
		protected Object getId(AuditLog t) {
			return t.getId();
		}
	}

	public AuditoriaDataModel getPrincipalDataModel() {
		return principalDataModel;
	}

	public void setPrincipalDataModel(AuditoriaDataModel principalDataModel) {
		this.principalDataModel = principalDataModel;
	}

	public Long getIdAuditedResource() {
		return idAuditedResource;
	}

	public void setIdAuditedResource(Long idAuditedResource) {
		this.idAuditedResource = idAuditedResource;
	}

	public List<SelectItem> getAuditedResourcesItems() {
		if (auditedResourcesItems == null) {
			try {
				List<AuditedResource> alldata =AuditedResourceLocalServiceUtil.getAuditedResources(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
				auditedResourcesItems = new ArrayList<SelectItem>();
				for (AuditedResource dato : alldata) {
					auditedResourcesItems.add(new SelectItem(dato.getId(), dato.getName()));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return auditedResourcesItems;
	}

	
	public void downloadHtml(String html){

		System.out.println(" downloadHtml " + html);

		/*FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		
		try {
			
			response.reset();
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition", "attachment; filename=\"prueba\"");
			
			OutputStream output = response.getOutputStream();
		    output.write(html.getBytes());
		    output.close();

		    System.out.println(" write >>>>> ");
		    // Inform JSF to not take the response in hands.
		    facesContext.responseComplete();
		
		} catch (IOException e) {
			
			e.printStackTrace();
		}*/
	}
	
	public void setAuditedResourcesItems(List<SelectItem> auditedResourcesItems) {
		this.auditedResourcesItems = auditedResourcesItems;
	}

	public Long getIdRegSeleccionado() {
		return idRegSeleccionado;
	}

	public void setIdRegSeleccionado(Long idRegSeleccionado) {
		this.idRegSeleccionado = idRegSeleccionado;
	}

	public AuditLog getRegistroSelecionado() {
		
		return registroSelecionado;
	}

	public void setRegistroSelecionado(AuditLog registroSelecionado) {
		this.registroSelecionado = registroSelecionado;
	}
	
	public String getSelectedRecordUser() {
		return selectedRecordUser;
	}
	public void setSelectedRecordUser(String selectedRecordUser) {
		this.selectedRecordUser = selectedRecordUser;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public StreamedContent exportAuditReport(String reportType) throws Exception {
		//	Antes de verificar el tipo de reporte, verificar si existe un DataModel para trabajar
		if(principalDataModel != null) {
			System.out.println("ReportType: " + reportType);
			List<AuditLog> logsToExport = principalDataModel.getDisplayedLogs();
			System.out.println("Logs to export: " + logsToExport.size());
			
			if(!logsToExport.isEmpty() && null != reportType && !reportType.isEmpty()) {
				String filename = "reporteAuditoria." + reportType;
				
				PortletResponse portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
				HttpServletResponse res = PortalUtil.getHttpServletResponse(portletResponse);
				res.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
				res.setHeader("Content-Transfer-Encoding", "binary");
				res.setContentType("application/octet-stream");
				res.flushBuffer();
				
				OutputStream out = res.getOutputStream();
				
				System.out.println("Generando reporte");
				if(reportType.equals("xls")) {
					Workbook wb = ExporterUtil.INSTANCE.exportAuditLogsToExcel(logsToExport);
					wb.write(out);
				} else if(reportType.equals("pdf")) {
					ExporterUtil.INSTANCE.exportAuditLogsToPDF(logsToExport, out);
				}
			}
		}
		
		return null;
	}
}
