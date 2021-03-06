/* @flavorc
 *
 * S_MSG.java
 * 
 * This file was automatically generated by flavorc
 * from the source file:
 *     'avm.fl'
 *
 * For information on flavorc, visit the Flavor Web site at:
 *     http://www.ee.columbia.edu/flavor
 *
 * -- Do not edit by hand --
 *
 */

package org.avm.elementary.protocol.avm.parser;

import java.io.IOException;
import java.io.InputStream;

import flavor.IBitstream;
import flavor.MapResult;

public class S_MSG extends Message {
	char _dest[] = new char[12];
	int _length;
	int _value[] = new int[1];

	public static final int MESSAGE_TYPE = 9;

	public S_MSG() {
		super(MESSAGE_TYPE);
	}

	public S_MSG(String dest, byte[] value) {
		super(MESSAGE_TYPE);
		setDestination(dest);
		setValue(value);
	}

	public String getDestination() {
		return new String(_dest);
	}

	public void setDestination(String dest) {
		if (dest != null) {
			int end = Math.min(dest.length(), 12);
			char[] value = dest.substring(0, end).toCharArray();
			for (int i = 0; i < end; i++) {
				_dest[i] = value[i];
			}
		}
	}

	public byte[] getValue() {
		byte[] value = new byte[_length];
		for (int i = 0; i < _length; i++) {
			value[i] = (byte) _value[i];
		}
		return value;
	}

	public void setValue(byte[] value) {
		if (value != null) {
			_length = value.length;
			_value = new int[_length];
			for (int i = 0; i < _length; i++) {
				_value[i] = value[i];
			}
		}
	}

	public String toString() {
		return "message: " + super.toString() + " dest = " + getDestination()
				+ " msg = " + toHexaString(getValue());
	}

	public static class DefaultMessageFactory extends MessageFactory {

		protected Message get(InputStream in) throws Exception {
			InputBitstream bs = new InputBitstream(in);
			Message message = new S_MSG();
			message.get(bs);

			return message;
		}
	}

	static {
		MessageFactory.factories.put(new Integer(MESSAGE_TYPE),
				new DefaultMessageFactory());
	}

	public int get(IBitstream _F_bs) throws IOException {
		int _F_ret = 0;
		MapResult _F_mr;
		int _F_dim0, _F_dim0_end;
		_F_ret += super.get(_F_bs);
		_F_dim0_end = 12;
		for (_F_dim0 = 0; _F_dim0 < _F_dim0_end; _F_dim0++) {
			_dest[_F_dim0] = (char) _F_bs.getbits(8);
		}
		_length = _F_bs.getbits(16);
		_value = new int[_length];
		_F_dim0_end = _length;
		for (_F_dim0 = 0; _F_dim0 < _F_dim0_end; _F_dim0++) {
			_value[_F_dim0] = _F_bs.getbits(8);
		}
		return _F_ret;
	}

	public int put(IBitstream _F_bs) throws IOException {
		int _F_ret = 0;
		MapResult _F_mr;
		int _F_dim0, _F_dim0_end;
		_F_ret += super.put(_F_bs);
		_F_dim0_end = 12;
		for (_F_dim0 = 0; _F_dim0 < _F_dim0_end; _F_dim0++) {
			_F_bs.putbits(_dest[_F_dim0], 8);
		}
		_F_bs.putbits(_length, 16);
		_F_dim0_end = _length;
		for (_F_dim0 = 0; _F_dim0 < _F_dim0_end; _F_dim0++) {
			_F_bs.putbits(_value[_F_dim0], 8);
		}
		return _F_ret;
	}
}
