package networks_project;


public class VirtualModem{

	public static void main (String[] param){
		
		// set (String code, String filename, loop time (in minutes))
		Echo e = new Echo();
		e.echo("E8935\r", "echo.txt", 0.5);
		
		// set (String code, String filename)
		Image i = new Image();
		i.image("M2881\r", "image_error_free.jpg");
		i.image("G1812\r", "image_with_errors.jpg");
		
		// set (String code, String startOfRoute(4 digits), String numberOfSamples(2 digits))
		Gps g = new Gps();
		String gpsCode = g.gps("P5148", "0001", "80");
		i.image(gpsCode, "gps.jpg");
		
		// set (String ack code,, String nack code, loop time (in minutes), String filename)
		Arq a = new Arq();
		a.arq("Q2243", "R9714", 4, "arq.txt");
		
	}
}
