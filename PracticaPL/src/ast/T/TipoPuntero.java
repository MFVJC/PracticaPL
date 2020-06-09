package ast.T;

public class TipoPuntero extends Tipo{
	private Tipo claseApuntada;

	public TipoPuntero(Tipo claseApuntada) {
		this.claseApuntada = claseApuntada;
	}
	@Override
	public EnumeradoTipos tipoEnumerado() {
		return EnumeradoTipos.PUNTERO;
	}
	public Tipo getClaseApuntada() {
		return claseApuntada;
	}
	
	public void setClaseApuntada(Tipo claseApuntada) {
		this.claseApuntada = claseApuntada;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return claseApuntada.toString() + "$";
	}
	
}
