//	nightori 2019
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class TXT2X584 {
	static final String IN_FILENAME = "in.txt";
	static final String OUT_FILENAME = "out.x584";
    static final int MAX_LENGTH = 1023;
	
    public static void main(String[] args) throws IOException {
		Commands cmd = new Commands();
		OutputStream os = new FileOutputStream(OUT_FILENAME);
		int written = 0;
		//writing "X584" header
		String[] header = {"58", "35", "38", "34"};
		for (String str : header){
			os.write(hexToByte(str));
		}
		BufferedReader br = new BufferedReader(new FileReader(IN_FILENAME));
		String row, line, comm;
		while ((row = br.readLine()) != null) {
			String[] tsv = row.split("\t");
			line = tsv[0];
			try {
				comm = tsv[1];
			} catch (ArrayIndexOutOfBoundsException e){
				comm = "";
			}
			//adding 64 means checking the ¬ıœ¿À” checkbox
			byte VhPALU = line.contains("œ=1") ? (byte)64 : 0;
			line = line.replaceAll(" \\(œ=.\\)", "");
			os.write(hexToByte(cmd.map.get(line)[0]));	//first byte
			os.write(hexToByte(cmd.map.get(line)[1])+VhPALU);	//second byte
			os.write(comm.length());	//third byte
			if (!comm.isEmpty()) {
				//comment bytes
				os.write(comm.getBytes(Charset.forName("cp1251")));
			}
			written++;
		}
		//writing [9A 00 00] until file length is reached
		for (; written<=MAX_LENGTH; written++){
			os.write(154);
			os.write(0);
			os.write(0);
		}
		os.close();
    }    
	
    public static byte hexToByte(String hex){
		int d = (Character.digit(hex.charAt(0), 16) << 4);
		d += Character.digit(hex.charAt(1), 16);
		return (byte)d;
    }
}