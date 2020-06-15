package ast.I;

import java.util.List;

import ast.E.E;

public class InstStruct extends I {
	private E identificador;
	private List<I> declaraciones;
	private int fila;
	private int columna;
   public InstStruct(E iden, List<I> declaraciones,int fila,int columna) {
	    this.identificador = iden;
	    this.declaraciones = declaraciones;
	}
   
   public TipoI tipoInstruccion() {return TipoI.STRUCT;}
   
   public String toString() {
	   String aux = "{{_Stru__}";
	   
	   for(I declaracion : declaraciones) {
		   aux += declaracion.toString();
	   }
	   aux += "}";
	   
	   return aux;
   }

	public E getIdentificador() {
		return identificador;
	}
	
	public List<I> getDeclaraciones() {
		return declaraciones;
	}
	   
}
