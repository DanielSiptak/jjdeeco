package cz.cuni.mff.ms.siptak.adeecolib.cloud;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.knowledge.RepositoryKnowledgeManager;
import cz.cuni.mff.d3s.deeco.knowledge.local.LocalKnowledgeRepository;
import cz.cuni.mff.d3s.deeco.provider.AbstractDEECoObjectProvider;
import cz.cuni.mff.d3s.deeco.provider.ClassDEECoObjectProvider;
import cz.cuni.mff.d3s.deeco.runtime.Runtime;
import cz.cuni.mff.d3s.deeco.scheduling.MultithreadedScheduler;
import cz.cuni.mff.d3s.deeco.scheduling.Scheduler;
import cz.cuni.mff.ms.siptak.adeecolib.knowledge.ReplicatedKnowledgeRepository;

/**
 * Main class for launching the application.
 * 
 * @author Daniel Siptak
 * 
 */
public class ReplicatedLauncherCloud2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<Class<?>> components = Arrays.asList(new Class<?>[]{ NodeB.class});
		List<Class<?>> ensembles = Arrays.asList(new Class<?>[]{MigrationEnsemble.class});
		KnowledgeManager km = new RepositoryKnowledgeManager(
				new ReplicatedKnowledgeRepository());
		Scheduler scheduler = new MultithreadedScheduler();
		AbstractDEECoObjectProvider dop = new ClassDEECoObjectProvider( components, ensembles);
		Runtime rt = new Runtime(km, scheduler);
		rt.registerComponentsAndEnsembles(dop);
		rt.startRuntime();
	}
}
