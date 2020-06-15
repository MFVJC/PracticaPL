package ast.T;

public class TipoPuntero extends Tipo{
	private Tipo tipoApuntado;
	private int fila;
	private int columna;
	public TipoPuntero(Tipo tipoApuntado,int fila,int columna) {
		this.tipoApuntado = tipoApuntado;
	     this.fila = fila;
	     this.columna = columna;
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
