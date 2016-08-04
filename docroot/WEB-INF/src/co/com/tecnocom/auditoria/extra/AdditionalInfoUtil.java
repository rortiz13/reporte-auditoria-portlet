package co.com.tecnocom.auditoria.extra;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import la.netco.liferay.audit.model.AuditLog;

import org.apache.log4j.Logger;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.liferay.portal.model.User;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

public enum AdditionalInfoUtil {
	INSTANCE;
	
	private static final String[] journalViewerFields = new String[]{"uuid", "plid", "companyId", "createDate", "privateLayout", "layoutId", "parentLayoutId", "title", "description", "keywords", "robots", "type", "typeSettings", "hidden", "friendlyURL", "iconImage", "iconImageId", "themeId", "colorSchemeId", "wapThemeId", "wapColorSchemeId", "css", "priority", "layoutPrototypeUuid", "layoutPrototypeLinkEnabled", "sourcePrototypeLayoutUuid"};
	private static final String[] pageFields = new String[]{"uuid", "companyId", "plid", "title", "description", "type", "privateLayout", "layoutId", "parentLayoutId", "keywords", "robots", "typeSettings", "hidden", "friendlyURL", "iconImage", "iconImageId", "themeId", "colorSchemeId", "wapThemeId", "wapColorSchemeId", "css", "priority", "layoutPrototypeUuid" , "layoutPrototypeLinkEnabled", "sourcePrototypeLayoutUuid"};
	private static final String[] journalFields = new String[]{"uuid", "id", "userId", "userName", "resourcePrimKey", "companyId", "title", "description", "content", "classNameId", "classPK", "version", "type", "structureId", "templateId", "layoutUuid", "expirationDate", "reviewDate", "indexable", "smallImage", "smallImageId", "status", "statusDate", "statusByUserId", "statusByUserName", "articleId", "displayDate", "smallImageURL"};
	private static final String[] organizationFields = new String[]{"uuid", "groupId", "companyId", "creatorUserId", "classNameId", "classPK", "parentGroupId", "liveGroupId", "description", "type", "typeSettings", "friendlyURL", "site", "active"};
	private static final String[] documentFields = new String[]{"uuid", "fileEntryId", "companyId", "userId", "userName", "versionUserId", "versionUserName", "repositoryId", "name", "mimeType", "description", "extraSettings", "fileEntryTypeId", "version", "extension", "size", "readCount", "smallImageId", "largeImageId", "custom1ImageId", "custom2ImageId"};
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
	
	private static final SimpleDateFormat strSdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss.S");
	private static final SimpleDateFormat strSdf2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
	
	private Logger _log = Logger.getLogger(getClass());
	
	public void processXmlData(AuditLog registroSelecionado) {
		String preferences = registroSelecionado.getPreferences();
		
		StringBuilder newPrefs = new StringBuilder();
		if(preferences != null && !preferences.isEmpty()) {
			switch (Long.valueOf(registroSelecionado.getIdAuditedResource()).intValue()) {
			case 1:	// Portlet Visor de Contenido
				preferences = removeSectionsFromXML(preferences, AdditionalInfoUtil.journalViewerFields);
				break;
				
			case 2: // Páginas
				preferences = removeSectionsFromXML(preferences, AdditionalInfoUtil.pageFields);
				break;
				
			case 3: // Artículos
				preferences = removeSectionsFromXML(preferences, AdditionalInfoUtil.journalFields);
				break;
				
			case 6: // Organizaciones
				preferences = removeSectionsFromXML(preferences, AdditionalInfoUtil.organizationFields);
				break;
				
			case 7: // Documentos
				preferences = removeSectionsFromXML(preferences, AdditionalInfoUtil.documentFields);
				break;
			}
			
			try {
				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				InputSource is = new InputSource();
			    is.setCharacterStream(new StringReader(preferences));
			    
			    Document doc = db.parse(is);
			    NodeList columns = doc.getElementsByTagName("column");
			    
				for (int i = 0; i < columns.getLength(); i++) {
					newPrefs.append("<tr>");

					Element element = (Element) columns.item(i);
					generateNodeInfo(element, newPrefs);
					
					newPrefs.append("</tr>");
				}
				
				newPrefs.append("<tr> <td class=\"columnLabelsDetalle\"></td></tr>");

			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("No preferences.");
		}
		
		registroSelecionado.setPreferences(newPrefs.toString());
	}
	
	private void generateNodeInfo(Element element, StringBuilder newPrefs) {
		NodeList columnNameNode = element.getElementsByTagName("column-name");
		Element columnNameElement = (Element) columnNameNode.item(0);
		String name = getCharacterDataFromElement(columnNameElement);
		
		NodeList columnValueNode = element.getElementsByTagName("column-value");
		Element columnValueElement = (Element) columnValueNode.item(0);
		String value = getCharacterDataFromElement(columnValueElement);
		
		if(name.equals("groupId")) {
			name = "Dueño del recurso";
			value = getGroupInfo(Long.valueOf(value));
		} else if(name.equals("createDate")) {
			name = "Fecha de Creación";
			try {
				value = sdf.format(strSdf.parse(value));
			} catch(Exception ex) {
				try {
					value = sdf.format(strSdf2.parse(value));
				} catch (Exception ex2) {
					_log.error("Error parsing Date: " + value);
					value = "";
				}
			}
		} else if(name.equals("modifiedDate")) {
			name = "Fecha de Modificación";
			try {
				value = sdf.format(strSdf.parse(value));
			} catch(Exception ex) {
				try {
					value = sdf.format(strSdf2.parse(value));
				} catch (Exception ex2) {
					_log.error("Error parsing Date: " + value);
					value = "";
				}
			}
		} else if(name.equals("name") || name.equals("urlTitle") || name.equals("title")) {
			name = "Nombre del Recurso";
			if(value.contains(" LFR_ORGANIZATION")) {
				value = value.replace(" LFR_ORGANIZATION", "");
			}
		} else if(name.equals("folderId")) {
			name = "Carpeta del Documento";
			if(value.equals("0")) {
				value = "Carpeta Raíz";
			} else {
				try {
					value = DLFolderLocalServiceUtil.getFolder(Long.valueOf(value)).getName();
				} catch (Exception ex) {}
			}
		}
		
		newPrefs.append("<td class=\"columnLabelsDetalle\">").append(name).append("</td>");
		newPrefs.append("<td class=\"columnDatosDetalle\">").append(value).append("</td>");
		
	}

	private String removeSectionsFromXML(String xml, String[] sectionsToRemove) {
		if(xml != null && !xml.isEmpty()) {
			for(String section : sectionsToRemove) {
				if(xml.contains("<column-name>" + section + "</column-name>")) {
					String part1 = xml.substring(0, xml.indexOf("<column><column-name>" + section + "</column-name>"));
					String part2 = xml.substring(xml.indexOf("<column><column-name>" + section + "</column-name>"), xml.length());
					
					String part3 = part2.substring(part2.indexOf("</column>") + 9, part2.length());
					xml = part1 + part3;
				}
			}
			
			return xml;
		}
		
		return "";
	}
	
	private String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";
	}

	public String getUserInfo(long userId) {
		try {
			User user = UserLocalServiceUtil.getUser(userId);
			return user.getFullName() + " (" + user.getScreenName() + ")"; 
		} catch (Exception ex) {}
		
		return Long.valueOf(userId).toString();
	}
	
	public String getGroupInfo(long groupId) {
		try {
			return GroupLocalServiceUtil.getGroup(groupId).getDescriptiveName() + " (" + groupId + ")";
		} catch (Exception ex) {}
		
		return Long.valueOf(groupId).toString();
	}
	
	
}
