/*******************************************************************************
 * Copyright 2014 Charles University in Prague
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package cz.cuni.mff.ms.siptak.adeecolib.knowledge;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.blocks.ReplicatedHashMap3;
import org.jgroups.blocks.ReplicatedMap;
import org.jgroups.logging.Log;
import org.jgroups.stack.Protocol;

import cz.cuni.mff.d3s.deeco.exceptions.KRExceptionAccessError;
import cz.cuni.mff.d3s.deeco.exceptions.KRExceptionUnavailableEntry;
import cz.cuni.mff.d3s.deeco.knowledge.ISession;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeRepository;
import cz.cuni.mff.d3s.deeco.knowledge.local.DeepCopy;
import cz.cuni.mff.d3s.deeco.scheduling.IKnowledgeChangeListener;
import cz.cuni.mff.ms.siptak.adeecolib.service.AppMessenger;
import cz.cuni.mff.ms.siptak.adeecolib.service.AppMessenger.AppLogger;

/**
 * Implementation of the knowledge repository using a ReplicateHashMap.
 * 
 * @author Daniel Siptak
 * 
 */
public class ReplicatedKnowledgeRepository extends KnowledgeRepository {

	final ReentrantLock lock = new ReentrantLock();
	private ReplicatedMap<String, LinkedList<Object>> ts;// = new HashMap<String, List<Object>>();
	private Channel channel = null;

	private AppLogger logger = AppMessenger.getInstance().getLogger("ReplicatedHashMap");
	
	public ReplicatedKnowledgeRepository() {
		try {
			channel = new JChannel();
			channel.connect("Adeeco");
			for ( Protocol protocol: channel.getProtocolStack().getProtocols()) {
				protocol.setLevel("debug");
			}
			ReplicatedHashMap3<String, LinkedList<Object>> replMap = new ReplicatedHashMap3<String, LinkedList<Object>>(channel);
			replMap.start(10000);
			ts = replMap;
			logger.addLog("Creating ReplicatedHashMap");
		} catch (ChannelException e) {
			ts=null;
			logger.addLog("Error with ReplicatedHashMap");
			e.printStackTrace();
		}
		
	}

	@Override
	public Object[] get(String entryKey, ISession session)
			throws KRExceptionUnavailableEntry, KRExceptionAccessError {

		// Lock here to prevent race conditions in case the method is used out
		// of a
		// session. Likewise done in the rest methods.
		List<Object> vals = ts.get(entryKey);

		if (vals == null) {
			throw new KRExceptionUnavailableEntry("Key " + entryKey
					+ " is not in the knowledge repository.");
		}

		vals = (List<Object>) DeepCopy.copy(vals);
		return vals.toArray();
	}

	@Override
	public void put(String entryKey, Object value, ISession session)
			throws KRExceptionAccessError {

		LinkedList<Object> vals = ts.get(entryKey);

		if (vals == null) {
			vals = new LinkedList<Object>();
		}

		vals.add(DeepCopy.copy(value));
		ts.put(entryKey, vals);
	}

	@Override
	public Object[] take(String entryKey, ISession session)
			throws KRExceptionUnavailableEntry, KRExceptionAccessError {

		List<Object> vals = ts.get(entryKey);

		if (vals == null) {
			throw new KRExceptionUnavailableEntry("Key " + entryKey
					+ " is not in the knowledge repository.");
		}

		if (vals.size() <= 1) {
			ts.remove(entryKey);
		}

		//vals = (List<Object>) DeepCopy.copy(vals);
		return vals.toArray();
	}

	@Override
	public ISession createSession() {
		return new LocalSession(this);
	}

	@Override
	public boolean registerListener(IKnowledgeChangeListener listener) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setListenersActive(boolean on) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isListenersActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unregisterListener(IKnowledgeChangeListener listener) {
		// TODO Auto-generated method stub
		return false;
	}

	public void printAll() {
		
		Iterator<String> iterator = ts.keySet().iterator();
	    while(iterator.hasNext()) {
	        String key = iterator.next();
	    	System.out.println(key+" : "+ts.get(key));
	    }
		
		
		
	}

}
