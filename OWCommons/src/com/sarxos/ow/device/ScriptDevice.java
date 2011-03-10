package com.sarxos.ow.device;

import java.util.TimerTask;

public interface ScriptDevice {

	/**
	 * Dodaje zadanie do wykonania, ktore ma byc wywolywane w rownych 
	 * odstepach czasu <i>(fixed-rate)</i>.
	 * @param functionName - nazwa funkcji do wykonania
	 * @param period - odstep czasu
	 * @return Zwraca uchwyt do zadania
	 */
	public TimerTask setInterval(String functionName, long period);

	/**
	 * Dodaje zadanie do wykonania, ktore ma byc wykonywane w rownych 
	 * odstepach czasu <i>(fixed-rate)</i>.
	 * @param functionName - nazwa funkcji do wykonania
	 * @param period - odstep czasu
	 * @param delay - opoznienie w stosunku do pierwszego wykonania
	 * @return Zwraca uchwyt do zadania
	 */
	public TimerTask setInterval(String functionName, long period, long delay);

	/**
	 * Dodaje zadanie do wykonania w rownych odstepach czasu pomiedzy jego kolejnym
	 * zakonczeniem i nastepnym rozpoczeciem <i>(fixed-delay)</i>.
	 * @param functionName - nazwa funkcji do wykonania
	 * @param period - odstep czasu
	 * @return Zwraca uchwyt do zadania
	 */
	public TimerTask setTimeout(String functionName, long period);

	/**
	 * Dodaje zadanie do wykonania w rownych odstepach czasu pomiedzy jego kolejnym
	 * zakonczeniem i nastepnym rozpoczeciem <i>(fixed-delay)</i>.
	 * @param functionName - nazwa funkcji do wykonania
	 * @param period - odstep czasu
	 * @param delay - opoznienie w stosunku do pierwszego wykonania
	 * @return Zwraca uchwyt do zadania
	 */
	public TimerTask setTimeout(String functionName, long period, long delay);

	/**
	 * Zatrzymuje wczesniej zaplanowane zadanie.<br>
	 * @param task - uchwyt do zadania
	 */
	public void clearInterval(TimerTask task);

}