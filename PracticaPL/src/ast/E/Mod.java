package ast.E;

public class Mod extends EBin {
	public Mod(E opnd1, E opnd2,int fila,int columna) {
	     super(opnd1,opnd2,fila,columna); 
	}     
	public TipoE tipoExpresion() {return TipoE.ELEV;}
	public String toString() {return "{{__Mod__}" + opnd1().toString() + opnd2().toString() + "}";}
}
