import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

public class CSVParser {
	
	public CSVParser(){}
	
	public static JTable parse(File file) {
		
		Object[][] data = new String[100][100];
		
		int col = 0, row = 0, y = 0, x = 0, length;
		char[] token, olToken, nlToken;
		String nextToken;

		try {
			Scanner sc = new Scanner(file);
			sc.useDelimiter(",");
			
			while(sc.hasNext()){
				nextToken = sc.next();
				token = nextToken.toCharArray();
				length = nextToken.length();
				
				for(int i=0;i<length;i++){
					if (token[i] == '\n'){
						olToken = new char[i-1];
						nlToken = new char[length-i];
		
						for (int j=0;j<(i-1);j++){
							olToken[j] = token[j];
						}
						for (int j=(i+1);j<length;j++){
							nlToken[j-i-1] = token[j];
						}

						String s1 = new String(olToken);
						String s2 = new String(nlToken);
						
						data[y][x] = s1;
						x=0; y++;
						data[y][x] = s2;
						x++;
						break; 
					}
					
					else if (i == (length-1)){
						data[y][x] = nextToken;
						x++;
					}
					
					if (x>col) col = x;
					if (y>row) row = y;
				}		
			}
		}
		catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}		

		Object[] ColumnNames = new Object[col];
		for(int i = 0; i < col; i++){
			ColumnNames[i] = i;
		}
		
		return (new JTable(new DefaultTableModel(data, ColumnNames)));
	}
}
