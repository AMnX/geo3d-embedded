/* @flavorc
 *
 * DemandeStatut.java
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
import java.io.InputStream;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;

import flavor.Bitstream;
import flavor.IBitstream;
import flavor.MapResult;

public class DemandeStatut extends Message {
    ChampsOptionnels _statut;
    
	public static final int MESSAGE_TYPE = 1;

	public static final String MESSAGE_NAME = "demande-statut";

	public DemandeStatut() {
		super();
		_entete._type = MESSAGE_TYPE;
		_entete._champs._date = 1;
		_entete._date = new Horodate();

		_statut = new ChampsOptionnels();
	}

	public ChampsOptionnels getStatut() {
		return _statut;
	}

	public void setStatut(ChampsOptionnels statut) {
		_statut = statut;
	}

	public String toString() {
		return MESSAGE_NAME + " [" + super.toString() + "]"
				+ " champs optionnels: " + _statut.toString();
	}

	public static class DefaultMessageFactory extends MessageFactory {

		protected Message unmarshal(InputStream in) throws Exception {
			IBindingFactory factory = BindingDirectory
					.getFactory(DemandeStatut.class);
			IUnmarshallingContext context = factory
					.createUnmarshallingContext();
			return (Message) context.unmarshalDocument(in, null);

		}

		protected Message get(InputStream in) throws Exception {
			Bitstream bs = new Bitstream(in);
			Message message = new DemandeStatut();
			message.get(bs);
			bs.close();
			in.reset();
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
        _F_ret += super.get(_F_bs);
        _statut = new ChampsOptionnels();
        _F_ret += _statut.get(_F_bs);
        return _F_ret;
    }

    public int put(IBitstream _F_bs) throws IOException {
        int _F_ret = 0;
        MapResult _F_mr;
        _F_ret += super.put(_F_bs);
        _F_ret += _statut.put(_F_bs);
        return _F_ret;
    }
}
