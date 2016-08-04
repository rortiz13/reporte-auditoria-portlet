package la.netco.auditoria.primefaces;

import javax.faces.FacesException;

//import org.primefaces.component.export.CSVExporter;
import org.primefaces.component.export.ExporterType;
//import org.primefaces.component.export.PDFExporter;
//import org.primefaces.component.export.XMLExporter;

public class ExporterFactory {
	public static Exporter getExporterForType(String type) {
		Exporter exporter = null;

		try {
			ExporterType exporterType = ExporterType
					.valueOf(type.toUpperCase());

			switch (exporterType) {
			case XLS:
				exporter = new ExcelExporter();
				break;

			case PDF:
				//exporter = new PDFExporter();
				break;

			case CSV:
				//exporter = new CSVExporter();
				break;

			case XML:
				//exporter = new XMLExporter();
				break;
			}
		} catch (IllegalArgumentException e) {
			throw new FacesException(e);
		}

		return exporter;
	}
}
