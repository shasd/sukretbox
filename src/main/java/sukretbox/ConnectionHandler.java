package sukretbox;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionHandler implements Runnable {
	private static final byte[] terminator = {'1','2','3','4'};
	private InputStream input;
	private DataStore data;
    public ConnectionHandler(Socket connection, DataStore data) throws IOException {
        input = connection.getInputStream();
        this.data = data;
    }

	public void run() {
		ArrayList<Byte> terminatorList = new ArrayList<Byte>();
		for(byte b : terminator) terminatorList.add(b);
		StringBuilder sb = new StringBuilder();
		while(true) {
			int b;
			try {
				b = input.read();
				if(b == ' ')
					break;
				sb.append((char) b);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} 
		String name = sb.toString();
		System.out.println("connection with name: " + name);
		ArrayList<Byte> inputFile = new ArrayList<Byte>();
        
        while(inputFile.size() < terminator.length 
           || !inputFile.subList(inputFile.size()-terminatorList.size(), inputFile.size())
           .equals(terminatorList)) {
             
            try {
            	byte inp = (byte) input.read();
				inputFile.add(inp);
			} catch (IOException e) {
			    e.printStackTrace();
			}	
            
        }
        //data.storeData(name, inputFile.toArray(new Byte[1]));
    }
}
