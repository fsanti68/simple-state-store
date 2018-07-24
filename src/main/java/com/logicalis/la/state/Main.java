package com.logicalis.la.state;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Exemplo de uso
 *
 */
public class Main {

	public Main() {

		// simula a geração de 200 chamadas /segundo ao callback de atualização de
		// coordenadas
		Thread populateCoordinates = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					Map<String, Double> coords = new HashMap<>();
					coords.put("lat", Math.random() * -100);
					coords.put("lng", Math.random() * -100);
					coords.put("alt", Math.random() * 300);
					StateStore.getInstance().set("coord", coords);
					sleep(5);
				}
			}
		});

		// simula a geração de 100 chamadas /segundo ao callback de atualização de
		// nível de bateria
		Thread populateBattery = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					Double battery = Math.random() * 100;
					StateStore.getInstance().set("battery", battery);
					sleep(10);
				}
			}
		});

		// simula a geração de 20 chamadas / segundo ao callback de atualização de
		// notificações / eventos
		Thread populateEvents = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					String event = "random event #" + new Double((Math.random() * 20)).shortValue();
					StateStore.getInstance().add("event", event);
					sleep(50);
				}
			}
		});

		// Dispara as thread de simulação dos callbacks
		populateCoordinates.start();
		populateBattery.start();
		populateEvents.start();

		// Thread que emite o estado a cada 1 segundo
		Thread sendState = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						System.out.println(StateStore.getInstance().getState());
					} catch (InvalidDataTypeException e) {
						System.err.println("Invalid data type: " + e.getMessage());
						e.printStackTrace();
					}
					sleep(1000L);
				}
			}
		});

		sleep(2000L);

		// Dispara a thread de atualização do estado (por ex. de envio para o front-end
		sendState.start();
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
