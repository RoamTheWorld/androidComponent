package com.android.sqlite.simple.bean;

import java.util.HashSet;
import java.util.Set;

import com.android.utils.StringUtil;

public class Coloumn {

	public static Set<Class<?>> numbers;

	static {
		numbers = new HashSet<Class<?>>();
		numbers.add(byte.class);
		numbers.add(Byte.class);
		numbers.add(short.class);
		numbers.add(Short.class);
		numbers.add(int.class);
		numbers.add(Integer.class);
		numbers.add(long.class);
		numbers.add(Long.class);
		numbers.add(float.class);
		numbers.add(Float.class);
		numbers.add(double.class);
		numbers.add(Double.class);
	}

	/**
	 * 字段名
	 */
	private String coloumnName;

	/**
	 * 字段类型
	 */
	private Class<?> coloumnType;

	/**
	 * 数据值
	 */
	private Object value;

	public Coloumn(String coloumnName) {
		super();
		this.coloumnName = coloumnName;
	}

	public Coloumn(String coloumnName, Class<?> coloumnType) {
		super();
		this.coloumnName = coloumnName;
		this.coloumnType = coloumnType;
	}

	public Coloumn() {
		super();
	}

	public Coloumn(String coloumnName, Class<?> coloumnType, Object value) {
		super();
		this.coloumnName = coloumnName;
		this.coloumnType = coloumnType;
		this.value = value;
	}

	public Object getValue() {
		if (coloumnType == Boolean.class || coloumnType == boolean.class) {
			value = Boolean.parseBoolean(value.toString()) ? 1 : 0;
		}
		return value;
	}

	public Object getConvertValue() {
		if (coloumnType == byte.class ) {
			return Byte.parseByte(value.toString()) == 0 ? null : value;
		}
		
		if (coloumnType == int.class ) {
			return Integer.parseInt(value.toString()) == 0 ? null : value;
		}
		
		if (coloumnType == double.class) {
			return Double.parseDouble(value.toString()) == 0 ? null : value;
		}

		if (coloumnType == short.class) {
			return Short.parseShort(value.toString()) == 0 ? null : value;
		}

		if (coloumnType == long.class) {
			return Long.parseLong(value.toString()) == 0 ? null : value;
		}

		if (coloumnType == float.class) {
			return Float.parseFloat(value.toString()) == 0 ? null : value;
		}

		if (coloumnType == String.class || coloumnType == Character.class || coloumnType == char.class) {
			return StringUtil.isEmpty(value.toString()) ? null : value;
		}
		
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getColoumnName() {
		return coloumnName;
	}

	public void setColoumnName(String coloumnName) {
		this.coloumnName = coloumnName;
	}

	public Class<?> getColoumnType() {
		return coloumnType;
	}

	public void setColoumnType(Class<?> coloumnType) {
		this.coloumnType = coloumnType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coloumnName == null) ? 0 : coloumnName.hashCode());
		result = prime * result + ((coloumnType == null) ? 0 : coloumnType.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coloumn other = (Coloumn) obj;
		if (coloumnName == null) {
			if (other.coloumnName != null)
				return false;
		} else if (!coloumnName.equals(other.coloumnName))
			return false;
		if (coloumnType == null) {
			if (other.coloumnType != null)
				return false;
		} else if (!coloumnType.equals(other.coloumnType))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public boolean isNumber() {
		return numbers.contains(coloumnType);
	}
}
