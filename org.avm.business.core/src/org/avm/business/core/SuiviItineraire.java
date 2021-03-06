/*_________________________________________________________________________________
 *  SuiviCourse 2
 *  
 *  Int�gre les distances inter-arr�t d�finies dans la base de donn�es 
 *   
 */

package org.avm.business.core;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.avm.business.core.bundle.ConfigImpl;
import org.avm.business.core.event.Course;
import org.avm.business.core.event.Point;
import org.avm.business.protocol.phoebus.AvanceRetard;
import org.avm.business.protocol.phoebus.Deviation;
import org.avm.business.protocol.phoebus.Entete;
import org.avm.business.protocol.phoebus.Horodate;
import org.avm.business.protocol.phoebus.Message;
import org.avm.business.protocol.phoebus.PassageArret;
import org.avm.business.protocol.phoebus.Progression;
import org.avm.business.protocol.phoebus.Service;
import org.avm.device.gps.Gps;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.variable.Variable;
import org.osgi.util.position.Position;

public class SuiviItineraire implements ConfigurableService {

	// suivi de course
	private ProducerManager _producer;
	private Logger _log;
	private Gps _gps;
	private Avm _avm;
	private Variable _odometer;
	private JDB _jdb;

	private Deviation _deviation;
	private AvanceRetard _avanceRetard;
	private PassageArret _passageArret;

	private boolean _useAlgoWithDistanceBetweenStops = false;
	private float _distParcourue; // distance reelle parcourue depuis le debut
	// de la course
	private double _lastMeasuredDistance;
	private double _realDistBetweenTwoPoints = 0d;
	private int _toleranceDev;
	private Timer _avanceRetardTask = new Timer(); // timer de traitement de
	private int _period = 1000;
	private double _indexOdometer;

	private int _progressionVersProchainArret;

	private boolean _running;

	private long _entryTime;

	private boolean isAtStop = false;

	public void setAvm(Avm avm) {
		_avm = avm;
	}

	public SuiviItineraire() {
		_log = Logger.getInstance(SuiviItineraire.class);
		// _log.setPriority(Priority.DEBUG);
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void setOdometer(Variable odometer) {
		_odometer = odometer;
		resetOdometer();
	}

	private void resetOdometer() {
		if (_odometer != null) {
			_indexOdometer = _odometer.getValue().getValue();
		}
	}

	public boolean isRunning() {
		return _running;
	}

	public void start() {
		_running = true;
		_distParcourue = 0;

		resetOdometer();
		_avanceRetardTask = new Timer();
		_avanceRetardTask.schedule(new AvanceRetardTask(), 0, _period);

		_avanceRetard = new AvanceRetard();
		_passageArret = new PassageArret();
		setEnteteMessage(_avanceRetard);

		Point dep = null;
		int ar = 0;
		if (getCourse() != null) {
			dep = getCourse().getPointAvecRang(1);
			ar = calculAvanceRetard(dep);
			_log.debug("Start 'suiviitineraire' : avrt=" + ar);
			_avanceRetard.getEntete().getProgression().setRetard(ar);
			_producer.publish(_avanceRetard);
		}

	}

	private Course getCourse() {
		return _avm.getModel().getCourse();
	}

	public void stop() {
		_running = false;
		_avanceRetardTask.cancel();
	}

	// public AvanceRetard getAvanceRetard() {
	// return _avanceRetard;
	// }

	public Deviation getDeviation() {
		return _deviation;
	}

	public int getProgression() {
		return _progressionVersProchainArret;
	}

	public void setGps(Gps gps) {
		_gps = gps;
	}

	private void setEnteteMessage(Message message) {
		Entete entete = message.getEntete();
		int advDelay = calculAvanceRetard(getDernierArret());// en
		_log.debug("setEnteteMessage : avrt=" + advDelay);
		// minutes

		Service service = new Service();
		service.setServiceAgent((_avm.getModel().getServiceAgent() != null) ? _avm
				.getModel().getServiceAgent().getIdU()
				: -1);
		service.setCourse((_avm.getModel().getCourse() != null) ? _avm
				.getModel().getCourse().getIdu() : -1);
		service.setConducteur((_avm.getModel().getAuthentification() != null) ? _avm
				.getModel().getAuthentification().getMatricule()
				: -1);

		Progression progression = new Progression();
		progression.setDeviation(getModel().isHorsItineraire() ? 1 : 0);
		progression.setProgression(_progressionVersProchainArret);
		if (getDernierArret() != null) {
			progression.setRangDernierArret(getDernierArret().getRang());
			progression.setIduDernierArret(getDernierArret().getIdu());
		} else {
			progression.setRangDernierArret(0);
			progression.setIduDernierArret(0);
		}
		progression.setRetard(advDelay);

		entete.setDate(new Horodate());
		entete.getChamps().setService(1);
		entete.setService(service);
		entete.getChamps().setProgression(1);
		entete.setProgression(progression);
		entete.getChamps().setPosition(1);
		entete.setPosition(null);
		entete.setVersion(_avm.getModel().getDatasourceVersion());
	}

	/*
	 * Calcul de l'avance retard en minutes
	 */
	private int calculAvanceRetard(Point point) {
		int ar = 0;
		if (point != null) {
			if (_log.isDebugEnabled()) {
				_log.debug("Heure courante = "
						+ getCurrentHourInSecondFromMidnight());
				_log.debug("Heure theorique = " + point.getArriveeTheorique());
			}
			ar = (getCurrentHourInSecondFromMidnight() - point
					.getArriveeTheorique());
		}
		_log.debug("Calcul de l'avance/retard : " + ar);

		return ar;
	}

	private void updateDistParcourues() {
		double delta = getDistance() - _lastMeasuredDistance;
		_lastMeasuredDistance = getDistance();
		_realDistBetweenTwoPoints += delta;
		_distParcourue += delta;

	}

	/**
	 * Calcule la tranche correspondant à une heure d'arrivée donnée. Une
	 * tranche dure "_amplitude" secondes et recouvre la tranche précédente de
	 * "_chevauchement" secondes.
	 * 
	 * @param heureArriveTh
	 *            heure d'arrivee théorique en nb de seconde depuis minuit.
	 * @return la tranche dans laquelle est l'heure d'arrivee théorique passée
	 *         en paramètre.
	 */

	/*
	 * Calcul de la mise en deviation
	 */
	private boolean isSortieItineraire() {
		if (_gps == null) {
			return false;
		}
		Position curPos = _gps.getCurrentPosition();
		if (curPos == null || getDernierArret() == null)
			return false;

		int workIndex = getDernierArret().getRang() + 1;
		double directDist;
		long distRestante;

		if (workIndex < getCourse().getNombrePoint()) {
			Point nextStop = getCourse().getPointAvecRang(workIndex);

			directDist = getDistBetweenTwoPoints(curPos.getLatitude()
					.getValue(), curPos.getLatitude().getValue(),
					nextStop.getLongitude(), nextStop.getLatitude());

			// distRestante = (long) (nextStop.getDistance() -
			// _distParcourueRecalee);
			distRestante = (long) (nextStop.getDistance() - getDernierArret()
					.getDistance());
			if (directDist > distRestante + _toleranceDev)
				return true;
		}
		return false;
	}

	/*
	 * Calcul de la distance entre deux points geographiques (lat & lon) en
	 * metre
	 */
	private double getDistBetweenTwoPoints(double lat1, double lon1,
			double lat2, double lon2) {
		long rayonTerre = 6371000; // rayon de la terre en metre
		double deltaLat = lat2 - lat1; // delta des latitudes en degr�
		double deltaLong = lon1 - lon2; // delta des longitudes en degr�
		double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
				+ Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLong / 2)
				* Math.sin(deltaLong / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = rayonTerre * c;

		return dist;
	}

	public Point getDernierPoint() {
		return getDernierArret();
	}

	/*
	 * Obtient l'heure courante syst�me en secondes
	 */
	private int getCurrentHourInSecondFromMidnight() {
		Calendar cal = Calendar.getInstance();
		int heure = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		int sec = cal.get(Calendar.SECOND);
		return (heure * 3600) + (min * 60) + sec;
	}

	/*
	 * Donne la distance parcourue entre deux demandes (simulation odometre)
	 */
	private double getDistance() {
		if (_odometer != null) {
			return _odometer.getValue().getValue() - _indexOdometer;
		}
		return 0.0;
	}

	public void configure(Config config) {
		Integer i;

		i = ((ConfigImpl) config).getToleranceDev();
		setToleranceDev(i);

		i = ((ConfigImpl) config).getSuiviCrsPeriod();
		setPeriodiciteTraitement(i);
	}

	public void setPeriodiciteTraitement(Integer i) {
		_period = (i == null) ? 1000 : i.intValue() * 1000;
	}

	private void setToleranceDev(Integer i) {
		_toleranceDev = (i == null) ? 0 : i.intValue();
	}

	public void sendAvanceRetard() {
		_log.debug("Emission avance retard");
		((AvmImpl) _avm).sendMessage(_avanceRetard);
	}

	public void sendPassageArret() {
		_log.debug("Emission passage arret");
		Point dernier = _avm.getModel().getDernierPoint();
		if (dernier != null) {
			_passageArret.setAttente(dernier.getAttente());
		}
		((AvmImpl) _avm).sendMessage(_passageArret);
	}

	private void traitementAvanceRetard() {
		_log.info("Traitement AVANCE RETARD.");
		int oldadvDelay = _avanceRetard.getEntete().getProgression()
				.getRetard();// en minutes
		int advDelay = calculAvanceRetard(getDernierArret());// en
		// minutes

		if (_log.isDebugEnabled()) {
			_log.debug("advDelay = " + advDelay + " oldadvDelay = "
					+ oldadvDelay);
		}
		setEnteteMessage(_avanceRetard);

		if (isNecessarySendAR(advDelay, oldadvDelay)) {
			if (_log.isDebugEnabled()) {
				_log.debug("AR = " + advDelay);
			}
			_producer.publish(_avanceRetard);
			// on envoie un message que si on est parti
			if (_avm.getModel().isDepart()) {
				sendAvanceRetard();
			}
		}
	}

	/**
	 * Determine s'il est necessaire d'envoyer un message d'AR et reinit
	 * _indexCourant.
	 * 
	 * @param ar
	 *            : avance retard courant
	 * @param oldAr
	 *            : avance retard precedemment calculé
	 * @return true si on a change de tranche, false sinon.
	 */
	private boolean isNecessarySendAR(int ar, int oldAr) {
		// TODO
		return false;
	}

	/*
	 * Classe dotee de la methode "run" qui est appelee periodiquement par le
	 * timer _timPeriodTrait
	 */
	private class AvanceRetardTask extends TimerTask {
		public void run() {
			updateDistParcourues();
			if (_useAlgoWithDistanceBetweenStops) {
				if (isSortieItineraire()) {
					_log.debug("Mise en deviation");
					// _deviation = new Deviation();
					// _producer.publish(_deviation);
					_avm.sortieItineraire();
				} else {
					calculProgression();
					traitementAvanceRetard();
				}
			}
			// on est à un arret
			if (isAtStop) {
				Point point = getDernierArret();
				if (point == null) {
					_log.warn("AV/RT Task last point is null");
				} else {
					int ar = (getCurrentHourInSecondFromMidnight()
							- point.getArriveeTheorique() + point
							.getAttenteTheorique());
					_avanceRetard.getEntete().getProgression().setRetard(ar);
					getModel().setAvanceRetard(ar);
					_producer.publish(_avanceRetard);
				}
			}
		}

		/*
		 * Calcul de la progression entre deux arrets
		 */
		private void calculProgression() {
			int nextRank = getDernierArret().getRang();
			Point nextPoint = getCourse().getPointAvecRang(nextRank);
			double distBetweenTwoPoints = nextPoint.getDistance()
					- getDernierArret().getDistance();
			_progressionVersProchainArret = (int) ((_realDistBetweenTwoPoints * 100) / distBetweenTwoPoints);
		}
	}

	public void setJdb(JDB jdb) {
		_jdb = jdb;
	}

	public void unsetJdb(JDB jdb) {
		_jdb = null;
	}

	protected void journalize(String message, boolean sync) {
		if (_log.isDebugEnabled()) {
			_log.debug(message);
		}
		if (_jdb != null) {
			try {
				String tag = _avm.getModel().isGeorefMode() ? ("labo-" + Avm.JDB_TAG)
						: Avm.JDB_TAG;
				_jdb.journalize(tag, message);
				if (sync) {
					_jdb.sync();
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	public boolean isTerminusDepart(int balise) {
		Point[] p = getCourse().getPointAvecId(balise);
		return (p != null && p[0] != null && p[0].getRang() == 1);
	}

	public boolean isTerminusArrivee(int balise) {
		Course course = getCourse();
		if (course != null) {
			Point[] p = course.getPointAvecId(balise);
			if (p != null) {
				for (int i = 0; i < p.length; ++i) {
					if (p[i] != null
							&& p[i].getRang() == getCourse().getNombrePoint()
							&& getDernierArret() != null
							&& getDernierArret().getRang() == getCourse()
									.getNombrePoint()) {
						return true;
					}
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean isArret(int balise) {
		return (getCourse().getPointAvecId(balise) != null);
	}

	public boolean isArretEnAval(int balise) {
		Point[] points = getCourse().getPointAvecId(balise);
		boolean result = false;
		if (points != null) {
			result = false;
			Point dernierPoint = getDernierArret();
			if (dernierPoint != null) {

				for (int i = 0; i < points.length; i++) {
					if (points[i].getRang() > dernierPoint.getRang()) {
						// --si c'est un arret en _aval_ : son rang est plus
						// grand que
						// le dernier arret desservi
						return true;
					}

				}
			} else {
				// -- la course n'a pas commence' ; les 'points' sont donc à
				// venir...
				result = true;
			}

		}
		return result;

	}

	public boolean isArretCourant(int balise) {
		return (getDernierArret() != null && getDernierArret().getId() == balise);
	}

	private AvmModelManager getModel() {
		return (AvmModelManager) _avm.getModel();
	}

	public boolean isProchainArret(int balise) {
		Point prochain = _avm.getModel().getProchainPoint();
		return (prochain != null && prochain.getId() == balise);
	}

	public void entree(int balise) {
		entree(balise, false);
	}

	public void entree(int balise, boolean finHorsItineraire) {
		int currentRang = 0;
		if (getDernierArret() != null) {
			currentRang = getDernierArret().getRang();
		}

		Point p = getCourse().getPointAPartirRangAvecId(currentRang, balise);

		if (p != null) {

			if (p.isDesservi()) {
				_log.warn("StopPoint " + p.getNom() + " already served!");
				return;
			}
			updateAttente(p, true);
			getCourse().setPointDesservi(p);
			getModel().setDernierPoint(p);
			setEnteteMessage(_passageArret);
			setEnteteMessage(_avanceRetard);
			getModel().setAvanceRetard(
					_avanceRetard.getEntete().getProgression().getRetard());
			getModel().setInsidePoint(true);
			if (_log.isDebugEnabled()) {
				_log.debug("Entry StopPoint " + getDernierArret() + " at "
						+ _entryTime);
			}

			if (_avm.getModel().isGeorefMode()) {
				journalize("GEOREF;" + getDernierArret().getIdu() + ";"
						+ getDistance() + ";" + getDernierArret().getId() + ";"
						+ getDernierArret().getNom(), true);

			} else {
				isAtStop = true;
				StringBuffer buf = new StringBuffer();
				buf.append("ARRET;IN;");
				buf.append(getDernierArret().getId());
				buf.append(";");
				buf.append(getDistance());
				buf.append(";");
				buf.append(_avanceRetard.getEntete().getProgression()
						.getRetard());
				journalize(buf.toString(), false);

				// -- emission du passage a l'entree si le point horaire est
				// configure pour l'emission a l'entree
				if (p.isEntryNotify() || finHorsItineraire) {
					// if (!getModel().isDepart()) {
					// _log.warn("Message 'passage arret' non envoye : pas de depart");
					// } else {
					_log.debug("ENTREE ARRET : Message 'passage arret' envoye");
					setEnteteMessage(_passageArret);
					setEnteteMessage(_avanceRetard);
					sendPassageArret();

					// -- automatiquement en mode georef, si le prochain
					// arret n'est
					// pas georef.
					Point prochain = getModel().getProchainPoint();
					getModel().setGeorefMode(
							prochain != null && prochain.isGeoref() == false);
					// }
				}
			}

			Point prochain = _avm.getModel().getProchainPoint();
			if (prochain != null) {
				if (prochain.getDistance() == 0)
					_useAlgoWithDistanceBetweenStops = false;
				else
					_useAlgoWithDistanceBetweenStops = true;
			}

		} else {
			_log.debug("entree(balise) / Balise " + balise
					+ " n'est pas un point de la course");
		}

	}

	public void sortie(int balise) {
		if (getDernierArret() != null
				&& (getDernierArret().getId() == balise || getDernierArret()
						.getRang() == 1)) {
			int attente = getDernierArret().getAttente();
			if (attente > 0) {
				_log.warn("Deja passé par cet arret !?");
				return;
			}
			getModel().setInsidePoint(false);
			updateAttente(getDernierArret(), false);
			if (_log.isDebugEnabled()) {
				_log.debug("sortie balise (" + balise + "): "
						+ getDernierArret() + " at " + _entryTime);
			}
			isAtStop = false;
			StringBuffer buf = new StringBuffer();
			buf.append("ARRET;OUT;");
			buf.append(getDernierArret().getId());
			buf.append(";");
			buf.append(getDistance());
			buf.append(";");
			buf.append(_avanceRetard.getEntete().getProgression().getRetard());
			journalize(buf.toString(), false);

			// -- emission du passage a la sortie
			if (!getModel().isDepart()) {
				_log.warn("Message 'passage arret' non envoye : pas de depart");
			} else {
				setEnteteMessage(_passageArret);
				setEnteteMessage(_avanceRetard);
				Horodate horodate = getHorodate(_entryTime);
				_passageArret.getEntete().setDate(horodate);
				_avanceRetard.getEntete().setDate(horodate);
				sendPassageArret();
				_entryTime = 0;
				_log.debug("sortie arret : Message 'passage arret' envoye");
			}

		} else {
			_log.debug("sortie(balise) / Balise " + balise
					+ " n'est pas le dernier point desservi (!?)");
		}
	}

	private Horodate getHorodate(long time) {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTime(new Date(time));
		int annee = cal.get(java.util.Calendar.YEAR) - 1990;
		int mois = cal.get(java.util.Calendar.MONTH) + 1;
		int jour = cal.get(java.util.Calendar.DAY_OF_MONTH);
		int heure = cal.get(java.util.Calendar.HOUR_OF_DAY);
		int minute = cal.get(java.util.Calendar.MINUTE);
		int seconde = cal.get(java.util.Calendar.SECOND);

		return new Horodate(annee, mois, jour, heure, minute, seconde);
	}

	private void updateAttente(Point p, boolean entry) {
		int attente = 0;
		_log.debug("updateAttente : " + entry);
		if (entry == false) {
			long now = System.currentTimeMillis();
			if (_entryTime > 0 && _entryTime < now) {
				attente = (int) ((now - _entryTime) / 1000);
			}
			if (attente <= 0) {
				attente = 1;
			}
		} else {
			_entryTime = System.currentTimeMillis();
		}
		p.setAttente(attente);
		if (_log.isDebugEnabled()) {
			_log.debug("Attente a l'arret : " + attente + " sec.");
		}

	}

	private Point getDernierArret() {
		return _avm.getModel().getDernierPoint();
	}

}
