package ast.E;

public class Dot extends EBin {
	int fila;
	int columna;
   public Dot(E opnd1, E opnd2,int fila,int columna) {
	     super(opnd1,opnd2,fila,columna); 

   }     
   public TipoE tipoExpresion() {return TipoE.DOT;}
   public String toString() {
		  return "{{__Dot__}" + opnd1().toString() + opnd2().toString() + "}";
   }
}
