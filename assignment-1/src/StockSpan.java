import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Stack;

public final class StockSpan {
	
	public static int quotesSize;
	public static String[] parsedDates;
	public static float[] parsedQuotes;
	public static int[] calculatedSpans;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args[0].equals("-n")) {
			//naive calculation
			readCsv(args[1]);
			naiveCalculation();
			printSpanCsv();
		}
		else if (args[0].equals("-s")) {
			//stack calculation
			readCsv(args[1]);
			stackCalculation();
			printSpanCsv();
		}
		else if (args[0].equals("-b")) {
			//benchmark
			readCsv(args[1]);
			long startTime, endTime;
			
			startTime = System.currentTimeMillis();
			for (int i = 0; i < 100; i++) {
				naiveCalculation();
			}
			endTime = System.currentTimeMillis();
			System.out.printf("Naive implementation took: %d millis\n",endTime-startTime);
			
			startTime = System.currentTimeMillis();
			for (int i = 0; i < 100; i++) {
				stackCalculation();
			}
			endTime = System.currentTimeMillis();
			System.out.printf("Stack implementation took: %d millis\n",endTime-startTime);
		}
		else {
			System.out.println("Wrong parameters");
		}
	}
	public static void readCsv(String filename) {
		List<String> lines;
		String[] lineparts;
		
		try {
			lines = Files.readAllLines(Paths.get(filename),Charset.defaultCharset());
			quotesSize = lines.size()-1; //Ignore CSV header
			parsedDates = new String[quotesSize];
			parsedQuotes = new float[quotesSize];
			
			for (int i = 0; i < quotesSize; i++) {
				lineparts = lines.get(i+1).split(",");
				parsedDates[i] = lineparts[0];
				parsedQuotes[i] = Float.parseFloat(lineparts[1]);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}	
	public static void naiveCalculation() {
		calculatedSpans = new int[quotesSize];
		int k;
		boolean span_end;
		
		for (int i = 0; i < quotesSize; i++) {
			k = 1;
			span_end = false;
			while (i-k>= 0 && !span_end) {
				if (parsedQuotes[i-k] <= parsedQuotes[i]) {
					k++;
				}
				else {
					span_end= true;
				}
			}
			
			calculatedSpans[i] = k;
		}
	}
	
	public static void stackCalculation() {
		Stack<Integer> s = new Stack<Integer>();
		calculatedSpans = new int[quotesSize];
		
		s.push(0);
		calculatedSpans[0] = 1;
		
		for (int i = 1; i < quotesSize; i++) {
			while (!s.empty() &&
					parsedQuotes[s.lastElement()] <= parsedQuotes[i]) {
				s.pop();
			}
			if (s.empty()) {
				calculatedSpans[i] = i + 1;
			}
			else {
				calculatedSpans[i] = i - s.lastElement();
			}
			s.push(i);
		}
	}
	private static void printSpanCsv() {
		for (int i = 0 ; i < quotesSize; i++) {
			System.out.printf("%s,%d\n",parsedDates[i],calculatedSpans[i]);
		}
	}
}
