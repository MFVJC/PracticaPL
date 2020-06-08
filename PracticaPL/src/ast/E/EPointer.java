package ast.E;

import ast.T.Tipo;

public class EPointer extends E{
	private Tipo tipoVariable;
	//ellos lo tienen en una lista supongo por si es una lista de objetos propios
	private int tam;
	
	public EPointer(Tipo tipoVariable) {
		this.tipoVariable = tipoVariable;
	}
	public Tipo getTipo() {
		return tipoVariable;
	}
	@Override
	public TipoE tipoExpresion() {
		return TipoE.EPOINTER;
	}
}
