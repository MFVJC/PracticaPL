package ast.E;

public class Elev extends EBin {
   public Elev(E opnd1, E opnd2,int fila,int columna) {
	     super(opnd1,opnd2,fila,columna); 
   }     
   public TipoE tipoExpresion() {return TipoE.ELEV;}
   public String toString() {return "{{__Elev_}" + opnd1().toString() + opnd2().toString() + "}";}
}
