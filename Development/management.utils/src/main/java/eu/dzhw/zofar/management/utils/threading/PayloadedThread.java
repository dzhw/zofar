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

import java.util.Observer;

public class PayloadedThread extends Thread {
	
	private final Payload target;


	public PayloadedThread(Payload target) {
		super(target);
		this.target = target;
	}
	
	public void addObserver(final Observer observer) {
		if(this.target != null)this.target.addObserver(observer);
	}

	public void sleepFor(final long delay) throws InterruptedException {
		if (delay > 0) {
			sleep(delay);
		}
	}
}
