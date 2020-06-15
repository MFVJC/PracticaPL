package ast.E;

import ast.SentenciaAbstracta;
import ast.E.TipoE;
import ast.T.Tipo;

public class Iden extends E {
	private String nombre;
	private Tipo tipoVariable;
	private SentenciaAbstracta referencia;
	private boolean constante =false; //true si es procedimiento
	private int fila;
	private int columna;
	private int profundidadAnidamiento = 0;
	private int direccionMemoria;
	
	public Iden(String opnd1,int fila,int columna) {
     this.nombre = opnd1;  
     this.fila = fila;
     this.columna = columna;
   }    
	public void setConstante(boolean constante) {
		this.constante = constante;
	}
	public boolean esConstante() {
		return constante;
	}
   public TipoE tipoExpresion() {return TipoE.IDEN;}
   public String toString() {
	   return "{" + nombre + "}";
   }
   public String getNombre() {
	   return nombre;
   }
   public void setTipo(Tipo tipo) {
	   tipoVariable = tipo;
   }
   public Tipo getTipo() {
	   return tipoVariable;
   }
	public SentenciaAbstracta getReferencia() {
		return referencia;
	}
	public void setReferencia(SentenciaAbstracta referencia) {
		this.referencia = referencia;
	}
	public void setPa(int pa) {
		  profundidadAnidamiento = pa;
	}
	public int getPa() {
		  return profundidadAnidamiento;
	}
	public int getDireccionMemoria() {
		return direccionMemoria;
	}
	public void setDireccionMemoria(int direccionMemoria) {
		this.direccionMemoria = direccionMemoria;
	}
	public int getFila() {
		return fila;
	}
	public void setFila(int fila) {
		this.fila = fila;
	}
	public int getColumna() {
		return columna;
	}
	public void setColumna(int columna) {
		this.columna = columna;
	}
	
	
}
