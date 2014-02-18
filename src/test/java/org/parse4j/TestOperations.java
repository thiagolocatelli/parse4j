package org.parse4j;


public class TestOperations {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ParseObject po = new ParseObject("products");
		System.out.println(po.getEndPoint());
		
		po.put("amountInt", 1);
		po.increment("amountInt", 10);
		int amountInt = po.getInt("amountInt");
		System.out.println("amountInt: " + amountInt);
		
		po.put("amountLong", 100l);
		po.increment("amountLong", 10l);
		long amountLong = po.getLong("amountLong");
		System.out.println("amountLong: " + amountLong);
		
		po.put("amountDouble", 5.6);
		po.increment("amountDouble", 3.7);
		double amountDouble = po.getDouble("amountDouble");
		System.out.println("amountDouble: " + amountDouble);
		
		po.remove("amountLong");
		po.remove("amountInt");
		po.remove("amountDouble");
		
		po.put("nome", "teste");
		po.increment("nome");

		System.out.println("end");
		
	}

}
