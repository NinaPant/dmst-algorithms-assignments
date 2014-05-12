
import java.io.Console;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.*;
import java.lang.Math;

public class Schulze {
	public static int maxCandidates;
	public static int maxBallots;
	
	public static String[] candidates;
	public static ArrayList<ArrayList<Integer>> ballots;
	
	public static ArrayList<ArrayList<Integer>> wins = new ArrayList<ArrayList<Integer>>();
	
	
	public static void main(String[] args) {
		String filename = args[0];
		readVotes(filename);
		doSchulze();
		printResults();
	}
	
	private static void readVotes(String filename) {
		byte[] content;
		String jsontext;
		try {
			content = Files.readAllBytes(Paths.get(filename));
			jsontext = new String(content,Charset.defaultCharset());
			
			JSONTokener jt = new JSONTokener(jsontext);
			JSONObject obj = new JSONObject(jt);
			
			JSONArray jsoncandidates = obj.getJSONArray("candidates");
			JSONArray jsonballots = obj.getJSONArray("ballots");
			
			maxCandidates = jsoncandidates.length();
			maxBallots = jsonballots.length();
			
			candidates = new String[maxCandidates];
			for (int i = 0; i < maxCandidates; i++ ) {
				candidates[i] = jsoncandidates.getString(i);
			}
			
			ballots = new ArrayList<ArrayList<Integer>>();
					
			JSONArray jsonballot;
			ArrayList<Integer> ballot;
			
			for ( int i = 0; i < jsonballots.length(); i++ ) {
				jsonballot = jsonballots.getJSONArray(i);
				ballot = new ArrayList<Integer>();
				
				for ( int j = 0; j < jsonballot.length(); j++ ) {
					ballot.add(j,jsonballot.getInt(j));
				}
				ballots.add(i,ballot);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static void doSchulze() {
		int[][] candidatePairs = new int[maxCandidates][maxCandidates];
		int[][] predPairs = new int[maxCandidates][maxCandidates];
		int[][] strongestPaths = new int[maxCandidates][maxCandidates];
		List<Integer> ballot;
		int firstvote;
		
		//candidate pairs
		for (int i = 0; i < maxBallots; i++ ) {
			ballot = ballots.get(i);
			for ( int j = 0; j < ballot.size(); j++ ) {
				firstvote = ballot.get(j);
				for ( int k = j + 1; k < ballot.size(); k++ ) {
					candidatePairs[firstvote][ballot.get(k)] = candidatePairs[firstvote][ballot.get(k)] + 1;
				}
			}
		}
		
		//strongest paths
		for ( int i = 0; i < maxCandidates; i ++ ) {
			for ( int j = 0; j < maxCandidates; j ++ ) {
				if ( candidatePairs[i][j] > candidatePairs[j][i] ) {
					strongestPaths[i][j] = candidatePairs[i][j] - candidatePairs[j][i];
					predPairs[i][j] = i;
				}
				else {
					strongestPaths[i][j] = Integer.MIN_VALUE;
					predPairs[i][j] = -1;
				}
			}
		}
		
		for ( int k = 0; k < maxCandidates; k++ ) {
			for ( int i = 0; i < maxCandidates; i++ ) {
				if ( i != k ) {
					for ( int j = 0; j < maxCandidates; j++ ) {
						if ( j != i ) {
							if ( strongestPaths[i][j] < Math.min(strongestPaths[i][k], strongestPaths[k][j])) {
								strongestPaths[i][j] = Math.min(strongestPaths[i][k], strongestPaths[k][j]);
								predPairs[i][j] = predPairs[k][j];
							}
						}
					}
				}
			}
		}
		
		
		// calculate wins		
		for ( int i = 0; i < maxCandidates; i++ ) {
			ArrayList<Integer> listi = new ArrayList<Integer>();
			wins.add(listi);
			
			for ( int j= 0; j < maxCandidates; j++ ) {
				if ( i != j ) {
					if ( strongestPaths[i][j] > strongestPaths[j][i] ) {
						listi.add(j);
					}
				}
			}
		}
	}
	
	private static void printResults(){
		//print results
		for ( int i = 0; i < maxCandidates; i++ ) {
			System.out.print( candidates[i] + " = " + wins.get(i).size() + " [" );
			for ( int j = 0; j < wins.get(i).size(); j ++ ) {
				System.out.print( candidates[wins.get(i).get(j)] );
				if ( j+1 != wins.get(i).size() ) {
					System.out.print( ", " );
				}
			}
			System.out.print( "]\n" );
		}
	}
}
