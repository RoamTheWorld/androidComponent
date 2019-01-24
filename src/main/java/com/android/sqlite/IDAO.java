package com.android.sqlite;

import java.util.List;

/**
 * 
 * @author wangyang
 * @param <M>
 *            一些对数据库的公共接口,如增删改查
 */
public interface IDAO<M> {

	/**
	 * 
	 * @author wangyang
	 * @param m
	 * @return boolean 将传入的对象保存至数据库,并以boolean类型返回结果
	 */
	public boolean saveOrUpdate(M m);
	
	/**
	 * 
	 * @author wangyang
	 * @param m
	 * @return boolean 将传入的对象保存至数据库,并以boolean类型返回结果
	 */
	public boolean saveOrUpdate(List<M> m);

	/**
	 * 
	 * @author wangyang
	 * @param m
	 * @return boolean 根据传入的对象修改数据库中数据,并以boolean类型返回结果
	 */
	public boolean delete(M m);

	/**
	 * 
	 * @author wangyang
	 * @param m
	 * @return boolean 根据传入的对象删除数据库中数据,并以boolean类型返回结果
	 */
	public boolean delete(List<M> m);

	/**
	 * 
	 * @author wangyang
	 * @param m
	 * @param orderSql
	 * @param havingSql
	 * @return List 根据传入的String参数,SQL语句,来获取对应数据库的数据并以泛型集合返回
	 */
	public List<M> listBySql(String sql,String...args);
	
	/**
	 * 
	 * @author wangyang
	 * @return 根据泛型获取数据库对象并以泛型集合返回
	 */
	public List<M> list(M m);
	
	/**
	 * 
	 * @author wangyang
	 * @param m
	 * @param orderSql
	 * @param havingSql
	 * @return List 根据传入的String参数,SQL语句,来获取对应数据库的数据并以泛型返回
	 */
	public M queryBySql(String sql,String...args);
	
	/**
	 * 
	 * @author wangyang
	 * @return 根据泛型获取数据库对象并以泛型返回
	 */
	public M query(M m);
}
