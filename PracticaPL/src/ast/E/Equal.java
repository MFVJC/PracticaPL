package ast.E;

import ast.E.TipoE;

public class Equal extends EBin {
	public Equal(E opnd1, E opnd2,int fila,int columna) {
	     super(opnd1,opnd2,fila,columna); 
	}     
	public TipoE tipoExpresion() {return TipoE.EQUAL;}
	public String toString() {return "{{__EQ___}"+opnd1().toString() + opnd2().toString()+"}";}
}
