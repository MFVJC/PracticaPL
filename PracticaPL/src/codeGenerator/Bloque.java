package codeGenerator;

import java.util.HashMap;
import java.util.Map;

public class Bloque {
	//mirar pag 133 en el pdf de GeneracionCodigo
	private Map<String,Integer> direccionIdentificadores = new HashMap<>();
	private Map<String,Integer> tamanoTipos = new HashMap<>();
	
	private int tamano = 0;
	private int tamanoBloque = 0;
	private int posicionBloque;
	private Bloque bloquePadre;
	//lo anterior está reservado para  el SL,DL,EP,RA y el valor de la función
	private int proximaDireccion = 5;
	//leer 6.5.1
	private int profundidadAnidamiento = 0;
	// se utiliza para modificar el SP (stack pointer)
	private int ssp;
	
	//True si el bloque pertenece a un nuevo ambito de funcion o procedimiento
	//False en caso contrario (ambitos de if, while, switch...)
	boolean ambitoFuncion;
	
	public Bloque(Bloque bloquePadre, int posicionBloque, boolean ambitoFuncion) {
		this.bloquePadre = bloquePadre;
		this.posicionBloque = posicionBloque;
		this.ambitoFuncion = ambitoFuncion;
		
		if(ambitoFuncion) this.ssp = 5;
		else this.ssp = 0;
		
		if(bloquePadre!=null) { //entonces la profundidad es != 0
			profundidadAnidamiento = bloquePadre.getProfundidadAnidamiento() + 1;
			proximaDireccion = bloquePadre.getProximaDireccion();
		}
	}
	
	public int getDireccionIdentificador(String iden) {
		return direccionIdentificadores.get(iden);
	}
	
	public void insertaIdentificador(String identificador,int tam) {
		direccionIdentificadores.put(identificador, proximaDireccion);
		proximaDireccion +=tam;
		tamanoBloque +=tam;
	}
	
	public void insertaTamanoTipo(String tipo, int tam) {
		tamanoTipos.put(tipo,tam);
	}
	
	public int getTamanoTipo(String tipo) {
		if(tamanoTipos.containsKey(tipo)) {
			return tamanoTipos.get(tipo);
		}
		return bloquePadre.getTamanoTipo(tipo);
	}
	
	public int getProfundidadAnidamiento() {
		return profundidadAnidamiento;
	}
	
	public int getProximaDireccion() {
		return proximaDireccion;
	}
}
