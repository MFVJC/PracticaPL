package asem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import errors.GestionErroresTiny;
import ast.SentenciaAbstracta;
import ast.T.Tipo;
public class TablaSimbolos {
	//guardo la información de que los identificadores en diferentes bloques correspondientes a los diferentes bloques diferentes donde se pueden declarar variables sin que haya error semántico.
	private List<HashMap<String,SentenciaAbstracta>> bloques = new ArrayList<>();
	private Map<String,Tipo> tiposVariables = new HashMap<>();
	public void abreBloque() {
		bloques.add(new HashMap<>());
	}
	public void cierraBloque() {
		bloques.remove(bloques.size()-1); //elimina la última tabla de símbolos.
	}
	public void anadeTipoVariable(String identificador,Tipo tipoVariable) {
		tiposVariables.put(identificador,tipoVariable);
	}
	
	public Tipo getTipoVariable(String identificador) {
		if(tiposVariables.containsKey(identificador)) {
			return tiposVariables.get(identificador);
		}
		System.out.println("Cuidado! Estás pidiendo el tipo de un identificador que ni siquiera lo tiene guardado" + identificador);
		return null;
	}
	public void insertaId(String identificador, SentenciaAbstracta sentencia) {
		HashMap<String,SentenciaAbstracta> ultimoBloque = bloques.get(bloques.size()-1);
		if(ultimoBloque.containsKey(identificador)) {
			GestionErroresTiny.errorSemantico("ERROR. La variable " + identificador + " ya ha sido declarada.",sentencia.getFila(),sentencia.getColumna());
			//Falta lanzar excepción o algo
		}else {
			ultimoBloque.put(identificador,sentencia);
		}
	}
	public SentenciaAbstracta getSentenciaDeclaracion(String identificador) {
		for(int i = bloques.size()-1;i>-1;--i) {
			if(bloques.get(i).containsKey(identificador)) {
				return bloques.get(i).get(identificador);
			}
		}
		return null;
	}
}
