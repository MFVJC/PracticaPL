package ast.T;

public class TipoError extends Tipo{

	
	public EnumeradoTipos tipoEnumerado() {
		return EnumeradoTipos.ERROR;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Error";
	}

}
