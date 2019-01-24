package com.android.sqlite.sql;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.text.TextUtils;

import com.android.sqlite.converter.ColumnConverterFactory;
import com.android.sqlite.table.Column;
import com.android.sqlite.table.Column.ColumnType;

/**
 * 数据库查询Where条件
 *
 * @author wangyang
 * 2014-7-16 上午11:27:01
 */
public class WhereBuilder {
	
	/**
	 * 条件集合
	 */
	private final List<String> whereItems;

	private WhereBuilder() {
		this.whereItems = new ArrayList<String>();
	}

	/**
	 * create new instance
	 * 
	 * @return
	 */
	public static WhereBuilder b() {
		return new WhereBuilder();
	}

	/**
	 * create new instance
	 * 
	 * @param columnName
	 * @param op
	 *            operator: "=","<","LIKE","IN","BETWEEN"...
	 * @param value
	 * @return
	 */
	public static WhereBuilder b(String columnName, String op, Object value) {
		WhereBuilder result = new WhereBuilder();
		result.appendCondition(null, columnName, op, value);
		return result;
	}

	/**
	 * add AND condition
	 * 
	 * @param columnName
	 * @param op
	 *            operator: "=","<","LIKE","IN","BETWEEN"...
	 * @param value
	 * @return
	 */
	public WhereBuilder and(String columnName, String op, Object value) {
		appendCondition(whereItems.size() == 0 ? null : "AND", columnName, op, value);
		return this;
	}

	/**
	 * add OR condition
	 * 
	 * @param columnName
	 * @param op
	 *            operator: "=","<","LIKE","IN","BETWEEN"...
	 * @param value
	 * @return
	 */
	public WhereBuilder or(String columnName, String op, Object value) {
		appendCondition(whereItems.size() == 0 ? null : "OR", columnName, op, value);
		return this;
	}
	
	/**
	 * 添加where条件表达式
	 *
	 * @author wangyang
	 * 2014-7-16 上午11:28:09
	 * @param expr
	 * @return
	 */
	public WhereBuilder expr(String expr) {
		whereItems.add(" " + expr);
		return this;
	}
	
	/**
	 * 添加where条件表达式
	 *
	 * @author wangyang
	 * 2014-7-16 上午11:28:28
	 * @param columnName    列名
     * @param op			操作符
     * @param value			数据值
	 * @return
	 */
	public WhereBuilder expr(String columnName, String op, Object value) {
		appendCondition(null, columnName, op, value);
		return this;
	}
	
	/**
	 * 查询条件数量
	 *
	 * @author wangyang
	 * 2014-7-16 上午11:29:00
	 * @return
	 */
	public int getWhereItemSize() {
		return whereItems.size();
	}
	
	/**
	 * 将Where对象转换为where条件sql
	 *
	 * @author wangyang
	 * 2014-7-16 上午11:29:14
	 * @return
	 */
	@Override
	public String toString() {
		if (whereItems.size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (String item : whereItems) {
			sb.append(item);
		}
		return sb.toString();
	}
	
	/**
	 * 添加where条件
	 *
	 * @author wangyang
	 * 2014-7-16 上午11:29:42
	 * @param conj
	 * @param columnName
	 * @param op
	 * @param value
	 */
	private void appendCondition(String conj, String columnName, String op, Object value) {
		StringBuilder sqlSb = new StringBuilder();

		if (whereItems.size() > 0) {
			sqlSb.append(" ");
		}

		// append conj
		if (!TextUtils.isEmpty(conj)) {
			sqlSb.append(conj + " ");
		}

		// append columnName
		sqlSb.append(columnName);

		// convert op
		if ("!=".equals(op)) {
			op = "<>";
		} else if ("==".equals(op)) {
			op = "=";
		}

		// append op & value
		if (value == null) {
			if ("=".equals(op)) {
				sqlSb.append(" IS NULL");
			} else if ("<>".equals(op)) {
				sqlSb.append(" IS NOT NULL");
			} else {
				sqlSb.append(" " + op + " NULL");
			}
		} else {
			sqlSb.append(" " + op + " ");

			if ("IN".equalsIgnoreCase(op)) {
				Iterable<?> items = null;
				if (value instanceof Iterable) {
					items = (Iterable<?>) value;
				} else if (value.getClass().isArray()) {
					ArrayList<Object> arrayList = new ArrayList<Object>();
					int len = Array.getLength(value);
					for (int i = 0; i < len; i++) {
						arrayList.add(Array.get(value, i));
					}
					items = arrayList;
				}
				if (items != null) {
					StringBuffer stringBuffer = new StringBuffer("(");
					for (Object item : items) {
						Object itemColValue = Column.convert2ColumnValue(item);
						if (ColumnType.TEXT.equals(ColumnConverterFactory.getColumnType(itemColValue.getClass()))) {
							String valueStr = itemColValue.toString();
							if (valueStr.indexOf('\'') != -1) { // convert
																// single
																// quotations
								valueStr = valueStr.replace("'", "''");
							}
							stringBuffer.append("'" + valueStr + "'");
						} else {
							stringBuffer.append(itemColValue);
						}
						stringBuffer.append(",");
					}
					stringBuffer.deleteCharAt(stringBuffer.length() - 1);
					stringBuffer.append(")");
					sqlSb.append(stringBuffer.toString());
				} else {
					throw new IllegalArgumentException("value must be an Array or an Iterable.");
				}
			} else if ("BETWEEN".equalsIgnoreCase(op)) {
				Iterable<?> items = null;
				if (value instanceof Iterable) {
					items = (Iterable<?>) value;
				} else if (value.getClass().isArray()) {
					ArrayList<Object> arrayList = new ArrayList<Object>();
					int len = Array.getLength(value);
					for (int i = 0; i < len; i++) {
						arrayList.add(Array.get(value, i));
					}
					items = arrayList;
				}
				if (items != null) {
					Iterator<?> iterator = items.iterator();
					if (!iterator.hasNext())
						throw new IllegalArgumentException("value must have tow items.");
					Object start = iterator.next();
					if (!iterator.hasNext())
						throw new IllegalArgumentException("value must have tow items.");
					Object end = iterator.next();

					Object startColValue = Column.convert2ColumnValue(start);
					Object endColValue = Column.convert2ColumnValue(end);

					if (ColumnType.TEXT.equals(ColumnConverterFactory.getColumnType(startColValue.getClass()))) {
						String startStr = startColValue.toString();
						if (startStr.indexOf('\'') != -1) { // convert single
															// quotations
							startStr = startStr.replace("'", "''");
						}
						String endStr = endColValue.toString();
						if (endStr.indexOf('\'') != -1) { // convert single
															// quotations
							endStr = endStr.replace("'", "''");
						}
						sqlSb.append("'" + startStr + "'");
						sqlSb.append(" AND ");
						sqlSb.append("'" + endStr + "'");
					} else {
						sqlSb.append(startColValue);
						sqlSb.append(" AND ");
						sqlSb.append(endColValue);
					}
				} else {
					throw new IllegalArgumentException("value must be an Array or an Iterable.");
				}
			} else {
				value = Column.convert2ColumnValue(value);
				if (ColumnType.TEXT.equals(ColumnConverterFactory.getColumnType(value.getClass()))) {
					String valueStr = value.toString();
					if (valueStr.indexOf('\'') != -1) { // convert single
														// quotations
						valueStr = valueStr.replace("'", "''");
					}
					sqlSb.append("'" + valueStr + "'");
				} else {
					sqlSb.append(value);
				}
			}
		}
		whereItems.add(sqlSb.toString());
	}
}
