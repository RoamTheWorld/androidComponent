package com.android.sqlite.example;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;

import com.android.sqlite.BaseDAO;
import com.android.sqlite.FinderLazyLoader;
import com.android.sqlite.ForeignLazyLoader;

public class Test extends AndroidTestCase {

	public void test() {
		long startTime = System.currentTimeMillis();
		// testFinderLazyLoader();
		// testForeginLazyLoader();
		// testMany2Many();
		// BaseDAO<Department> dao = new BaseDAO<Department>(getContext(),
		// "test.db"){};
		// dao.setRecursion(true);
		// dao.setGenerateRecursion(true);
		// dao.delete(new Department("研发部","3"));
		BaseDAO<ChatGroup> dao = new BaseDAO<ChatGroup>(getContext(), "test.db") {
		};
		dao.setRecursion(true);
		dao.setGenerateRecursion(true);
		dao.delete(new ChatGroup("开发部"));
		long endTime = System.currentTimeMillis();
		System.out.println("一共耗时====" + (endTime - startTime));
	}

	public void testFinderLazyLoader() {
		long startTime = System.currentTimeMillis();

		BaseDAO<Department> dao = new BaseDAO<Department>(getContext(), "test.db") {
		};
		dao.beginTransaction();
		dao.setRecursion(true);
		dao.setGenerateRecursion(true);

		List<Department> list = new ArrayList<Department>();
		list.add(new Department("研发部", "7"));
		list.add(new Department("开发部", "8"));
		// dao.getDb().saveOrUpdate(list);

		List<User> users = new ArrayList<User>();
		for (int i = 1; i <= 50; i++) {
			User user = new User("bid" + i, "关羽" + i, "男" + i, "云长" + i, "123456" + i);
			int j = i % 2;
			System.out.println("--------------------------" + j);
			// user.departId = new
			// ForeignLazyLoader<Department>(User.class,"departId",list.get(j),dao.isRecursion());
			// user.departId = list.get(j);
			// user.departId = list;
			users.add(user);
		}
		list.get(0).users = new FinderLazyLoader<User>(Department.class, "dId", users);
		// list.get(0).users = users;
		dao.saveOrUpdate(list);

		// dao.delete(list.get(0));

		list = dao.list(new Department("研发部"));
		System.out.println(list);

		System.out.println(list.get(0).users.getAll2List());
		System.out.println(list.get(0).users.getAll2List().get(0).departId.getObject());
		System.out.println(list.get(0).users.getAll2List().get(0).departId.getObject().users.getAll2List());
		System.out.println(list.get(0).users.getAll2List().get(0).departId.getObject().users.getAll2List().get(0).departId.getObject());
		// System.out.println(list.get(0).users);

		// System.out.println(users.get(0).departId);
		long endTime = System.currentTimeMillis();
		System.out.println("一共耗时====" + (endTime - startTime));
	}

	public void testForeginLazyLoader() {
		long startTime = System.currentTimeMillis();

		BaseDAO<User> dao = new BaseDAO<User>(getContext(), "test.db") {
		};
		dao.setRecursion(true);
		dao.setGenerateRecursion(true);
		List<Department> list = new ArrayList<Department>();
		list.add(new Department("研发部", "9"));
		list.add(new Department("开发部", "10"));
		// dao.getDb().saveOrUpdate(list);

		List<User> users = new ArrayList<User>();
		for (int i = 1; i <= 50; i++) {
			User user = new User("bid" + i, "关羽" + i, "男" + i, "云长" + i, "123456" + i);
			int j = i % 2;
			System.out.println("--------------------------" + j);
			user.departId = new ForeignLazyLoader<Department>(User.class, "departId", list.get(j), dao.isGenerateRecursion());
			// user.departId = list;
			users.add(user);
		}
		dao.saveOrUpdate(users);

		users = dao.list(new User(1));
		System.out.println(users);

		System.out.println(users.get(0).departId.getObject());
		System.out.println(users.get(0).departId.getObject().users.getObject());
		System.out.println(users.get(0).departId.getObject().users.getObject().departId.getObject());
		System.out.println(users.get(0).departId.getObject().users.getObject().departId.getObject().users.getAll2List());
		long endTime = System.currentTimeMillis();
		System.out.println("一共耗时====" + (endTime - startTime));
	}

	public void testMany2Many() {
		long startTime = System.currentTimeMillis();

		BaseDAO<ChatGroup> dao = new BaseDAO<ChatGroup>(getContext(), "test.db") {
		};
		dao.setRecursion(true);
		dao.setGenerateRecursion(true);

		List<ChatGroup> list = new ArrayList<ChatGroup>();
		list.add(new ChatGroup("11", "研发部"));
		list.add(new ChatGroup("12", "开发部"));
		// dao.getDb().saveOrUpdate(list);

		List<ChatUser> users1 = new ArrayList<ChatUser>();
		List<ChatUser> users2 = new ArrayList<ChatUser>();
		for (int i = 1; i <= 50; i++) {
			ChatUser user = new ChatUser("bid" + i, "关羽" + i, "男" + i, "123456" + i, "云长" + i);
			if (i % 2 == 0)
				users1.add(user);
			else
				users2.add(user);
		}
		list.get(0).users = new FinderLazyLoader<ChatUser>(ChatGroup.class, "uid", users1);
		list.get(1).users = new FinderLazyLoader<ChatUser>(ChatGroup.class, "uid", users2);
		// list.get(0).users = users;
		dao.saveOrUpdate(list);

		// dao.delete(list.get(0));

		list = dao.list(new ChatGroup("研发部"));
		System.out.println(list);

		List<ChatUser> us = list.get(0).users.getAll2List();
		System.out.println(us);
		System.out.println(us.get(0).bId.getObject());
		// System.out.println(list.get(0).users.getAll2List().get(0).group.getObject());
		// System.out.println(list.get(0).users);

		// System.out.println(users.get(0).departId);
		long endTime = System.currentTimeMillis();
		System.out.println("一共耗时====" + (endTime - startTime));
	}

	public void testForeginLazyLoaderWithSuperClass() {
		long startTime = System.currentTimeMillis();

		BaseDAO<UserA> dao = new BaseDAO<UserA>(getContext(), "test.db") {
		};
		dao.setRecursion(false);
		dao.setGenerateRecursion(true);
		List<Group> list = new ArrayList<Group>();
		list.add(new Group("1","开发部"));
		list.add(new Group("2", "研发部"));
		// dao.getDb().saveOrUpdate(list);

		List<UserA> users = new ArrayList<UserA>();
		for (int i = 1; i <= 50; i++) {
			UserA user = new UserA("uid" + i, "关羽" + i,"password"+i, "男" + i, "icon" + i);
			int j = i % 2;
			System.out.println("--------------------------" + j);
			//user.group = new ForeignLazyLoader<Group>(UserA.class, "groupId", list.get(j), dao.isGenerateRecursion());
			user.setGroup(new ForeignLazyLoader<Group>(UserA.class, "groupId", list.get(j), dao.isGenerateRecursion()));
			// user.departId = list;
			users.add(user);
		}
		dao.saveOrUpdate(users);

		users = dao.list(new UserA(1));
		System.out.println(users);

		System.out.println(users.get(0).group.getObject());
		System.out.println(users.get(0).group.getObject().users);
//		System.out.println(users.get(0).group.getObject().users.getObject().departId.getObject());
//		System.out.println(users.get(0).group.getObject().users.getObject().departId.getObject().users.getAll2List());
		long endTime = System.currentTimeMillis();
		System.out.println("一共耗时====" + (endTime - startTime));
	}
}
