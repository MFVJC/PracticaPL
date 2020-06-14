package codeGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bloque {
	//Guardamos el nombre y la direccion en la que esta almacenado cada identificador
	private Map<String,Integer> direccionIdentificadores = new HashMap<>();
	
	//Guardamos el tipo y el tamano de cada tipo nuevo declarado
	private Map<String,Integer> tamanoTipos = new HashMap<>();
	
	//Guardamos las dimensiones de los arrays declarados, para poder acceder a ellos posteriormente
	private Map<String, List<Integer>> dimensionesArrays = new HashMap<>();
	
	//Guardamos las direcciones relativas (con respecto a la direccion de inicio del struct) de los campos de los structs declarados.
	//Para ello, tenemos un mapa de clave el nombre del struct, y valor un mapa de clave nombre de los campos y valor la direcciones relativas de estos
	private Map<String, Map<String, Integer>> camposStructs = new HashMap<>();
	
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
	
	//Funcion para mantener el ssp actualizado cada vez que introducimos una nueva declaracion en identificadorDirecciones
	private void actualizarSsp(int nuevoSsp) {
		if(marcoActivacion) ssp = Math.max(ssp, nuevoSsp);
		else bloquePadre.actualizarSsp(nuevoSsp);
	}
	
	//Funcion para acceder a la direccion de un identificador, si no se encuentra en el bloque actual, buscamos en su bloque padre
	public int getDireccionIdentificador(String iden) {
		if(direccionIdentificadores.containsKey(iden)) return direccionIdentificadores.get(iden);
		else return bloquePadre.getDireccionIdentificador(iden);
	}
	
	//Funcion para insertar un identificador y su direccion en el bloque
	public void insertaIdentificador(String identificador,int tamanoIdentificador) {
		direccionIdentificadores.put(identificador, proximaDireccion);
		proximaDireccion += tamanoIdentificador;
		
		if(!marcoActivacion) tamanoBloque += tamanoIdentificador;
		actualizarSsp(proximaDireccion);
	}

	//Funcion para acceder al tamano que ocupa un tipo, si no se encuentra en el bloque actual, buscamos en su bloque padre
	public int getTamanoTipo(String nombreTipo) {
		if(tamanoTipos.containsKey(nombreTipo)) return tamanoTipos.get(nombreTipo);
		else return bloquePadre.getTamanoTipo(nombreTipo);
	}
	
	//Funcion para insertar un nuevo tipo y su tamano
	public void insertaTamanoTipo(String nombreTipo, int tamanoTipo) {
		tamanoTipos.put(nombreTipo, tamanoTipo);
	}
	
	//Funcion para obtener las dimensiones de un array
	public List<Integer> getDimensionesArray(String nombreArray) {
		if(dimensionesArrays.containsKey(nombreArray)) return dimensionesArrays.get(nombreArray);
		else return bloquePadre.getDimensionesArray(nombreArray);
	}
	
	//Funcion para insertar las dimensiones de un array
	public void insertaDimensionesArray(String nombreArray, List<Integer> dimensiones) {
		dimensionesArrays.put(nombreArray, dimensiones);
	}
	
	//Funcion para obtener la direccion absoluta de un elemento de un array. 
	public int getDireccionElementoArray(String nombreArray, List<Integer> indices) throws Exception{
		List<Integer> dimensiones = getDimensionesArray(nombreArray);
		String errorMessage = "Error de ejecucion al acceder al vector " + nombreArray + ". Indice (";
		//Si la lista de indices no es de la misma longitud que la lista de dimensiones del array => ERROR
		if(indices.size() != dimensiones.size()) return -1;
		else {
			int direccion = getDireccionIdentificador(nombreArray);
			for(int i = 0; i < indices.size(); i++) {
				 //Si algun indice >= dimension => Index out of range Exception!
				if(indices.get(i) >= dimensiones.get(i)) throw new Exception(errorMessage + indices.get(i) + ") fuera de rango (" + dimensiones.get(i) + ")");
				else {
					if(i == indices.size() - 1) direccion += indices.get(i); //Si es el ultimo indice, sumamos solo el indice
					else direccion += dimensiones.get(i) * indices.get(i); //Si no es el ultimo indice sumamos el (indice*dimension)
				}
			}
			return direccion;
		}
	}
	
	//Funccion para obtener la direccion relativa de un campo de un tipo struct
	public int getCampoStruct(String nombreStruct, String nombreCampo) {
		if(camposStructs.containsKey(nombreStruct)) return camposStructs.get(nombreStruct).get(nombreCampo);
		else return bloquePadre.getCampoStruct(nombreStruct, nombreCampo);
	}
	
	//Funcion para almacenar las direcciones relativas de los campos de un struct. Recibe los tamanos de cada campo del struct
	//y calcula que direccion relativa le toca a cada uno
	public void insertaCamposStruct(String nombreStruct, Map<String, Integer> tamanoCamposStruct) {
		int direccionRelativa = 0;
		Map<String, Integer> direccionesCamposStruct = new HashMap<>();
		
		for(Map.Entry<String, Integer> campoStruct : tamanoCamposStruct.entrySet()) {
			direccionesCamposStruct.put(campoStruct.getKey(), direccionRelativa);
			direccionRelativa += campoStruct.getValue();
		}
		
		camposStructs.put(nombreStruct, direccionesCamposStruct);
	}
	
	//Funcion para obtener la direccion absoluta de un campo de un struct
	public int getDireccionCampoStruct(String nombreStruct, String nombreCampo) {
		//La direccion absoluta sera la direccion base del struct + la direccion relativa del campo buscado
		return getDireccionIdentificador(nombreStruct) + getCampoStruct(nombreStruct, nombreCampo);
	}
	
	
	
	//Getters y Setters

	
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
		for(Map.Entry<String, Integer> entry : tamanoTipos.entrySet()) {
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
