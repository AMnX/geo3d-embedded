/* @flavorc
 *
 * Heure.java
 * 
 * This file was automatically generated by flavorc
 * from the source file:
 *     'phoebus.fl'
 *
 * For information on flavorc, visit the Flavor Web site at:
 *     http://www.ee.columbia.edu/flavor
 *
 * -- Do not edit by hand --
 *
 */

package org.avm.business.protocol.phoebus;
import java.io.IOException;

import flavor.IBitstream;
import flavor.MapResult;

public class Heure {
    int _heure;
    int _minute;
    int _seconde;
    	
   public Heure() {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		_heure = cal.get(java.util.Calendar.HOUR_OF_DAY);
		_minute = cal.get(java.util.Calendar.MINUTE);
		_seconde = cal.get(java.util.Calendar.SECOND);
	}

	public Heure(int heure, int minute, int seconde) {
		super();
		_heure = heure;
		_minute = minute;
		_seconde = seconde;
	}

	public int getHeure() {
		return _heure;
	}

	public void setHeure(int heure) {
		_heure = heure;
	}

	public int getMinute() {
		return _minute;
	}

	public void setMinute(int minute) {
		_minute = minute;
	}

	public int getSeconde() {
		return _seconde;
	}

	public void setSeconde(int seconde) {
		_seconde = seconde;
	}

	public String toString() {
		return "[ " + _heure + ":" + _minute + ":" + _seconde + "]";
	}
	

    public int get(IBitstream _F_bs) throws IOException {
        int _F_ret = 0;
        MapResult _F_mr;
        _heure = _F_bs.getbits(5);
        _minute = _F_bs.getbits(6);
        _seconde = _F_bs.getbits(6);
        return _F_ret;
    }

    public int put(IBitstream _F_bs) throws IOException {
        int _F_ret = 0;
        MapResult _F_mr;
        _F_bs.putbits(_heure, 5);
        _F_bs.putbits(_minute, 6);
        _F_bs.putbits(_seconde, 6);
        return _F_ret;
    }
}
