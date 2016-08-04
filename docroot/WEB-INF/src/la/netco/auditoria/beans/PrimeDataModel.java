package la.netco.auditoria.beans;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import la.netco.liferay.audit.service.AuditLogLocalServiceUtil;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Order;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public abstract class PrimeDataModel<T, PK extends Serializable> extends LazyDataModel<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private Class<T> entityClass;
	private List<T> data;
	private Order orderBy;
	private List<Criterion> filtros;

	public PrimeDataModel(Class<T> entityClass) {
		super();
		this.entityClass = entityClass;
	}
	
	@SuppressWarnings("unchecked")
	public List<T> getDisplayedLogs() {
		List<T> queryResult = new LinkedList<T>();
		try {

			DynamicQuery dynamicQuery = AuditLogLocalServiceUtil.dynamicQuery();
			for (Criterion filtro : filtros) {
				dynamicQuery.add(filtro);
			}

			dynamicQuery.addOrder(OrderFactoryUtil.desc("fechaRegistro"));
			queryResult = (List<T>) AuditLogLocalServiceUtil.dynamicQuery(dynamicQuery, 0, 1000);

		} catch (SystemException e) {
			_log.error("Error leyendo AuditLogLocalServiceUtil ", e);
		}

		return queryResult;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
		try {

			DynamicQuery dynamicQuery = AuditLogLocalServiceUtil.dynamicQuery();
			for (Criterion filtro : filtros) {
				dynamicQuery.add(filtro);
			}

			rowCount = AuditLogLocalServiceUtil.dynamicQueryCount(dynamicQuery);

			dynamicQuery = AuditLogLocalServiceUtil.dynamicQuery();
			for (Criterion filtro : filtros) {
				dynamicQuery.add(filtro);
			}

			dynamicQuery.addOrder(OrderFactoryUtil.desc("id"));

			data = (List<T>) AuditLogLocalServiceUtil.dynamicQuery(dynamicQuery, first, first + pageSize);

		} catch (SystemException e) {
			_log.error("Error leyendo AuditLogLocalServiceUtil ", e);
		}

		setPageSize(pageSize);
		return data;
	}

	private Long rowCount = null;

	@Override
	public int getRowCount() {

		int count = 0;

		try {
			if (rowCount == null) {
				DynamicQuery dynamicQuery = AuditLogLocalServiceUtil.dynamicQuery();
				for (Criterion filtro : filtros) {
					dynamicQuery.add(filtro);
				}
				rowCount = AuditLogLocalServiceUtil.dynamicQueryCount(dynamicQuery);
				count = rowCount.intValue();
			} else {
				count = rowCount.intValue();
			}
		} catch (SystemException e) {
			_log.error("Error leyendo AuditLogLocalServiceUtil ", e);
		}
		return count;
	}

	@Override
	public Object getRowKey(T object) {
		return getId(object);
	}

	@Override
	public T getRowData(String rowKey) {
		for (T object : data) {
			if (getId(object).equals(rowKey))
				return object;
		}

		return null;
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	protected abstract Object getId(T t);

	public Order getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Order orderBy) {
		this.orderBy = orderBy;
	}

	public List<Criterion> getFiltros() {
		return filtros;
	}

	public void setFiltros(List<Criterion> filtros) {
		this.filtros = filtros;
	}

	private static Log _log = LogFactoryUtil.getLog(PrimeDataModel.class);

}
