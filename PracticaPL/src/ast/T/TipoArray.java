package ast.T;

import ast.E.E;

public class TipoArray extends Tipo{
	private Tipo tipoBase;
	private E dimension;
	private int fila;
	private int columna;
	public TipoArray(Tipo tipoBase, E dimension,int fila,int columna) {
		this.tipoBase = tipoBase;
		this.dimension = dimension;
	     this.fila = fila;
	     this.columna = columna;
	}
	@Override
	public EnumeradoTipos tipoEnumerado() {
		return EnumeradoTipos.ARRAY;
	}
	public Tipo getTipoBase() {
		return tipoBase;
	}
	
	public void setTipoBase(Tipo tipoBase) {
		this.tipoBase = tipoBase;
	}
	
	public E getDimension() {
		return dimension;
	}
	public void setDimension(E dimension) {
		this.dimension = dimension;
	}
	
	@Override
	public String toString() {
		//dimension.toString().substring(1, dimension.toString().length()-1) Aparecen las dimensiones invertidas...
		//Habria que liarla para reinvertirlas, asi que por ahora no mostramos las dimensiones y ya
		return tipoBase.toString() + "[]";
	}
}
