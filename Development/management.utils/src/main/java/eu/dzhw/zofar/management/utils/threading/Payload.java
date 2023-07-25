/*START HEADER*/
/* Zofar Survey System
* Copyright (C) 2014 Deutsches Zentrum für Hochschul- und Wissenschaftsforschung
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
/*STOP HEADER*/
package eu.dzhw.zofar.management.utils.threading;

import java.util.Observable;
import java.util.Observer;

public abstract class Payload extends Thread {
	
	private class myObservable extends Observable {

		public void changed() {
			this.setChanged();
		}
	};

	private final myObservable observable;

	
	public Payload() {
		super();
		this.observable = new myObservable();
	}

	public void addObserver(final Observer observer) {
		observable.addObserver(observer);
	}
	
	public void changed(){
		observable.changed();
	}
	
	public void notifyObservers(final Object message){
		observable.notifyObservers(message);
	}
	
	public abstract void run();

}
