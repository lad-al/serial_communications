package networks_project;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import ithakimodem.Modem;


public class Echo {

	public void echo(String c, String f, double m) {

		String filename = f;
		long loopTime = (long) (m*60000);
		int k;
		long packageNumber = 0;
		String request = c;
		String response = " ";
		Modem modem;
		modem=new Modem();
		modem.setSpeed(80000);
		modem.setTimeout(6000);
		modem.open("ithaki");
		
		long startTime = 0;
		long curTime = 0;
		long packageTime = 0;
		long startFourMinuteLoop = System.currentTimeMillis();
		boolean conClosed = false;
		
		Writer writer = null;

		//generate txt file
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(filename), "utf-8"));
		    writer.write("Echo data with request code " + request);
		    ((BufferedWriter) writer).newLine();
		    ((BufferedWriter) writer).newLine();
		    
		    //4 minute loop
			do {
				
				//if k=-1 don't repeat
				if (conClosed == true) {
					break;
				}
				
				modem.write(request.getBytes());
				startTime = System.currentTimeMillis();
				
				response = " ";
				
				for (;;) {
					try {
						
						k=modem.read();
						System.out.print((char)k);
						if (k==-1) {
							System.out.println("Connection closed");
							conClosed = true;
							break;
						}
						response += (char)k;				
						if(response.indexOf("PSTOP")>0) {
							
							packageTime = System.currentTimeMillis() - startTime;
							packageNumber += 1;
							writer.write(packageTime + "");
						    ((BufferedWriter) writer).newLine();
							break;				
						}
					} catch (Exception x) {
						System.out.println(x);
						break;
					  }
				}

				curTime = System.currentTimeMillis();
				
			} while (curTime <= (loopTime+startFourMinuteLoop));
		    
			
		    ((BufferedWriter) writer).newLine();
		    writer.write("Number of packages recieved in 4 minutes: " + packageNumber);
		} catch (IOException ex) {
		} finally {
		   try {writer.close();} catch (Exception ex) {}
		}
		
		modem.close();
		System.out.println(filename + " created!");
	}
	
}
