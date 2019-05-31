package edu.uw.nrs.exchange;

import java.net.UnknownHostException;

public class TestStuff {

	public static void main(String[] args) {
		
		ExchangeNetworkAdapter ena = null;
		
		try {
			System.out.println("kick 1");
			ena = new ExchangeNetworkAdapter(null, "230.0.0.1", 4446, 123);
			Thread.sleep(1000);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("kick 2");
		ExchangeNetworkProxy enp = new ExchangeNetworkProxy("230.0.0.1", 4446, "eeee", 123);
//		enp.run();
		
	
		
		
	}

}
