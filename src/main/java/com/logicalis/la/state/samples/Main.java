package com.logicalis.la.state.samples;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.la.state.core.InvalidDataTypeException;
import com.logicalis.la.state.core.StateStore;

/**
 * 
 * Exemplo de uso
 *
 */
public class Main {

	static double batteryLevel = 100.0;

	// Campinas, Parque Rural Fazenda Santa Cândida
	static double lat = -22.842830;
	static double lng = -47.035735;
	static double alt = 300.0;

	public Main() {

		// simula a geração de aprox. 200 chamadas/segundo ao callback de atualização
		// de coordenadas
		Thread populateCoordinates = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					Map<String, Double> coords = new TreeMap<>();
					lat += Math.random() * 0.0001;
					lng += Math.random() * 0.0001;
					alt += -0.001 + (Math.random() * 0.002);
					coords.put("lat", lat);
					coords.put("lng", lng);
					coords.put("alt", alt);
					StateStore.getInstance().set("coord", coords);
					sleep(4);
				}
			}
		});

		// simula a geração de aprox. 100 chamadas/segundo ao callback de atualização
		// de nível de bateria
		Thread populateBattery = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					StateStore.getInstance().set("battery", batteryLevel);
					batteryLevel -= 0.00001;
					sleep(9);
				}
			}
		});

		// simula a geração de 20 chamadas/segundo ao callback de atualização de
		// notificações / eventos
		Thread populateMessages = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					String event = "random event #" + new Double((Math.random() * 20)).shortValue();
					StateStore.getInstance().addToSet("event", event);
					sleep(45);
				}
			}
		});

		// simula a geração de 2 chamadas/segundo ao callback de atualização de
		// notificações / eventos
		Thread populateEvents = new Thread(new Runnable() {

			private int id = 0;

			@Override
			public void run() {
				while (true) {
					String event = "mission event #" + ++id;
					StateStore.getInstance().addToList("mission", event);
					sleep(500 + ((long) Math.random() * 600000));
				}
			}
		});

		// Dispara as threads de simulação dos callbacks
		populateCoordinates.start();
		populateBattery.start();
		populateMessages.start();
		populateEvents.start();

		// Busca o estado atualizado a cada 1 segundo
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			ObjectMapper mapper = new ObjectMapper();

			@Override
			public void run() {
				try {
					String json = mapper.writeValueAsString(StateStore.getInstance().getState());
					System.out.println(json);

				} catch (InvalidDataTypeException | JsonProcessingException e) {
					System.err.println("Invalid data type: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}, 0L, 1000L);
	}

	private void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}
