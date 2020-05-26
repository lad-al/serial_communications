package networks_project;

import ithakimodem.Modem;


public class Gps {

	public String gps(String c, String s, String n){
		
		int k;
		int j=0;
		String request = c + "R=1" + s + n + "\r";
		String response = " ";
		String endRequest = c;
		int packagesIgnored = 19;
		
		Modem modem;
		modem=new Modem();
		modem.setSpeed(80000);
		modem.setTimeout(6000);
		modem.open("ithaki");
		modem.write(request.getBytes());
		
		for (;;) {
			try {
				
				k=modem.read();
				if (k==-1) {
//					System.out.println("Connection closed");
					break;
				}
				response += (char)k;
				if(response.indexOf("START ITHAKI GPS TRACKING\r\n")>0) {
					for (;;) {
						k=modem.read();
						response += (char)k;
						
						if(response.indexOf("$GPGGA")>0){
							
							endRequest += "T=";
							String length = "";
							String lengthTime = "";
							double lengthTimeDouble = 0;
							String width = "";
							String widthTime = "";
							double widthTimeDouble = 0;
							response = " ";
							//get length
							for (int i=0; i<12 ; i++) {
								k=modem.read();
							}
							for (int i=0; i<4; i++) {
								k=modem.read();
								length += (char)k;
							}
							k=modem.read();
							for (int i=0; i<4 ; i++) {
								k=modem.read();
								lengthTime += (char)k;
							}
							lengthTimeDouble = (double)Integer.valueOf(lengthTime)*0.006;
							lengthTime = String.valueOf((int) Math.round(lengthTimeDouble));
							length += lengthTime;
							//get width
							for(int i=0; i<4; i++) {
								k=modem.read();
							}
							for (int i=0; i<4; i++) {
								k=modem.read();
								width += (char)k;
							}
							k=modem.read();
							for (int i=0; i<4 ; i++) {
								k=modem.read();
								widthTime += (char)k;
							}
							widthTimeDouble = (double)Integer.valueOf(widthTime)*0.006;
							widthTime = String.valueOf((int) Math.round(widthTimeDouble));
							width += widthTime;
							endRequest += width + length;
							//ignore 19 packets
							response = " ";
							for (;;) {
								k=modem.read();
								response += (char)k;
								if(response.indexOf("$GPGGA")>0) { 
									j++;
									response = " ";
								}
								if (j==packagesIgnored) {
									j=0;
									break;
								}
							}
						}
						
						if(response.indexOf("STOP ITHAKI GPS TRACKING\r\n")>0) {
							endRequest += "\r";
							break;
						}
					}
					
				}
				


				
			} catch (Exception x) {
				System.out.println(x);
				break;
			  }
		}
		
		modem.close();


		return endRequest;
		
	}
	
}
