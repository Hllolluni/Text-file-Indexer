import java.awt.Desktop;
import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class FileIndexer {

	File folder = null;
	Map<Integer,File> list;
	Map<String, ArrayList<Integer>> tokens;
	int n = 1;
	
	public FileIndexer(String path) throws IOException{
		list = new LinkedHashMap<Integer,File>();
		tokens = new LinkedHashMap<String,ArrayList<Integer>>();
		
		folder = new File(path);
		if (!folder.exists()) {
			throw new FileNotFoundException("\"" + folder + "\" does not exist!");
		}
		indexTextFiles(folder);

	}
	
	public void indexTextFiles(File f) throws IOException, NullPointerException {
		if(list.containsValue(f)) {
			return;
		}
	
		 if(f.isFile()&& 
				(f.getName().endsWith(".txt") || f.getName().endsWith(".doc") || f.getName().endsWith(".docx"))) {
				list.put(n, f);
				tokenize(f.getPath(),n);
				n++;
				return;
		}

		File [] files = f.listFiles();
		
		for(File file : files) {
			if(file.isFile() && 
				(file.getName().endsWith(".txt") || file.getName().endsWith(".doc")	|| file.getName().endsWith(".docx"))) {
				list.put(n, file);
				tokenize(file.getPath(),n);
				n++;
			}else if(file.isDirectory()) {
				indexTextFiles(file);
			}else {
				continue;
			}
		}

	}
		
	public boolean search(String word) {
		if(tokens.containsKey(word)) {
			for(int i = 0; i < tokens.get(word).size(); i++) {
				System.out.println(list.get(tokens.get(word).get(i)));
			}
			return true;
		}else {
			System.out.println("There is no file containing the word!");
			return false;
		}
	}
	

	
	public void tokenize(String path, int index) throws IOException {
		String str;
		String space = " ";
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		while((str = br.readLine()) != null) {
			String [] s = str.split(space);
			int N = s.length;
			for(int i = 0; i < N; i++) {
				if(!tokens.containsKey(s[i])) {
					ArrayList<Integer> str1 = new ArrayList<Integer>();
					str1.add(index);
					tokens.put(s[i], str1);
				}
				else tokens.get(s[i]).add(index);
			}
		}
	}
	
	public void showFileNames() {
		
		for(Map.Entry<Integer, File> entry : list.entrySet()) {
			System.out.println(entry.getValue().getParent());
			System.out.println(entry.getKey()+" - "+entry.getValue().getName());
		}
	}
		
	public void openFile(String path) throws IOException {
		File file = new File(path);
		Desktop.getDesktop().open(file);
	}
	
	public static void main(String [] args) {
		FileIndexer f = null;
		Scanner scan = null;
		String path1;
		try {
			scan = new Scanner(System.in);
			System.out.println("Type the path of the folder");
			path1 = scan.nextLine();
			f = new FileIndexer(path1);
			
			boolean b = true;
			while(b){
				System.out.println("For specifiyin files, type: 1 \n"+
								   "  indexing new files, type: 2 \n"+
								   "     question a word, type: 3 \n"+
								   "	     open a file, type: 4 \n");
				String answ = scan.nextLine();
				if(answ.equals("1")) f.showFileNames();
				else if(answ.equals("2")) {
					boolean ahead = true;
					while(ahead) {
						System.out.println("Type the path of the file: ");
						String p = scan.next();
						File file = new File(p);
						f.indexTextFiles(file);
						System.out.println("Do you want to index new files, yes or no?");
						String s = scan.next();
						if(s.equals("yes")) ahead = true;
						else ahead = false;
					}
				}
				else if(answ.equals("3")) { 
					System.out.print("Type the word: ");
					String word = scan.nextLine();
					boolean open = f.search(word);
					if(open) {
						System.out.println("Do you want to open any of these files, yes or no?");
						String answer = scan.nextLine();
						if(answer.equals("yes")) {
							System.out.println("Type the path: ");
							String path = scan.nextLine();
							f.openFile(path);
						}
					}
				}else if(answ.equals("4")) {
					System.out.println("Type path: ");
					String path = scan.nextLine();
					f.openFile(path);
				}
				System.out.println("Do you want to continue, yes or no? ");
				String ans = scan.nextLine();
				if(ans.equals("yes")) b = true;
				else if(ans.equals("no")) b = false;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException nfe) {
				System.out.println("NumberFormatException: " + nfe.getMessage());
		}
	}
}