package asem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import ast.SentenciaAbstracta;
import ast.E.*;
import ast.I.*;
import ast.T.*;
import errors.GestionErroresTiny;
import javafx.util.Pair;

public class AnalizadorSemantico {
	private List<I> programa;
	private TablaSimbolos tabla = new TablaSimbolos();
	
	public AnalizadorSemantico(List<I> programa) {
		this.programa = programa;
	}
	
	public boolean analizaSemantica() {
		//Vinculamos todas las instrucciones del programa
		tabla.abreBloque();
		for(I instruccion : programa) vincula(instruccion);
		tabla.cierraBloque();
		
		//Comprobamos tipos
		AtomicBoolean correcto = new AtomicBoolean(true);
	programa.forEach(x -> {correcto.set(compruebaTipos(x) && correcto.get());});
		if(correcto.get()) System.out.println("Comprobacion de Tipos sin errores");
		return correcto.get();
	}
	
	public void vincula(SentenciaAbstracta sentencia) {
		switch(sentencia.tipoSentencia()) {
			//Instrucciones
			case INSTRUCCION:
				I instruccion = (I) sentencia;
				switch(instruccion.tipoInstruccion()) {
					case ASIG:
						InstAsignacion asignacion = (InstAsignacion) sentencia;
						E iden = asignacion.getIdentificador();
						iden.setVieneDeAsignacion(true);
						//cuando llegue a un identificador comprobaré si viene de asignación
						vincula(iden);
						vincula(asignacion.getValor());
						break;
					case CALLPROC:
						InstCallProc llamadaProcedimiento = (InstCallProc) sentencia;
						Iden id  =(Iden)llamadaProcedimiento.getNombre_funcion();
						SentenciaAbstracta referenciaDeclaracion = tabla.getSentenciaDeclaracion(((Iden)llamadaProcedimiento.getNombre_funcion()).getNombre());
						if(referenciaDeclaracion!=null) {
							llamadaProcedimiento.setReferencia(referenciaDeclaracion);
							llamadaProcedimiento.getArgumentos().forEach(x -> vincula(x));
						}else {
							GestionErroresTiny.errorSemantico("El procedimiento " + llamadaProcedimiento.getNombre_funcion() + " no ha sido declarado. Solo se puede llamar a funciones declaradas anteriormente",sentencia.getFila(),sentencia.getColumna());
						}
						break;
					case DECL:
						InstDeclaracion declaracion = (InstDeclaracion) sentencia;
						Iden identificadorV = (Iden)declaracion.getIdentificador();
						identificadorV.setConstante(declaracion.isConstant());
						identificadorV.setReferencia(declaracion);
						identificadorV.setTipo(declaracion.getTipo());
						vincula(declaracion.getTipo());
						tabla.insertaId(identificadorV.getNombre(), declaracion);
						List<E> valorInicial = declaracion.getValor();
						if(valorInicial != null) valorInicial.forEach(x -> vincula(x));
						break;
					case DECLFUN:
						InstDeclFun declaracionFuncion = (InstDeclFun) sentencia;
						
						Tipo tipoFuncion = declaracionFuncion.getTipo(); //no vale si es proc
						if(tipoFuncion != null) vincula(tipoFuncion);
						Iden identificadorFuncion = (Iden)declaracionFuncion.getIdentificador();
						tabla.insertaId(identificadorFuncion.getNombre(), declaracionFuncion);
						identificadorFuncion.setConstante(true);

						tabla.abreBloque();
						List<Pair<Tipo, E>> listaParametros = declaracionFuncion.getArgs();
						for(Pair<Tipo, E> parametro : listaParametros) {

							tabla.insertaId(((Iden)parametro.getValue()).getNombre(), declaracionFuncion);
							Iden identificadorParametro = (Iden)parametro.getValue();
							identificadorParametro.setTipo(parametro.getKey());
							tabla.anadeTipoVariable(identificadorParametro.getNombre(), parametro.getKey());
							vincula(parametro.getKey());
						}
						
						List<I> cuerpoFuncion = declaracionFuncion.getCuerpo();
						cuerpoFuncion.forEach(x -> vincula(x));
						if(tipoFuncion!= null)vincula(declaracionFuncion.getReturn());
						tabla.cierraBloque();
						break;
					case IF:
						InstIf instIf = (InstIf) sentencia;
						vincula(instIf.getCondicion());
						tabla.abreBloque();
						instIf.getCuerpoIf().forEach(x->vincula(x));
						tabla.cierraBloque();
						List<I> cuerpoElse = instIf.getCuerpoElse();
						if(cuerpoElse != null) {
							tabla.abreBloque();
							instIf.getCuerpoElse().forEach(x->vincula(x));
							tabla.cierraBloque();
						}
						break;
					case STRUCT:
						InstStruct instStruct = (InstStruct) sentencia;
						tabla.insertaId(((Iden) instStruct.getIdentificador()).getNombre(), instStruct);
						tabla.abreBloque();
						instStruct.getDeclaraciones().forEach(x->vincula(x));
						tabla.cierraBloque();
						break;
					case SWITCH:
						InstSwitch instSwitch = (InstSwitch) sentencia;
				
						SentenciaAbstracta referenciaVariableSwitch = tabla.getSentenciaDeclaracion(((Iden)instSwitch.getCondicion()).getNombre());
						vincula((Iden)instSwitch.getCondicion());
						if(referenciaVariableSwitch == null) {
							GestionErroresTiny.errorSemantico("La variable " + ((Iden)instSwitch.getCondicion()).getNombre() + " no ha sido declarada",sentencia.getFila(),sentencia.getColumna());
						}else {
							instSwitch.setReferencia(referenciaVariableSwitch);
							
							List<Pair<E, List<I>>> casos = instSwitch.getCases();
							for(Pair<E, List<I>> caso : casos) {
								
								tabla.abreBloque();
								caso.getValue().forEach(x->vincula(x));
								tabla.cierraBloque();
							}
						}
						break;
					case WHILE:
						InstWhile instWhile = (InstWhile) sentencia;
						vincula(instWhile.getCondicion()); // así veo el tipo
						tabla.abreBloque();
						instWhile.getCuerpo().forEach(x-> vincula(x));
						tabla.cierraBloque();
						break;
					default:
						break;
					
				}
			
			break;
			case EXPRESION_BINARIA:
				EBin expresionBinaria = (EBin) sentencia;
				expresionBinaria.opnd1().setVieneDeAsignacion(expresionBinaria.vieneAsignacion());
				expresionBinaria.opnd2().setVieneDeAsignacion(expresionBinaria.vieneAsignacion());
	
					vincula(expresionBinaria.opnd1());
					if(expresionBinaria.tipoExpresion() != TipoE.DOT) {
						vincula(expresionBinaria.opnd2());
					}
			break;
			case EXPRESION:
			E expresion = (E) sentencia;
				
			switch(expresion.tipoExpresion()) {
				case FUNCION:
					LlamadaFuncion llamada = (LlamadaFuncion) expresion;
					SentenciaAbstracta referenciaFuncion = tabla.getSentenciaDeclaracion(((Iden) llamada.getNombreFuncion()).getNombre());
					if(referenciaFuncion == null) {
						GestionErroresTiny.errorSemantico("Llamada a función " + ((Iden) llamada.getNombreFuncion()).getNombre() + " no existente.",sentencia.getFila(),sentencia.getColumna());
					}else {
						llamada.setReferencia(referenciaFuncion);
						llamada.setTipoReturn(((InstDeclFun)referenciaFuncion).getTipo());
						llamada.getArgumentos().forEach(x->vincula(x));
					}
					break;
				case IDEN:
					Iden identificador = (Iden) expresion;
					SentenciaAbstracta dec = tabla.getSentenciaDeclaracion(identificador.getNombre());
					if((dec instanceof InstDeclaracion) && ((InstDeclaracion)dec).isConstant() && identificador.vieneAsignacion()) {
						identificador.setTipo(((InstDeclaracion)dec).getTipo());
						GestionErroresTiny.errorSemantico("El identificador " + identificador.getNombre() +" es constante por lo que no es asignable.", sentencia.getFila(), sentencia.getColumna());
					}else {
						SentenciaAbstracta refIdentificador = tabla.getSentenciaDeclaracion(identificador.getNombre());
						if(refIdentificador == null) {
							GestionErroresTiny.errorSemantico("El identificador " + identificador.getNombre() + " no ha sido declarado.",sentencia.getFila(),sentencia.getColumna());
						}else {
							if(refIdentificador instanceof InstDeclaracion) {
								tabla.anadeTipoVariable(identificador.getNombre(), ((InstDeclaracion)refIdentificador).getTipo());
								identificador.setTipo(((InstDeclaracion)refIdentificador).getTipo());
							}else if(refIdentificador instanceof InstDeclFun) { //instancia de InstDeclFun
								InstDeclFun declaracionFuncion = (InstDeclFun) refIdentificador;
								for(Pair<Tipo,E> argumento: declaracionFuncion.getArgs()) {
									Iden identificadorArgumento = (Iden)argumento.getValue();
									if(identificadorArgumento.getNombre() .equals(identificador.getNombre())) {
										identificador.setTipo(argumento.getKey());
									}
								}
								
							}else if(refIdentificador instanceof InstStruct){
								TipoStruct tipoVariable = new TipoStruct(identificador.getNombre(),identificador.getFila(),identificador.getColumna());
								tabla.anadeTipoVariable(identificador.getNombre(), tipoVariable);
								identificador.setTipo(tipoVariable);
							}else {
								System.out.println("nuevo caso");
							}
						}
					}
					break;
				case NOT:
					Not expNot = (Not) expresion;
					vincula(expNot.opnd1());
					break;
				case DOLLAR:
					Dollar asterisk = (Dollar) expresion;
					asterisk.opnd1().setVieneDeAsignacion(expresion.vieneAsignacion());
					vincula(asterisk.opnd1());
				break;
				default:
					break;
				
			}
			break;
			
			case TIPOS:
				Tipo tipo = (Tipo) sentencia;
				switch(tipo.tipoEnumerado()) {
				case PUNTERO:
					vincula(((TipoPuntero)tipo).getTipoApuntado());
					break;
				case STRUCT:
					TipoStruct tipoStruct = (TipoStruct) tipo;
					SentenciaAbstracta referenciaSentencia = tabla.getSentenciaDeclaracion(tipoStruct.getNombreStruct());
					if(referenciaSentencia == null) {
						GestionErroresTiny.errorSemantico("Struct " + tipoStruct.getNombreStruct() + " no declarado.",sentencia.getFila(),sentencia.getColumna());
					}else {
						tipoStruct.setReferencia(referenciaSentencia);
					}	
					break;
				case ARRAY:
					vincula(((TipoArray)tipo).getTipoBase());
					vincula(((TipoArray) tipo).getDimension());
					
					break;
				default:
					break;
				}
			break;
		default:
			break;
		}
	}
	
	public boolean compruebaTipos(SentenciaAbstracta sentencia) {
		if(sentencia.tipoSentencia() == EnumeradoTipoGeneral.INSTRUCCION) {
			I instruccion = (I) sentencia;
			switch(instruccion.tipoInstruccion()) {
			case ASIG:
				InstAsignacion instruccionAsignacion = (InstAsignacion) instruccion;
					if(instruccionAsignacion.getIdentificador() instanceof Iden) {
						Iden identificador = (Iden) instruccionAsignacion.getIdentificador();
						if(identificador.esConstante()) {
							GestionErroresTiny.errorSemantico("Error de tipos. El identificador " + identificador.getNombre() + " corresponde con una constante o una función por lo que no es modificable.",sentencia.getFila(),sentencia.getColumna());
						}
					}
					E iden = instruccionAsignacion.getIdentificador();
					Tipo tipoOriginal = tiposExpresion(instruccionAsignacion.getIdentificador());
					Tipo tipoAsignar = tiposExpresion(instruccionAsignacion.getValor());
					if(tipoOriginal.tipoEnumerado() == tipoAsignar.tipoEnumerado()) {
						return true;	
					}else {
						GestionErroresTiny.errorSemantico("Error de tipos en la asignación. Los tipos no coinciden. Intentando asignar a " + instruccionAsignacion.getIdentificador().toString() + " el valor " + instruccionAsignacion.getValor().toString()+ ".Tipos: " + tipoOriginal + " " + tipoAsignar,sentencia.getFila(),sentencia.getColumna());

					}
				break;
			case CALLPROC:
				InstCallProc intruccionLlamadaFuncion  = (InstCallProc) instruccion;
				SentenciaAbstracta declaracion = intruccionLlamadaFuncion.getReferencia();
				InstDeclFun declaracionFuncion = (InstDeclFun) declaracion;
				List<E> argumentos = intruccionLlamadaFuncion.getArgumentos();
				int i = 0;
				if(!(intruccionLlamadaFuncion.getNombre_funcion() instanceof Iden)) {
					GestionErroresTiny.errorSemantico("Error de tipos. El identificador de una función tiene que ser de tipo iden",sentencia.getFila(),sentencia.getColumna());
				}
				boolean correctArguments = true;
				for(Pair<Tipo,E> atributo : declaracionFuncion.getArgs()) {
					if(tiposExpresion(argumentos.get(i)).tipoEnumerado() != atributo.getKey().tipoEnumerado()) {
						correctArguments = false;
						GestionErroresTiny.errorSemantico("Error tipos. El parámetro número " + i + " no concuerda con el tipo del atributo de la función. Atributo: " + ((Iden)atributo.getValue()).getNombre(),sentencia.getFila(),sentencia.getColumna());
					}
					i++;
				}
				if(correctArguments) return correctArguments;
				break;
			case DECL:
				InstDeclaracion instruccionDeclaracion = (InstDeclaracion) instruccion;
				if(instruccionDeclaracion.getIdentificador().tipoExpresion() == TipoE.IDEN) {
					Tipo tipoDeclaracion = instruccionDeclaracion.getTipo();
					boolean correct = true;
					if(instruccionDeclaracion.getValor() != null) {//Esta inicializada
						if(tipoDeclaracion.tipoEnumerado() == EnumeradoTipos.ARRAY) { //esto creo que va a fallar si tenemos un array multidimensional
							TipoArray tipo = (TipoArray)tipoDeclaracion;
								int numDimension = tamanoArray(tipo,instruccionDeclaracion);
						
								if(numDimension != instruccionDeclaracion.getValor().size()) {
									GestionErroresTiny.errorSemantico("El número de valores debe coincidir con el tamaño del vector",sentencia.getFila(),sentencia.getColumna());
								}
								Tipo tipoValores = ((TipoArray)tipoDeclaracion).getTipoBase();
								for(E valor : instruccionDeclaracion.getValor()) {
									Tipo aux = tiposExpresion(valor);
									if(!(((TipoArray)tipoDeclaracion).getTipoBase() instanceof TipoArray) && aux.tipoEnumerado() != ((TipoArray)tipoDeclaracion).getTipoBase().tipoEnumerado()) {
										correct = false;
										GestionErroresTiny.errorSemantico("Error tipos. El tipo de la declaración no concuerda con su valor inicial. Intentando asignar al tipo " + tipoValores + " el tipo " + aux ,sentencia.getFila(),sentencia.getColumna());
										break;
									}
								}
						}else if(tipoDeclaracion.tipoEnumerado() == EnumeradoTipos.PUNTERO){
							E valor = instruccionDeclaracion.getValor().get(0);
							Tipo auxDeclaracion = tipoDeclaracion;
							Tipo auxValorInicial = tiposExpresion(valor);
							while(auxDeclaracion.tipoEnumerado() == EnumeradoTipos.PUNTERO && auxValorInicial.tipoEnumerado() == EnumeradoTipos.PUNTERO) {
								if(auxDeclaracion.tipoEnumerado() != auxValorInicial.tipoEnumerado()) {
									correct = false;
									GestionErroresTiny.errorSemantico("Error tipos. El tipo de la declaración no concuerda con su valor inicial. Asignando a " + instruccionDeclaracion.getIdentificador().toString() + " el valor " + valor.toString() + ". Con tipos " + auxDeclaracion + " y " + auxValorInicial, sentencia.getFila(),sentencia.getColumna());
								}
								auxDeclaracion = ((TipoPuntero) auxDeclaracion).getTipoApuntado();
								auxValorInicial = ((TipoPuntero) auxValorInicial).getTipoApuntado();	
							}
							//Aqui ya tendremos los tipos bases -> ultima comprobacion, si son distintos: error
							if(auxDeclaracion.tipoEnumerado() != auxValorInicial.tipoEnumerado()) {
								correct = false;
								GestionErroresTiny.errorSemantico("Error tipos. El tipo de la declaración no concuerda con su valor inicial. Asignando a " + instruccionDeclaracion.getIdentificador().toString() + " el valor " + valor.toString() + ". Con tipos " + auxDeclaracion + " y " + auxValorInicial, sentencia.getFila(),sentencia.getColumna());
							}				
						} else {
							E valor = instruccionDeclaracion.getValor().get(0);
							Tipo aux = tiposExpresion(valor);
							if(aux.tipoEnumerado() != tipoDeclaracion.tipoEnumerado()) {
								correct = false;
								GestionErroresTiny.errorSemantico("Error tipos. El tipo de la declaración no concuerda con su valor inicial. Asignando a " + instruccionDeclaracion.getIdentificador().toString() + " el valor " + valor.toString() + ". Con tipos " + tipoDeclaracion + " y " + aux,sentencia.getFila(),sentencia.getColumna());
							}
						}
					}
					return correct;
				}else {
					GestionErroresTiny.errorSemantico("Error de tipos. La variable tiene que ser necesariamente un identificador.",sentencia.getFila(),sentencia.getColumna());
				}
				break;
			case DECLFUN:
				InstDeclFun instruccionDeclaracionFuncion = (InstDeclFun) instruccion;
				Tipo tipoRealReturn = null;
				if(!(instruccionDeclaracionFuncion.getIdentificador() instanceof Iden)) {
					GestionErroresTiny.errorSemantico("Error de tipos. El identificador de una función tiene que ser de tipo iden",sentencia.getFila(),sentencia.getColumna());
				}
				if(instruccionDeclaracionFuncion.getTipo() != null)
					tipoRealReturn = tiposExpresion(instruccionDeclaracionFuncion.getReturn());
				
				if(instruccionDeclaracionFuncion.getIdentificador().tipoExpresion() == TipoE.IDEN) {
					AtomicBoolean correcto = new AtomicBoolean(true);
					
					if(!(tipoRealReturn instanceof TipoArray) && tipoRealReturn != null && tipoRealReturn.tipoEnumerado() != instruccionDeclaracionFuncion.getTipo().tipoEnumerado()){
						GestionErroresTiny.errorSemantico("Error de tipos. El tipo del return no coincide con el de la función.",sentencia.getFila(),sentencia.getColumna());
					}
					instruccionDeclaracionFuncion.getCuerpo().forEach(x -> {correcto.set(compruebaTipos(x) && correcto.get());});
					if(instruccionDeclaracionFuncion.getTipo() != null) correcto.set(correcto.get() && tipoRealReturn == instruccionDeclaracionFuncion.getTipo());
					return correcto.get();
				} else {
					GestionErroresTiny.errorSemantico("Error de tipos. El nombre de la funcion tiene que ser necesariamente un identificador.",sentencia.getFila(),sentencia.getColumna());
				}
				
				break;
			case IF:
				InstIf instruccionIf = (InstIf) instruccion;
				if(tiposExpresion(instruccionIf.getCondicion()).tipoEnumerado() == EnumeradoTipos.BOOLEAN){
				AtomicBoolean correcto = new AtomicBoolean(true);
				instruccionIf.getCuerpoIf().forEach(x -> {correcto.set(compruebaTipos(x) && correcto.get());});
				
				if(instruccionIf.getCuerpoElse() != null) {
					instruccionIf.getCuerpoElse().forEach(x -> {correcto.set(compruebaTipos(x) && correcto.get());});
				}
				return correcto.get();
				}else {
					GestionErroresTiny.errorSemantico("Error de tipos. La condición del if debe ser booleana",sentencia.getFila(),sentencia.getColumna());
				}
				break;
			case STRUCT:
				InstStruct instruccionStruct = (InstStruct) instruccion;
				if(!(instruccionStruct.getIdentificador() instanceof Iden)) {
					GestionErroresTiny.errorSemantico("Error de tipos. El identificador del struct debe ser de tipo Iden",sentencia.getFila(),sentencia.getColumna());
				}
				AtomicBoolean correcto = new AtomicBoolean(true);
				instruccionStruct.getDeclaraciones().forEach(x -> {correcto.set(compruebaTipos(x) && correcto.get());});
				return correcto.get();
			case SWITCH:
				InstSwitch instruccionSwitch = (InstSwitch) instruccion;
				E condicion = instruccionSwitch.getCondicion();
				Tipo tipoCondicion = tiposExpresion(condicion);
				AtomicBoolean correct = new AtomicBoolean(true);
				for(Pair<E,List<I>> caso : instruccionSwitch.getCases()) {
					if(!caso.getValue().isEmpty()) {
						if(caso.getKey()!=null) {
							if(tipoCondicion.tipoEnumerado() != tiposExpresion(caso.getKey()).tipoEnumerado()) {
								correct.set(false);
								GestionErroresTiny.errorSemantico("Error de tipos. Los tipos de los case deben coincidir con la expresión del switch.",sentencia.getFila(),sentencia.getColumna());
							
							}
						}
						caso.getValue().forEach(x -> {correct.set(compruebaTipos(x) && correct.get());});
					}
				}

				return correct.get();
			case WHILE:
				InstWhile instruccionWhile = (InstWhile) instruccion;
				if(tiposExpresion(instruccionWhile.getCondicion()).tipoEnumerado() == EnumeradoTipos.BOOLEAN) {
					AtomicBoolean correctWhile = new AtomicBoolean(true);
					instruccionWhile.getCuerpo().forEach(x -> {correctWhile.set(compruebaTipos(x) && correctWhile.get());});
					return correctWhile.get();
				}else {
					GestionErroresTiny.errorSemantico("Error de tipos. La condición del while debe ser booleana.",sentencia.getFila(),sentencia.getColumna());
				}
				break;
			default:
				break;
			
			}
			
		}
		
		return false;
	}
	
	public Tipo tiposExpresion(SentenciaAbstracta sentencia) {
		switch(sentencia.tipoSentencia()) {
			case EXPRESION_BINARIA:
				EBin ebin = (EBin) sentencia;
				E operando1= ebin.opnd1();
				E operando2 = ebin.opnd2();
				Tipo tipoOperando1 = tiposExpresion(operando1);
				Tipo tipoOperando2 = null;
				if(ebin.tipoExpresion() != TipoE.DOT) {
					tipoOperando2 = tiposExpresion(operando2);
				}
				if(tipoOperando1 == null  || (ebin.tipoExpresion()!= TipoE.DOT && tipoOperando2 ==null)) {
					System.out.println("Devuelven null cuando eso nunca debería pasar. ha tenido que haber algún fallo previo. Operandos: " + operando1.toString() + " y " + operando2.toString());
					if(tipoOperando1 == null)System.out.println(operando1 +" "+ operando1.tipoExpresion());
					if(tipoOperando2==null)System.out.println(operando2 + " " + operando2.tipoExpresion() + ((Iden)operando2).getTipo());
				}
				else {
				if (tipoOperando1.tipoEnumerado() != EnumeradoTipos.ERROR || (ebin.tipoExpresion() != TipoE.DOT && tipoOperando2.tipoEnumerado() != EnumeradoTipos.ERROR)) {
				
				switch(ebin.tipoExpresion()) {
				case AND:
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.BOOLEAN && tipoOperando2.tipoEnumerado()==EnumeradoTipos.BOOLEAN) {
						return new TipoBoolean();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Uno de los operandos del AND no es booleano. Operandos: " + operando1.toString() + " y " + operando2.toString(),sentencia.getFila(),sentencia.getColumna());
					break;
				case DIV:
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
						return new TipoInt();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Uno de los operandos de la división no es entero. Operandos: " + operando1.toString() + " y " + operando2.toString(),sentencia.getFila(),sentencia.getColumna());
		
					break;
				case DOT:
					//
					if(tipoOperando1.tipoEnumerado() == EnumeradoTipos.STRUCT) {
						Iden atributo = (Iden) ebin.opnd2();
						TipoStruct tipoStruct = (TipoStruct) tipoOperando1;
						InstStruct sentenciaStruct = (InstStruct) tipoStruct.getReferencia(); // esto es null
						if(sentenciaStruct == null) {
							GestionErroresTiny.errorSemantico("Debes especificar un struct definido y no un struct general. Struct: " + operando1,sentencia.getFila(),sentencia.getColumna());
						}
						else{
							
						
						for(I instruccion : sentenciaStruct.getDeclaraciones()) {
							if(instruccion instanceof InstDeclaracion) {
								Iden atributoStruct = (Iden)((InstDeclaracion) instruccion).getIdentificador();
								if(atributoStruct.getNombre() .equals(atributo.getNombre())) {
									//Entonces si que existe la variable
									return atributoStruct.getTipo();
								}
							}
						}	
						}
					}
						GestionErroresTiny.errorSemantico("Error de tipos. Tipo de operandos inválido para el . Operandos: " + operando1.toString() + " y " + operando2.toString(),sentencia.getFila(),sentencia.getColumna());
					
					
					break;
				case ELEV:
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
						return new TipoInt();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Tipo de operandos inválido para el operador **. Operandos: " + operando1.toString() + " y " + operando2.toString(),sentencia.getFila(),sentencia.getColumna());
		
					break;
				case EQUAL:
					//podemos tener booleanos o enteros
					if((tipoOperando1.tipoEnumerado() == EnumeradoTipos.BOOLEAN || tipoOperando1.tipoEnumerado() == EnumeradoTipos.INT) && tipoOperando1.tipoEnumerado() == tipoOperando2.tipoEnumerado() ) {
						return new TipoBoolean();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la comparación de igualdad no coinciden o no son válidos. Operandos: " + operando1.toString() + " y " + operando2.toString(),sentencia.getFila(),sentencia.getColumna());
					break;
				case GREATEREQUAL:
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
						return new TipoBoolean();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la comparación de mayor-igual no son enteros. Operandos: " + operando1.toString() + " y " + operando2.toString(),sentencia.getFila(),sentencia.getColumna());
					break;
				case GREATERTHAN:
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
						return new TipoBoolean();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la comparación de mayor no son enteros. Operandos: " + operando1.toString() + " y " + operando2.toString(),sentencia.getFila(),sentencia.getColumna());
		
					break;
				case LESSEQUAL:
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
						return new TipoBoolean();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la comparación de menor-igual no son enteros. Operandos: " + operando1.toString() + " y " + operando2.toString(),sentencia.getFila(),sentencia.getColumna());
					break;
				case LESSTHAN:
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
						return new TipoBoolean();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la comparación de menor no son enteros. Operandos: " + operando1.toString() + " y " + operando2.toString(),sentencia.getFila(),sentencia.getColumna());
					break;
				case MUL:
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
						return new TipoInt();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la multiplicación no son enteros. Operandos: " + operando1.toString() + " y " + operando2.toString(),sentencia.getFila(),sentencia.getColumna());
					break;
				case NOTEQUAL:
					if((tipoOperando1.tipoEnumerado() == EnumeradoTipos.BOOLEAN || tipoOperando1.tipoEnumerado() == EnumeradoTipos.INT) && tipoOperando1.tipoEnumerado() == tipoOperando2.tipoEnumerado() ) {
						return new TipoBoolean();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la comparación de desigualdad no coinciden o no son válidos. Operandos: " + operando1.toString() + " y " + operando2.toString(),sentencia.getFila(),sentencia.getColumna());
					break;
				case OR:
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.BOOLEAN && tipoOperando2.tipoEnumerado()==EnumeradoTipos.BOOLEAN) {
						//los dos operandos son booleanos entonces devolvemos un booleano
						return new TipoBoolean();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Uno de los operandos del OR no es booleano. Operandos: " + operando1.toString() + " y " + operando2.toString(),sentencia.getFila(),sentencia.getColumna());
					break;
				case RESTA:
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
						return new TipoInt();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la resta no son enteros. Operandos: " + operando1.toString() + " y " + operando2.toString(),sentencia.getFila(),sentencia.getColumna());
					break;
				case SQUAREBRACKET:
					if(tipoOperando1.tipoEnumerado() == EnumeradoTipos.ARRAY && tipoOperando2.tipoEnumerado() == EnumeradoTipos.INT) {
						return (((TipoArray) tipoOperando1).getTipoBase());
					} else if(operando1.tipoExpresion() == TipoE.DOLLAR && tipoOperando2.tipoEnumerado() == EnumeradoTipos.INT) {
						//Si lo de la izquierda es un puntero, devolvemos su tipo base (el que ya nos da el dollar)
						return tipoOperando1;
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Se esta accediendo a un array erroneamente. Operandos: " + operando1.toString() + " y " + operando2.toString(),sentencia.getFila(),sentencia.getColumna());
					break;
				case SUMA:
					//hay alguno que es null
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
						return new TipoInt();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la suma no son enteros. Operandos: " + operando1.toString() + " y " + operando2.toString(),sentencia.getFila(),sentencia.getColumna());
					break;
				default:
					break;
				
				}
				}
				break;
				}
			case EXPRESION:
				E expresion = (E) sentencia;
				switch(expresion.tipoExpresion()) {
				case DOLLAR:
					Dollar dollarExp = (Dollar) expresion;
					Tipo tipoPuntero = tiposExpresion(dollarExp.opnd1());
					if(tipoPuntero.tipoEnumerado() == EnumeradoTipos.PUNTERO) {
						Tipo tipoApuntado = ((TipoPuntero) tipoPuntero).getTipoApuntado();
						return tipoApuntado;
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Intentando accede con el operador $ a un identificador que no es un puntero.", sentencia.getFila(), sentencia.getColumna());
					break;
				case NEW:
					New newExp = (New)expresion;
					if(tiposExpresion(newExp.getTam()).tipoEnumerado() == EnumeradoTipos.INT) {
						return new TipoPuntero(newExp.getTipo(),sentencia.getFila(),sentencia.getColumna());
					}
					break;
				case BASICFALSE:
					return new TipoBoolean();
				case BASICTRUE:
					return new TipoBoolean();
				case NUM:
					return new TipoInt();
				case FUNCION:
					LlamadaFuncion llamada = (LlamadaFuncion) expresion;
					List<E> argumentos = llamada.getArgumentos(); 
					List<Tipo> tiposLlamada = new ArrayList<>();
					if(!(llamada.getNombreFuncion() instanceof Iden)) {
						GestionErroresTiny.errorSemantico("El nombre de la función ha de ser un identificador", sentencia.getFila(), sentencia.getColumna());
					}
					for(E argumento: argumentos) {
						tiposLlamada.add(tiposExpresion(argumento));
					}
					InstDeclFun declaracionFuncion = (InstDeclFun) llamada.getReferencia();
					int i = 0;
					boolean coincidenTipos = true;
					for(Pair<Tipo,E> atributo : declaracionFuncion.getArgs()) {
						if(atributo.getKey().tipoEnumerado() != tiposLlamada.get(i).tipoEnumerado()){
							coincidenTipos = false;
							GestionErroresTiny.errorSemantico("Error de tipos. El tipo del parámetro " + i + " no coincide con el del respectivo argumento " + ((Iden)atributo.getValue()).getNombre(),sentencia.getFila(),sentencia.getColumna());
						}
						i++;
					}
					if(coincidenTipos) {
						return llamada.getTipoReturn();
					}
					break;
				case IDEN:
					Iden identificador = (Iden) expresion;
					return identificador.getTipo();
				case NOT:
					Not not  = (Not) expresion;
					if(tiposExpresion(not.opnd1()).tipoEnumerado() == EnumeradoTipos.BOOLEAN) {
						return new TipoBoolean();
					}
					GestionErroresTiny.errorSemantico("El operando de un NOT debe ser booleano",sentencia.getFila(),sentencia.getColumna());
					break;
				default:
					break;
				
				}
				break;
			default:
				break;
				}
		
		return new TipoError();
	}
	public int tamanoArray(TipoArray array,SentenciaAbstracta sentencia) {
		Tipo tipo = array;
		int tamanoTotal =1 ;
		while(tipo  instanceof TipoArray ) {
			if(((TipoArray)tipo).getDimension().tipoExpresion() != TipoE.NUM) {
				GestionErroresTiny.errorSemantico("Error tipos. En la declaración de un vector, su dimensión debe ser un número",sentencia.getFila(),sentencia.getColumna());
			}else {
				Num dimension = (Num)((TipoArray)tipo).getDimension();
				int numDimension = Integer.parseInt(dimension.num());
				tamanoTotal*=numDimension;
			}
			tipo = ((TipoArray)tipo).getTipoBase();
		}
		return tamanoTotal;
	}
	}
