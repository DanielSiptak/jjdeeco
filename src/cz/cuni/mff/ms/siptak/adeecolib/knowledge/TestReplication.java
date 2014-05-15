package cz.cuni.mff.ms.siptak.adeecolib.knowledge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.exceptions.KRExceptionAccessError;
import cz.cuni.mff.d3s.deeco.exceptions.KRExceptionUnavailableEntry;
import cz.cuni.mff.d3s.deeco.knowledge.Knowledge;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeRepository;
import cz.cuni.mff.d3s.deeco.knowledge.RepositoryKnowledgeManager;
import cz.cuni.mff.d3s.deeco.knowledge.local.LocalKnowledgeRepository;
import cz.cuni.mff.d3s.deeco.provider.AbstractDEECoObjectProvider;
import cz.cuni.mff.d3s.deeco.provider.ClassDEECoObjectProvider;
import cz.cuni.mff.d3s.deeco.runtime.Runtime;
import cz.cuni.mff.d3s.deeco.scheduling.MultithreadedScheduler;
import cz.cuni.mff.d3s.deeco.scheduling.Scheduler;

/**
 * Main class for launching the application.
 * 
 * @author Daniel Siptak
 * 
 */
public class TestReplication {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		   
		ReplicatedKnowledgeRepository kr = new ReplicatedKnowledgeRepository();
		String read="";
		try {
			while (!read.equals("e")) {
				if (read.equals("p")) {
					System.out.println("What to store :");
					read=  br.readLine();
					kr.put(read,read);
				} else if (read.equals("g")) {
					System.out.println("What to get :");
					read=  br.readLine();
					System.out.println(kr.get(read)[0].toString());
				} else if (read.equals("l")) {
					System.out.println("List all :");
					kr.printAll();					
				}  
				System.out.println("Pick [epgl] :");
				read=  br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KRExceptionAccessError e) {
			System.out.println("Put exception");
			e.printStackTrace();
		} catch (KRExceptionUnavailableEntry e) {
			System.out.println("Get exception");
			e.printStackTrace();
		}
		System.exit(1);
	}
}
