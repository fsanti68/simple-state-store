package com.logicalis.la.state.samples;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import com.logicalis.la.state.core.InvalidDataTypeException;
import com.logicalis.la.state.core.StateStore;

/**
 * 
 * Exemplo de uso
 *
 */
public class DroneSample {

	static double batteryLevel = 100.0;
	static double windLevel = 1.0;
	static double gpsSignalLevel = 92.0;

	// Campinas, Parque Rural Fazenda Santa Cândida
	static double lat = -22.842830;
	static double lng = -47.035735;

	static String[] randomEvents = { "droneconnectionlost", "dronestoragefull" };

	private long stopTime = 0L;
	
	public DroneSample() {

		// Busca o estado atualizado a cada 1 segundo
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					String state = StateStore.getInstance().getStateAsJson();
					System.out.println(state);
					
					boolean flightDone = state.contains("missionfinish");
					if (flightDone) {
						timer.cancel();
					}
				} catch (InvalidDataTypeException e) {
					System.err.println("Invalid data type: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}, 0L, 1000L);

		/**
		 * missão
		 */

		// obtido do WaypointMissionOperatorListener.OnMissionUploadProgress
		for (int i = 0; i < 100; i++) {
			StateStore.getInstance().set("missionuploadprogress", new Long(i + 1));
			sleep(30);
		}
		sleep(1000);

		// obtido do WaypointMissionOperatorListener.OnMissionStart
		StateStore.getInstance().addToList("mission", "missionstart");

		// elimina o registro de upload (não mais necessário após o início da missão)
		StateStore.getInstance().remove("missionuploadprogress");

		// marca a missão para durar 2 minutos
		this.stopTime = System.currentTimeMillis() + 120000L;

		// obtido do WaypointMissionOperatorListener.OnMissionFinish após o término da
		// missão
		Thread missionOnExecutionFinish = new Thread(new Runnable() {

			@Override
			public void run() {
				while (isFlying()) {
					sleep(2000);
				}
				sleep(2000);

				StateStore.getInstance().addToList("mission", "missionfinish");
			}
		});

		// obtido do FlightControllerState.OnDroneUpdateLocation
		Thread flightOnDroneUpdateLocation = new Thread(new Runnable() {

			@Override
			public void run() {
				while (isFlying()) {
					Map<String, Double> coords = new TreeMap<>();
					lat += Math.random() * 0.0001;
					lng += Math.random() * 0.0001;
					coords.put("lat", lat);
					coords.put("lng", lng);
					StateStore.getInstance().set("location", coords);
					sleep(50);
				}
			}
		});

		// obtido do FlightControllerState.WindLevel
		Thread flightOnWindLevel = new Thread(new Runnable() {
			@Override
			public void run() {
				while (isFlying()) {
					StateStore.getInstance().set("windlevel", windLevel);
					windLevel += Math.random() * 0.0001;
					sleep(500);
				}
			}
		});

		// obtido do FlightControllerState.GpsSignalLevel
		Thread flightOnGpsSignalLevel = new Thread(new Runnable() {
			@Override
			public void run() {
				while (isFlying()) {
					StateStore.getInstance().set("gpssignallevel", gpsSignalLevel);
					gpsSignalLevel += Math.random() * 0.0001;
					sleep(500);
				}
			}
		});

		// obtido do FlightControllerState.BatteryLevel
		Thread flightOnBatteryLevel = new Thread(new Runnable() {
			@Override
			public void run() {
				while (isFlying()) {
					StateStore.getInstance().set("batterylevel", batteryLevel);
					batteryLevel -= 0.0001;
					sleep(500);
				}
			}
		});

		// simula a geração randomica de eventos de missão
		Thread populateMessages = new Thread(new Runnable() {

			@Override
			public void run() {
				while (isFlying()) {
					StateStore.getInstance().addToList("mission", "missionerror");
					sleep(30000 + ((long) Math.random() * 600000));
				}
			}
		});

		// simula a geração randomica de eventos de estado
		Thread populateEvents = new Thread(new Runnable() {

			@Override
			public void run() {
				while (isFlying()) {
					int eventIndex = (int) (1 * Math.random());
					StateStore.getInstance().addToSet("events", randomEvents[eventIndex]);
					sleep(500 + ((long) Math.random() * 600000));
				}
			}
		});

		// Dispara as threads de simulação dos callbacks
		missionOnExecutionFinish.start();
		flightOnDroneUpdateLocation.start();
		flightOnWindLevel.start();
		flightOnGpsSignalLevel.start();
		flightOnBatteryLevel.start();
		populateMessages.start();
		populateEvents.start();
	}

	private boolean isFlying() {
		return System.currentTimeMillis() < stopTime;
	}

	private void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		}
	}

	public static void main(String[] args) {
		new DroneSample();
	}
}
