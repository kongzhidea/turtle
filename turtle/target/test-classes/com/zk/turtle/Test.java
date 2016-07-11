package com.zk.turtle;

import com.zk.turtle.model.TurtleGroup;
import com.zk.turtle.service.ConfigureFactory;
import com.zk.turtle.service.ConfigureLoader;
import com.zk.turtle.service.ConfigureManager;
import com.zk.turtle.service.impl.ZKConfigureImpl;

public class Test {
	public static void main(String[] args) throws Exception {
		testCreate();
		testGet();
	}

	private static void testCreate() {
		ConfigureManager manager = ConfigureFactory.getManager();
		manager.set(TurtleGroup.KK, "st", "1,2,3,4,5");
		// manager.remove(TurtleGroup.KK, "test");
	}

	private static void testGet() throws InterruptedException {
		ConfigureLoader client = ConfigureFactory.getLoader();
		while(true) {
			System.out.println(client.get(TurtleGroup.KK, "st"));
			System.out.println(client.get(TurtleGroup.TURTLE, "st"));
			System.out.println(client.get(TurtleGroup.UNIT_TEST, "st"));
			System.out.println(((ZKConfigureImpl) client).getAll());
			Thread.sleep(5000);
		}
	}
}
