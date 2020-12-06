package networks_project;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import ithakimodem.Modem;

public class Image {

	public void image(String c, String f) {

		try {
		
			 String filename = f;
			 int k;
			 String request = c;
			 String response = " ";
			 Modem modem;
			 modem=new Modem();
			 modem.setSpeed(80000);
			 modem.setTimeout(6000);
			 modem.open("ithaki");
			 modem.write(request.getBytes());
		     ArrayList<Byte> bytesList = new ArrayList<Byte>(); 
	
				 for (;;) {
					 try {
						 k=modem.read();
						 System.out.print(k);
						 
						 if (k==-1) {
							 System.out.println("Connection closed");
							 break;
						 }
						 response += Integer.toHexString(k) + "_";
	
	
						 if(response.indexOf("ff_")>0) {
							 bytesList.add((byte)k);
						     k=modem.read();
						     response += Integer.toHexString(k) + "_";
						     if(response.indexOf("d8_")>0) {
						    	 bytesList.add((byte)k);
						    	 break;
						     }else {
						    	 bytesList.clear();
						     }
						 }
	
						 
					 } catch (Exception x) {
						 System.out.println(x);
						 break;
					   }
				 }

				 for (;;) {
					 try {
						 k=modem.read();
						 bytesList.add((byte)k);
						 response += Integer.toHexString(k) + "_";
						 
						 
						 if(response.indexOf("ff_d9_")>0) {
						    	 break;
						 }
	
						 
					 } catch (Exception x) {
						 System.out.println(x);
						 break;
					   }
				 }
				 
				 byte[] arrayOfBytes = new byte[bytesList.size()];
				 for (int i=0; i<bytesList.size(); i++) {
					 arrayOfBytes[i] = bytesList.get(i);
				 }
				 
				
				 ByteArrayInputStream bis = new ByteArrayInputStream(arrayOfBytes);
			     BufferedImage bImage = ImageIO.read(bis);
			     ImageIO.write(bImage, "jpg", new File(filename) );
			     System.out.println(filename + " created");
			     
				 modem.close();

			     
		} catch (Exception x) {
			 System.out.println(x);
		   }

	}
	
		
	
}
