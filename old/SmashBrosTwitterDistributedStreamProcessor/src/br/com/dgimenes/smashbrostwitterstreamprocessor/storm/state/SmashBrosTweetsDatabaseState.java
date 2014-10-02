/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 Daniel Gimenes
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package br.com.dgimenes.smashbrostwitterstreamprocessor.storm.state;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import storm.trident.state.ITupleCollection;
import storm.trident.state.State;
import storm.trident.state.StateFactory;
import storm.trident.state.ValueUpdater;
import storm.trident.state.map.MapState;
import storm.trident.state.map.RemovableMapState;
import storm.trident.state.snapshot.Snapshottable;
import backtype.storm.task.IMetricsContext;
import br.com.dgimenes.smashbrostwitterstreamprocessor.model.Tweet;

public class SmashBrosTweetsDatabaseState implements Snapshottable<Tweet>, ITupleCollection, MapState<Tweet>,
		RemovableMapState<Tweet> {
	
	public static class Factory implements StateFactory {
		private static final long serialVersionUID = -2360857453577947571L;
		String _id;

        public Factory() {
            _id = UUID.randomUUID().toString();
        }

        @Override
        public State makeState(Map conf, IMetricsContext metrics, int partitionIndex, int numPartitions) {
            return new SmashBrosTweetsDatabaseState(_id + partitionIndex);
        }
    }

	public SmashBrosTweetsDatabaseState(String id) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Tweet get() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void beginCommit(Long txid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void commit(Long txid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Tweet> multiGet(List<List<Object>> keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void multiRemove(List<List<Object>> keys) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Tweet> multiUpdate(List<List<Object>> keys, List<ValueUpdater> updaters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void multiPut(List<List<Object>> keys, List<Tweet> vals) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Iterator<List<Object>> getTuples() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tweet update(ValueUpdater updater) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void set(Tweet o) {
		// TODO Auto-generated method stub
		
	}

}
