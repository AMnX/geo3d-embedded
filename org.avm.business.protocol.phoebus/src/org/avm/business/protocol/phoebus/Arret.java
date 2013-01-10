/* @flavorc
 *
 * Arret.java
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

public class Arret {
    int _point;
    int _longitude;
    int _latitude;
    CString _nom;
    CString _code;
    
	public Arret() {
		super();
		_nom = new CString();;
		_code = new CString();
	}

	public Arret(int point, int longitude, int latitude, CString nom,
			CString code) {
		super();
		_point = point;
		_longitude = longitude;
		_latitude = latitude;
		_nom = nom;
		_code = code;
	}

	public int getPoint() {
		return _point;
	}

	public void setPoint(int point) {
		_point = point;
	}

	public int getLongitude() {
		return _longitude;
	}

	public void setLongitude(int longitude) {
		_longitude = longitude;
	}

	public int getLatitude() {
		return _latitude;
	}

	public void setLatitude(int latitude) {
		_latitude = latitude;
	}

	public CString getNom() {
		return _nom;
	}

	public void setNom(CString nom) {
		_nom = nom;
	}

	public CString getCode() {
		return _code;
	}

	public void setCode(CString code) {
		_code = code;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" [point " + _point + " longitude " + _longitude
				+ " latitude " + _latitude + " nom " + _nom + " code " + _code);
		sb.append("]");
		return sb.toString();
	}
	

    public int get(IBitstream _F_bs) throws IOException {
        int _F_ret = 0;
        MapResult _F_mr;
        _point = _F_bs.getbits(32);
        _longitude = _F_bs.sgetbits(32);
        _latitude = _F_bs.sgetbits(32);
        _nom = new CString();
        _F_ret += _nom.get(_F_bs);
        _code = new CString();
        _F_ret += _code.get(_F_bs);
        return _F_ret;
    }

    public int put(IBitstream _F_bs) throws IOException {
        int _F_ret = 0;
        MapResult _F_mr;
        _F_bs.putbits(_point, 32);
        _F_bs.putbits(_longitude, 32);
        _F_bs.putbits(_latitude, 32);
        _F_ret += _nom.put(_F_bs);
        _F_ret += _code.put(_F_bs);
        return _F_ret;
    }
}
