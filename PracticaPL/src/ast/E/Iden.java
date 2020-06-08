package ast.E;

import ast.E.TipoE;
import ast.T.Tipo;

public class Iden extends E {
	private String nombre;
	private Tipo tipoVariable;
   
	public Iden(String opnd1) {
     this.nombre = opnd1;  
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
}
