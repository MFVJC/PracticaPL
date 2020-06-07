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
}
