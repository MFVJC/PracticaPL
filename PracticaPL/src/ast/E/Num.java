package ast.E;

import ast.E.TipoE;

public class Num extends E {
	private String v;
	public Num(String v,int fila,int columna) {
			this.v = v;   
		     this.fila = fila;
		     this.columna = columna;
	}
	public String num() {return v;}
	public TipoE tipoExpresion() {return TipoE.NUM;}   
	public String toString() {return "{" + v + "}";}  
}
