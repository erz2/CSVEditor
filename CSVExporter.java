import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class CSVExporter {

	public static void export(File destination, TableModel docModel) {
		int col = docModel.getColumnCount(), row = docModel.getRowCount();
		String strFilePath = destination.getPath(), content="";
		
		if (!strFilePath.endsWith(".csv")) strFilePath = destination.getPath() + ".csv";
	    try {
	    	FileOutputStream fos = new FileOutputStream(strFilePath, false);
	    	PrintStream ps = new PrintStream(fos);
			for (int r=0; r<row; r++) {
				for (int c=0; c<col; c++) {
					if (c==(col-1)) {	
						String token = (String)docModel.getValueAt(r, c);
						if(token == null){
							token = "";
						}
						content = "" + content + "" + token + "";
						ps.print(content);
						ps.println();
						content= "";
					}
					else {
						String token = (String)docModel.getValueAt(r, c);
						if(token == "null"){
							token = "";
						}
						content = "" + content + "" + token + ",";
					}
				}
			}
	     
	        fos.write(content.getBytes());
	        ps.close();
	        fos.close();
	    }
	    
	    catch(FileNotFoundException ex) {
	    	System.out.println("FileNotFoundException : " + ex);
	    }
	    
	    catch(IOException ioe) {
	    	System.out.println("IOException : " + ioe);
	    }		
	}
}
