package ast.T;

import ast.E.E;

public class TipoArray extends Tipo{
	private Tipo tipoBase;
	private E dimension;
	
	public TipoArray(Tipo tipoBase, E dimension) {
		this.tipoBase = tipoBase;
		this.dimension = dimension;
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
		return tipoBase.toString() + "[" + dimension.toString().substring(1, dimension.toString().length()-1) + "]";
	}
}
