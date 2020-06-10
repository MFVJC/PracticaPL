package ast.T;

public class TipoPuntero extends Tipo{
	private Tipo tipoApuntado;

	public TipoPuntero(Tipo tipoApuntado) {
		this.tipoApuntado = tipoApuntado;
	}
	@Override
	public EnumeradoTipos tipoEnumerado() {
		return EnumeradoTipos.PUNTERO;
	}
	public Tipo getTipoApuntado() {
		return tipoApuntado;
	}
	
	public void setTipoApuntado(Tipo tipoApuntado) {
		this.tipoApuntado = tipoApuntado;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return tipoApuntado.toString() + "$";
	}
	
}
