package networks_project;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import ithakimodem.Modem;

public class Arq {

	public void arq(String a, String n, double m, String f) {

		String filename = f;
		long loopTime = (long) (m*60000);
		int k;
		int xor = 0;
		int[] message = new int[16];
		String fcs = "";
		
		String ack = a + "\r";
		String nack = n + "\r";
		String response = "";
		Modem modem;
		modem=new Modem();
		modem.setSpeed(80000);
		modem.setTimeout(6000);
		modem.open("ithaki");
		
		long packageNumber = 0;
		long startTime = 0;
		long curTime = 0;
		long packageTime = 0;
		long startFourMinuteLoop = System.currentTimeMillis();
		boolean conClosed = false;
		int nacksSent = 0;
		int numOfWrongMessages = 0;
		ArrayList<Integer> numOfWrongMesList = new ArrayList<>();
		 
		Writer writer = null;

		//generate txt file
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"));
			writer.write("Arq data with ACK result code " + ack + " and NACK result code " + nack);
			((BufferedWriter) writer).newLine();
			((BufferedWriter) writer).newLine();
		 
		 modem.write(ack.getBytes());
		 startTime = System.currentTimeMillis();
		 
		 do {
			 
			//if k=-1 don't repeat
				if (conClosed == true) {
					break;
				}
				
			 
			 for(;;) {
				 try {
					 k=modem.read();
					 response += (char)k;
					 
					 if (k==-1) {
						 System.out.println("Connection closed");
						 conClosed = true;
						 break;
					 }
					 //get useful data
					 if(response.indexOf("PSTART")>0) {
						 for (;;) {
							 k=modem.read();
							 response += (char)k;
							 if(response.indexOf("<")>0) {
								 for (int i=0; i<16; i++) {
									 k=modem.read();
									 message[i]=k;
								 }
								 k=modem.read();
								 k=modem.read();
								 for (int i=0; i<3; i++) {
									 k=modem.read();
									 fcs += (char)k;
								 }
								 for (int i=0; i<16; i++) {
								 }
								 
								 xor = message[0];
								 for (int i=1; i<16; i++) {
									 xor ^= message[i];
								 }
								 response = "";
								 break;
							 }
						 }
					 }
					 
					 if(response.indexOf("PSTOP")>0) {
						 response = " ";
						 Arrays.fill(message, 0);
						 
						 //check if the package is correct
						 if(xor == Integer.valueOf(fcs)) {
							 packageNumber++;
							 packageTime = System.currentTimeMillis() - startTime;
							 writer.write(packageTime + "");
							 ((BufferedWriter) writer).newLine();
							 numOfWrongMesList.add(numOfWrongMessages);
							 numOfWrongMessages = 0;
							 modem.write(ack.getBytes());
							 fcs = "";
							 startTime = System.currentTimeMillis();
							 break;
						 }else {
							 modem.write(nack.getBytes());
							 nacksSent++;
							 numOfWrongMessages++;
							 fcs = "";
							 break;
						 }
						 
					 }
					 
				 } catch (Exception x) {
					 System.out.println(x);
					 break;
				   }
			 }
			 curTime = System.currentTimeMillis();
			 
		 }while (curTime <= (loopTime+startFourMinuteLoop));
		 
		 //use a hashmap to get the number of packages that needed x tries sorted.
		 HashMap<Integer, Integer> counts = new HashMap<Integer, Integer>();
		 for(int i=0; i<numOfWrongMesList.size(); ++i){
		    Integer x = counts.get(numOfWrongMesList.get(i));
		    if (x==null) counts.put(numOfWrongMesList.get(i), 1);
		    else counts.put(numOfWrongMesList.get(i), x+1);
		}
		 
		 ((BufferedWriter) writer).newLine();
		 writer.write("number of packages " + packageNumber);
		 ((BufferedWriter) writer).newLine();
		 ((BufferedWriter) writer).newLine();
		 writer.write("number of nacks sent " + nacksSent);
		 ((BufferedWriter) writer).newLine();
		 ((BufferedWriter) writer).newLine();
		 
		 for (int i=0; i<counts.size(); i++) {
			 ((BufferedWriter) writer).newLine();
			 writer.write("packages with " + i + " tries: " + counts.get(i));
		 }
		 
			} catch (IOException ex) {
			} finally {
			   try {writer.close();} catch (Exception ex) {}
			}
			
			System.out.println(f + " created");
	}
	
	
}
