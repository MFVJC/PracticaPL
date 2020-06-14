package codeGenerator;

import java.util.HashMap;
import java.util.Map;

import javafx.util.Pair;

public class Bloque {
	//Guardamos el nombre y la direccion en la que esta almacenado cada identificador
	private Map<String,Integer> direccionIdentificadores = new HashMap<>();
	
	//Guardamos el tipo y el tamano de cada tipo nuevo declarado
	private Map<String,Integer> tipos = new HashMap<>();
	
	//Usado para reutilizar direcciones al salir de un ambito local
	private int tamanoBloque;
	
	//Indice del bloque en la lista de bloques
	private int posicionBloque; 
	
	//Bloque del que proviene el bloque actual
	private Bloque bloquePadre;
	
	//Proxima direccion de la memoria en la que guardar la siguiente variable
	private int proximaDireccion;
	
	//Abreviado como pa: pa++ cuando se cree un nuevo marco de activacion. Esto sirve para remontarnos a la direccion de memoria del anterior marco
	private int profundidadAnidamiento;
	
	//Usado en caso de que se cree un nuevo marco de activacion. Calcula el espacio de memoria necesario para dicho marco
	private int ssp;
	
	//True si el bloque crea un nuevo marco de activacion (al declarar una funcion o procedimiento)
	//False en caso contrario (ambitos locales de if, while, switch...)
	boolean marcoActivacion;
	
	public Bloque(Bloque bloquePadre, int posicionBloque, boolean marcoActivacion) {
		this.tamanoBloque = 0;
		this.posicionBloque = posicionBloque;
		this.bloquePadre = bloquePadre;
		this.marcoActivacion = marcoActivacion;
		
		if(bloquePadre != null) { //Si provenimos de un bloque padre
			if(marcoActivacion) { //Si abrimos un nuevo marco de activacion
				this.proximaDireccion = 5;
				this.profundidadAnidamiento = bloquePadre.getProfundidadAnidamiento() + 1;
				this.ssp = 5;
			} else { //Si no -> ambito local
				this.proximaDireccion = bloquePadre.getProximaDireccion();
				this.profundidadAnidamiento = bloquePadre.getProfundidadAnidamiento();
				this.ssp = 0;
			}
		} else { //Cuando somos el primer bloque -> comenzamos en la primera posicion de la memoria
			this.proximaDireccion = 0;
			this.profundidadAnidamiento = 0;
			this.ssp = 0;
		}
	}
	
	public int getDireccionIdentificador(String iden) {
		return direccionIdentificadores.get(iden);
	}
	
	public void insertaIdentificador(String identificador,int tamanoIdentificador) {
		direccionIdentificadores.put(identificador, proximaDireccion);
		proximaDireccion += tamanoIdentificador;
		
		if(!marcoActivacion) tamanoBloque += tamanoIdentificador;
		actualizarSsp(proximaDireccion);
	}
	
	private void actualizarSsp(int nuevoSsp) {
		if(marcoActivacion) ssp = Math.max(ssp, nuevoSsp);
		else bloquePadre.actualizarSsp(nuevoSsp);
	}
	
	public void insertaTipo(String nombreTipo, int tamanoTipo) {
		tipos.put(nombreTipo, tamanoTipo);
	}
	
	public int getTipo(String nombreTipo) {
		if(tipos.containsKey(nombreTipo)) {
			return tipos.get(nombreTipo);
		}
		else return bloquePadre.getTipo(nombreTipo);
	}
	
	public int getTamanoBloque() {
		return tamanoBloque;
	}
	
	public int getPosicionBloque() {
		return posicionBloque;
	}

	public Bloque getBloquePadre() {
		return bloquePadre;
	}
	
	public int getProximaDireccion() {
		return proximaDireccion;
	}
	
	public void setProximaDireccion(int proximaDireccion) {
		this.proximaDireccion = proximaDireccion;
	}
	
	public int getProfundidadAnidamiento() {
		return profundidadAnidamiento;
	}
	
	public int getSsp() {
		return ssp;
	}
	
	public void setSsp(int ssp) {
		this.ssp = ssp;
	}
	
	public boolean getMarcoActivacion() {
		return marcoActivacion;
	}
	
	//Utilizado para debuggear
	public String toString() {
		String aux = "";
		
		aux += "Direccion identificadores: \n";
		for(Map.Entry<String, Integer> entry : direccionIdentificadores.entrySet()) {
			aux += "\t(" + entry.getValue() + ") " + entry.getKey() + "\n";
		}
		
		aux += "Tamano de los tipos: \n";
		for(Map.Entry<String, Integer> entry : tipos.entrySet()) {
			aux += "\t" + entry.getKey() + " " + entry.getValue() + "\n";
		}
		
		if(marcoActivacion) aux += "SSP: " + ssp + "\n";
		else aux += "Tamano Bloque: " + tamanoBloque + "\n";
		
		aux += "Posicion Bloque: " + posicionBloque + "\n";
		if(bloquePadre != null) aux += "Bloque Padre: " + bloquePadre.getPosicionBloque() + "\n";
		aux += "Proxima Direccion: " + proximaDireccion + "\n";
		aux += "Profundidad Anidamiento: " + profundidadAnidamiento + "\n";
		
		aux += "Marco Activacion: " + marcoActivacion + "\n";
		
		return aux;
	}
	
}
