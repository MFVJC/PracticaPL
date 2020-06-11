package codeGenerator;

import java.util.HashMap;
import java.util.Map;

public class Bloque {
	//mirar pag 133 en el pdf de GeneracionCodigo
	private Map<String,Integer> posicionIdentificador = new HashMap<>();
	private Map<String,Integer> tamanoTipos = new HashMap<>();
	private int tamano = 0;
	private int posicionBloque;
	private Bloque bloquePadre;
	private int tamanoBloque = 0;
	//lo anterior está reservado para  el SL,DL,EP,RA y el valor de la función
	private int proximaDireccion = 5;
	//leer 6.5.1
	private int profundidadAnidamiento = 0;
	// se utiliza para modificar el SP (stack pointer)
	private int ssp = 0;
	
	public Bloque(Bloque bloquePadre, int posicionBloque) {
		this.bloquePadre = bloquePadre;
		this.posicionBloque = posicionBloque;
		if(bloquePadre!=null) { //entonces la profundidad es != 0
			profundidadAnidamiento = bloquePadre.getPa();
			proximaDireccion = bloquePadre.getProximaDir();
		}
	}
	public int getDireccionIdentificador(String iden) {
		return posicionIdentificador.get(iden);
	}
	public void insertaIdentificador(String identificador,int tam) {
		posicionIdentificador.put(identificador, proximaDireccion);
		proximaDireccion +=tam;
		tamanoBloque +=tam;
	}
	public void insertaTamTipo(String tipo, int tam) {
		tamanoTipos.put(tipo,tam);
	}
	
	public int getTamanoTipo(String tipo) {
		if(tamanoTipos.containsKey(tipo)) {
			return tamanoTipos.get(tipo);
		}
		return bloquePadre.getTamanoTipo(tipo);
	}
	public int getPa() {
		return profundidadAnidamiento;
	}
	public int getProximaDir() {
		return proximaDireccion;
	}
}
