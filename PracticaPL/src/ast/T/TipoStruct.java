package ast.T;

public class TipoStruct extends Tipo{
	private String nombreStruct;
	public TipoStruct(String nombreStruct) {
		this.nombreStruct = nombreStruct;
	}
	public String getNombreStruct() {
		return nombreStruct;
	}
	@Override
	public EnumeradoTipos tipoEnumerado() {
		return EnumeradoTipos.STRUCTS;
	}

}
