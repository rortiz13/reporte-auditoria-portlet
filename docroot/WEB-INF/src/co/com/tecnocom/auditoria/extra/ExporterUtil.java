package co.com.tecnocom.auditoria.extra;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import la.netco.liferay.audit.model.AuditLog;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.liferay.portal.kernel.util.StringPool;

public enum ExporterUtil {
	INSTANCE;
	
	public Workbook exportAuditLogsToExcel(List<AuditLog> auditLogs) {
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("Reporte");
        
		addHeaders(sheet);
		generateTableRecords(sheet, auditLogs);
		
		//	Ajustar el tamaño de las celdas
		for(int columnNumber = 0; columnNumber <= 7; columnNumber++) {
			sheet.autoSizeColumn(columnNumber);
		}
		
    	return wb;
	}
	
	private void addHeaders(Object element) {
		//	Set headers based on param type
		if(element instanceof Sheet) {
			Row headerRow = ((Sheet)element).createRow(0);
	        headerRow.createCell(0).setCellValue(new HSSFRichTextString("Id Auditoria"));
	        headerRow.createCell(1).setCellValue(new HSSFRichTextString("Usuario Responsable"));
	        headerRow.createCell(2).setCellValue(new HSSFRichTextString("Comando"));
	        headerRow.createCell(3).setCellValue(new HSSFRichTextString("Id Recurso"));
	        headerRow.createCell(4).setCellValue(new HSSFRichTextString("Sitio"));
	        headerRow.createCell(5).setCellValue(new HSSFRichTextString("URL"));
	        headerRow.createCell(6).setCellValue(new HSSFRichTextString("IP Remota"));
	        headerRow.createCell(7).setCellValue(new HSSFRichTextString("Fecha Auditoria"));
		} else if(element instanceof PdfPTable) {
			PdfPTable table = (PdfPTable)element;
			
			table.addCell(new PdfPCell(new Phrase("Id Auditoria")));
			table.addCell(new PdfPCell(new Phrase("Usuario Responsable")));
			table.addCell(new PdfPCell(new Phrase("Comando")));
			table.addCell(new PdfPCell(new Phrase("Id Recurso")));
			table.addCell(new PdfPCell(new Phrase("Sitio")));
			table.addCell(new PdfPCell(new Phrase("URL")));
			table.addCell(new PdfPCell(new Phrase("IP Remota")));
			table.addCell(new PdfPCell(new Phrase("Fecha Auditoria")));
		}
	}
	
	private void generateTableRecords(Object element, List<AuditLog> auditLogs) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		
		if(element instanceof Sheet) {
			Sheet sheet = (Sheet)element;
			
			for (int i = 0; i < auditLogs.size(); i++) {
				AuditLog auditLog = auditLogs.get(i);
				
				Row contentRow = sheet.createRow(i+1);
				contentRow.createCell(0).setCellValue(new HSSFRichTextString(Long.valueOf(auditLog.getId()).toString()));
				contentRow.createCell(1).setCellValue(new HSSFRichTextString(AdditionalInfoUtil.INSTANCE.getUserInfo(auditLog.getUserID())));
				contentRow.createCell(2).setCellValue(new HSSFRichTextString(auditLog.getCommand()));
				contentRow.createCell(3).setCellValue(new HSSFRichTextString(auditLog.getResourceId()));
				contentRow.createCell(4).setCellValue(new HSSFRichTextString(AdditionalInfoUtil.INSTANCE.getGroupInfo(auditLog.getScopeGroupId())));
				contentRow.createCell(5).setCellValue(new HSSFRichTextString(auditLog.getLayoutURL()));
				contentRow.createCell(6).setCellValue(new HSSFRichTextString(auditLog.getRemoteAddr()));
				String dateValue = StringPool.BLANK;
				try { dateValue = sdf.format(auditLog.getFechaRegistro()); } catch (Exception ex) {}
				contentRow.createCell(7).setCellValue(new HSSFRichTextString(dateValue));
			}
		} else if(element instanceof PdfPTable) {
			PdfPTable table = (PdfPTable)element;
			for (int i = 0; i < auditLogs.size(); i++) {
				AuditLog auditLog = auditLogs.get(i);
				
				table.addCell(Long.valueOf(auditLog.getId()).toString());
				table.addCell(AdditionalInfoUtil.INSTANCE.getUserInfo(auditLog.getUserID()));
				table.addCell(auditLog.getCommand());
				table.addCell(auditLog.getResourceId());
				table.addCell(AdditionalInfoUtil.INSTANCE.getGroupInfo(auditLog.getScopeGroupId()));
				table.addCell(auditLog.getLayoutURL());
				table.addCell(auditLog.getRemoteAddr());
				String dateValue = StringPool.BLANK;
				try { dateValue = sdf.format(auditLog.getFechaRegistro()); } catch (Exception ex) {}
				table.addCell(dateValue);
			}
		}
	}
	
	public void exportAuditLogsToPDF(List<AuditLog> auditLogs, OutputStream out) {
		Document pdfDoc = new Document(PageSize.LETTER.rotate(), 50, 50, 50, 50);
		
		try {
			PdfWriter.getInstance(pdfDoc, out).setInitialLeading(20);
			pdfDoc.open();
			pdfDoc.add(createAuditTable(auditLogs));
			
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		pdfDoc.close();
	}
	
	private PdfPTable createAuditTable(List<AuditLog> auditLogs) {
		PdfPTable table = new PdfPTable(8);
		table.setSpacingAfter(20);
		table.setTotalWidth(720);
        table.setLockedWidth(true);
        
        addHeaders(table);
        generateTableRecords(table, auditLogs);
        
        return table;
	}
}
