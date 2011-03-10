import java.io.IOException;

import com.sarxos.ow.OWService;


public class OWServerRunner {

	public static void main(String[] args) {
		
		OWService service;
		try {
			System.out.println("Running...");
			service = new OWService();
			service.startService();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//service.stopService();
	}
	
}
