package codeGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asint.Main;
import ast.SentenciaAbstracta;
import ast.I.*;
import ast.T.EnumeradoTipoGeneral;
import ast.T.EnumeradoTipos;
import ast.T.Tipo;
import ast.T.TipoArray;
import ast.T.TipoPuntero;
import ast.T.TipoStruct;
import javafx.util.Pair;
import ast.E.*;

public class GeneradorCodigo {
	
	//Atributos
	
	
	private File archivoSalida ;
	
	private List<Bloque> listaBloques = new ArrayList<>();
	private Bloque bloqueActual = null;
	
	private int proximaDireccion;
	private static int ambitoActual = 0;
	private int maxPila = 0;
	private int maxAmbitos = 0;
	
	//para cada intrucción guardo en cuanto afecta al tamaño de la pila
	private List<InstruccionMaquina> codigoGenerado = new ArrayList<>();
	private List<I> programa;

	
	//Metodos
	
	public GeneradorCodigo(List<I> programa) {
		this.programa = programa;
		String fileName = Main.FILE_NAME;
		int lastIndex = fileName.lastIndexOf("/");
		String newFilename = fileName.substring(0, lastIndex+1) + "CodigoMaquina" + fileName.substring(lastIndex+1, fileName.length());
		archivoSalida = new File(newFilename);
	}
	
	public void generaCodigo() {
		try {
			//Abrimos el archivo de salida
			BufferedWriter writer = new BufferedWriter(new FileWriter(archivoSalida));
			
			//Asignamos direcciones a todas las declaraciones
			generaDireccionesPrograma();
			int i = 0;
			for(Bloque bloque : listaBloques) {
				System.out.println("----------" + "BLOQUE " + i + "----------");
				System.out.println(bloque.toString());
				i++;
			}

			
			codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.SSP,0,Integer.toString(listaBloques.get(0).getSsp())));
			//Generamos el codigo del programa
			codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.SEP,0));
			generaCodigoCuerpo(this.programa);
			int tamPila = tamanoPilaEvaluacion(1);
			codigoGenerado.get(1).setArgumento1(Integer.toString(tamPila));
			codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.STP,0));
			//insertIns("stp", 0);
			
			//Escribimos el codigo generado en el archivo de salida
			i = 0;
			for(InstruccionMaquina instruccion : codigoGenerado) {
				//Ellos generan tambien comentario en el codigo para poder leerlo facilmente
				//Quizas es buena idea, y cambiarlo en la version final
				writer.write("{" + i + "} " + instruccion.toString());
				i++;
			}
			//Cerramos el archivo de salida
			writer.close();
		} catch (IOException e) {
			System.out.println("Error al generar el archivo de salida.");
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(e.getMessage() != null) System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	//1) GENERAR DIRECCIONES
	
	//Generamos las direcciones del programa
	private void generaDireccionesPrograma() {
		crearNuevoBloque(true);
		
		for(I instruccion : this.programa) {
			generaDireccionesInstruccion(instruccion);
		}
		
		guardarBloqueActual();
		
		//codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.SSP, ));
	}
	
	//Generamos las direcciones para una lista de instrucciones.
	private void generaDireccionesCuerpo(List<I> instrucciones) {
		crearNuevoBloque(false);
		
		for(I instruccion : instrucciones) {
			generaDireccionesInstruccion(instruccion);
		}
		
		guardarBloqueActual();
	}
	
	//Generamos las direcciones de las instrucciones que declaren algo
	private void generaDireccionesInstruccion(I instruccion) {
		switch(instruccion.tipoInstruccion()) {
		case IF:
			InstIf instruccionIf = (InstIf) instruccion;
			//Direcciones de todas las declaraciones de las instrucciones de la rama if
			generaDireccionesCuerpo(instruccionIf.getCuerpoIf());
			
			//Si tiene rama else, direcciones de todas sus declaraciones
			if(instruccionIf.getCuerpoElse() != null) generaDireccionesCuerpo(instruccionIf.getCuerpoElse());
			break;
		
		case WHILE:
			InstWhile instruccionWhile = (InstWhile) instruccion;
			generaDireccionesCuerpo(instruccionWhile.getCuerpo());
			break;
		
		case SWITCH:
			InstSwitch instruccionSwitch = (InstSwitch) instruccion;
			
			//Creamos una lista cuerpoSwitch con el cuerpo de todos los cases, al fin y al cabo
			//las expresiones y la condicion del switch nos da igual, solo buscamos declaraciones!
			List<I> cuerpoSwitch = new ArrayList<I>();
			for(Pair<E, List<I>> ccase : instruccionSwitch.getCases()) {
				cuerpoSwitch.addAll(ccase.getValue());
			}
			generaDireccionesCuerpo(cuerpoSwitch);
			break;
			
		case DECL:
			InstDeclaracion instruccionDeclaracion = (InstDeclaracion) instruccion;
			String idenDeclaracion = ((Iden) instruccionDeclaracion.getIdentificador()).getNombre();
			
			//Diferenciamos dependiendo de el tipo de la declaracion
			switch(instruccionDeclaracion.getTipo().tipoEnumerado()) {
			case INT: case BOOLEAN: case PUNTERO:
				//Por el asin, siempre es un iden
				insertaIdentificadorBloqueActual(idenDeclaracion, 1);
				break;
			case STRUCT:
				//Cuando declaramos una instancia de un tipo struct, guardamos en la lista de identificadores su nombre y con su tamano
				String tipoStruct = ((TipoStruct) instruccionDeclaracion.getTipo()).getNombreStruct();
				int tamano = bloqueActual.getTamanoTipo(tipoStruct);
				
				insertaIdentificadorBloqueActual(idenDeclaracion, tamano);
				break;
			case ARRAY:
				//Cuando declaramos un array, tenemos que almacenar su identificador con el tamano y su lista de dimensiones
				Pair<Integer, Pair<Integer, List<Integer>>> informacionArray = obtenerInformacionArray(instruccionDeclaracion);
				int tamanoArray = informacionArray.getKey();
				int tamanoBaseArray = informacionArray.getValue().getKey();
				List<Integer> dimensionesArray = informacionArray.getValue().getValue();
				
				insertaIdentificadorBloqueActual(idenDeclaracion, tamanoArray);
				bloqueActual.insertaTamanoTipo(idenDeclaracion, tamanoBaseArray);
				bloqueActual.insertaDimensionesArray(idenDeclaracion, dimensionesArray);
				break;		
			default:
				break;
			}
			
			break;
			
		case STRUCT:
			//Cuando declaramos un nuevo tipo struct debemos meter su nombre con su tamano en la lista de tipos y guardar las direcciones relativas de los campos
			InstStruct instruccionStruct = (InstStruct) instruccion;
			String nombreStruct = ((Iden) instruccionStruct.getIdentificador()).getNombre();
			Pair<Integer, Map<String, Integer>> informacionStruct = obtenerInformacionStruct(instruccionStruct);
			int tamanoStruct = informacionStruct.getKey();
			Map<String, Integer> tamanoCamposStruct = informacionStruct.getValue();
			
			bloqueActual.insertaTamanoTipo(nombreStruct, tamanoStruct);
			bloqueActual.insertaCamposStruct(nombreStruct, tamanoCamposStruct);
			break;
			
		case DECLFUN:
			InstDeclFun instruccionDeclFun = (InstDeclFun) instruccion;
			instruccionDeclFun.setProfundidadAnidamiento(bloqueActual.getProfundidadAnidamiento());

			//Abrimos ambito
			crearNuevoBloque(true);
			
			//Asignamos direccion a cada parametro
			for(Pair<Tipo, E> argumento : instruccionDeclFun.getArgs()) {
				//Transformamos cada argumento en una declaracion (REVISAR)
				InstDeclaracion argumentoDeclaracion = new InstDeclaracion(false, argumento.getKey(), argumento.getValue(), null,((Iden)argumento.getValue()).getFila(),((Iden)argumento.getValue()).getColumna());	
				generaDireccionesInstruccion(argumentoDeclaracion);
			}
			
			//Asignamos direccion a cada instruccion del cuerpo (no llamamos a 
			//la funcion generaDireccionesCuerpo porque generariamos otro ambito)
			for(I instr : instruccionDeclFun.getCuerpo()) {
				generaDireccionesInstruccion(instr);
			}
			
			//Cerramos ambito
			guardarBloqueActual();
			
			break;
		
		default:
			
			break;
		}
	}
	
	

	//2) GENERAR CODIGO
	
	//Genera codigo para una lista de instrucciones. Usado para generar el codigo del programa completo,
	//pero tambien usado para generar listas de instrucciones del cuerpo de un if o de una funcion
	private void generaCodigoCuerpo(List<I> instrucciones) throws Exception {
		for(I instruccion : instrucciones) {
			//Generamos codigo para la instruccion que toca
			//Aqui ellos hacen diferencia entre declaracion funcion y otras...
			//Creo que no hace falta esta diferencia, pues lo hacemos en el switch de despues.
			generaCodigoInstruccion(instruccion);
		}
	}
	
	//Genera el codigo para una instruccion concreta
	private void generaCodigoInstruccion (I instruccion) throws Exception {
		switch(instruccion.tipoInstruccion()) {
			case ASIG:
				InstAsignacion instruccionAsignacion = (InstAsignacion) instruccion;
				//voy a ver el caso de vectores en otra funcion
				generaCodigoLeft(instruccionAsignacion.getIdentificador());
				generaCodigoExpresion(instruccionAsignacion.getValor());
				if(instruccionAsignacion.getValor().tipoExpresion() != TipoE.NEW) { //Si es un new, no hay que hacer sto.
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.STO,-2));	
				}
				break;
		
			case CALLPROC:
				InstCallProc llamadaFuncion = (InstCallProc) instruccion;
				List<E> listaArgumentos = llamadaFuncion.getArgumentos();
				InstDeclFun declaracionFuncion2 = (InstDeclFun) llamadaFuncion.getReferencia();
				List<Pair<Tipo,E>> parametros = new ArrayList<>();
				parametros = declaracionFuncion2.getArgs();
				//JORGE
				int diferenciaPA = declaracionFuncion2.getProfundidadAnidamiento() - getBloqueNivelActual().getProfundidadAnidamiento() - 1 ;
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.MST,5,Integer.toString(diferenciaPA)));
				int i=0;
				for(Pair<Tipo,E> parametro: parametros) {
					//Solo necesito l
					Tipo tipoParametro = parametro.getKey();
					if(tipoParametro.tipoEnumerado() == EnumeradoTipos.PUNTERO) {
						generaCodigoLeft(listaArgumentos.get(i)); //se carga la dirección de memoria del argumento
					}else if(tipoParametro.tipoEnumerado() == EnumeradoTipos.STRUCT || tipoParametro.tipoEnumerado() == EnumeradoTipos.ARRAY) {
						generaCodigoLeft(listaArgumentos.get(i)); //guardas la direccion
						int tamanoTipo =0;
						int direccion = 0;
						if(tipoParametro instanceof TipoStruct) {
							TipoStruct tipoStruct2 = (TipoStruct)tipoParametro;
							direccion =getBloqueNivelActual().getDireccionIdentificador(tipoStruct2.getNombreStruct());
							tamanoTipo = getBloqueNivelActual().getTamanoTipo(tipoStruct2.getNombreStruct());
						}else {
							tamanoTipo = getBloqueNivelActual().getTamanoTipo(((Iden) parametro.getValue()).getNombre());
							List<Integer> dimensionesArray = getBloqueNivelActual().getDimensionesArray(((Iden)parametro.getValue()).getNombre());
							int aux = 1;
							for(int dim : dimensionesArray) aux *= dim;
							tamanoTipo *= aux;
						}
						//codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDC,1,Integer.toString(direccion)));
						codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.MOVS,tamanoTipo-1,Integer.toString(tamanoTipo)));
					}else {
						//es un parámetro simple
						generaCodigoExpresion(listaArgumentos.get(i)); // no entiendo exactamente porque vale solo esto
					}
					i++;
				}

				Iden identificadorFuncion2 = (Iden)declaracionFuncion2.getIdentificador();
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.CUP,0,Integer.toString(declaracionFuncion2.getTamanoArgumentos()),Integer.toString(declaracionFuncion2.getLineaCodigoInicial())));
				
				break;
			case DECL:
				InstDeclaracion instruccionDeclaracion = (InstDeclaracion) instruccion;
				Iden identificadorVariable = (Iden) instruccionDeclaracion.getIdentificador();
				if(instruccionDeclaracion.getValor() != null) { 
					switch(instruccionDeclaracion.getTipo().tipoEnumerado()) {
					case ARRAY:
						TipoArray tipoArray = (TipoArray)instruccionDeclaracion.getTipo();
						if(tipoArray.getTipoBase().tipoEnumerado() == EnumeradoTipos.PUNTERO || tipoArray.getTipoBase().tipoEnumerado() == EnumeradoTipos.STRUCT) {
							System.out.println("Operación no soportada.");
						}else{
						
							int tamanoTipo = getBloqueNivelActual().getTamanoTipo(identificadorVariable.getNombre());
							int direccion = getBloqueNivelActual().getDireccionIdentificador(identificadorVariable.getNombre());
							List<E> valoresIniciales = instruccionDeclaracion.getValor();
							for(E valor: valoresIniciales) {
								codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDA,1,"0", Integer.toString(direccion)));
								generaCodigoExpresion(valor);
								codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.STO, -2));
								direccion+=tamanoTipo;
								
							}
						}
						break;
					case STRUCT:
						instruccionDeclaracion.setConstant(false);
						instruccionDeclaracion.setValor(null);
						
						generaCodigoInstruccion(instruccionDeclaracion);
						break;
					case PUNTERO:
						if(instruccionDeclaracion.getValor().get(0).tipoExpresion() == TipoE.NEW) {
							generaCodigoDireccionIdentificador(instruccionDeclaracion.getIdentificador());
							generaCodigoExpresion(instruccionDeclaracion.getValor().get(0));
						}
						break;

					default:
						generaCodigoDireccionIdentificador(instruccionDeclaracion.getIdentificador());
						generaCodigoExpresion(instruccionDeclaracion.getValor().get(0));
						codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.STO, -2));

						break;
					
					}
				}else if(instruccionDeclaracion.getTipo().tipoEnumerado() == EnumeradoTipos.STRUCT) {
					TipoStruct tipoStruct = (TipoStruct) instruccionDeclaracion.getTipo();
					String nombreTipoStruct = tipoStruct.getNombreStruct();
					Iden nombreStruct = (Iden) instruccionDeclaracion.getIdentificador();
						for(I instruccionDentroStruct : ((InstStruct)tipoStruct.getReferencia()).getDeclaraciones()) {							
							if(instruccionDentroStruct instanceof InstDeclaracion && ((InstDeclaracion)instruccionDentroStruct).getValor()!=null) {
								InstDeclaracion declaracionVariableStruct = (InstDeclaracion)instruccionDentroStruct;
								Iden nombreCampo= (Iden)declaracionVariableStruct.getIdentificador();
								int direccionStruct = getBloqueNivelActual().getDireccionIdentificador(nombreStruct.getNombre());
								int direccionRelativaCampo = getBloqueNivelActual().getCampoStruct(nombreTipoStruct, nombreCampo.getNombre());
								if(declaracionVariableStruct.getTipo().tipoEnumerado() == EnumeradoTipos.ARRAY) {
									List<E> valoresIniciales = declaracionVariableStruct.getValor();
									TipoArray array = (TipoArray)declaracionVariableStruct.getTipo();
									
									if(array.getTipoBase().tipoEnumerado() == EnumeradoTipos.ARRAY || array.getTipoBase().tipoEnumerado() == EnumeradoTipos.STRUCT || array.getTipoBase().tipoEnumerado() == EnumeradoTipos.PUNTERO ) { //matrices dentro de struct no
										
										System.out.println("Operación no soportada");
									}else {
										int direccionCampo = direccionStruct + direccionRelativaCampo;
										int tamanoTipo = 1;
										for(E valor: valoresIniciales) {
											//comprobar
											codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDA,1, "0", Integer.toString(direccionCampo)));
											
											generaCodigoExpresion(valor);
											codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.STO, -2));
											direccionCampo+=tamanoTipo;
										
										}
									}
								}else {
									codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDA,1, "0", Integer.toString(direccionStruct)));
									codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDC,1,Integer.toString(direccionRelativaCampo)));
									codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.ADD,-1));
									generaCodigoExpresion(declaracionVariableStruct.getValor().get(0));
									codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.STO,-2));
								}
							}
						}
							
						}
				
				break;
			case DECLFUN:
				InstDeclFun declaracionFuncion = (InstDeclFun) instruccion;
				Iden identificadorFuncion= (Iden)declaracionFuncion.getIdentificador();
				if(declaracionFuncion.getTipo() == null ) { //entonces es un procedimiento
					maxAmbitos++;
					ambitoActual = maxAmbitos;
					declaracionFuncion.setProfundidadAnidamiento(getBloqueNivelActual().getProfundidadAnidamiento());
					for(Pair<Tipo,E> parametro: declaracionFuncion.getArgs()) {
						if(parametro.getKey().tipoEnumerado() == EnumeradoTipos.STRUCT || parametro.getKey().tipoEnumerado() == EnumeradoTipos.ARRAY) {
							declaracionFuncion.aumentaTamanoArgumentos(getBloqueNivelActual().getTamanoTipo(((Iden)parametro.getValue()).getNombre()));//Esto está mal(hay que multiplicar por su dimensión en el caso de arrays)
						}else {
							declaracionFuncion.aumentaTamanoArgumentos(1);
						}
					}
					int posicionUJP =codigoGenerado.size();
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.UJP,0));
					declaracionFuncion.setLineaCodigoInicial(posicionUJP+1);
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.SSP,0,Integer.toString(getBloqueNivelActual().getSsp())));
					int posicionSEP = codigoGenerado.size();
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.SEP,0));
					generaCodigoCuerpo(declaracionFuncion.getCuerpo());
					int tamanoPilaFuncion = tamanoPilaEvaluacion(posicionSEP);
					codigoGenerado.get(posicionSEP).setArgumento1(Integer.toString(tamanoPilaFuncion));
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.RETP,0));
					codigoGenerado.get(posicionUJP).setArgumento1(Integer.toString(codigoGenerado.size()));
					ambitoActual = getBloqueNivelActual().getBloquePadre().getPosicionBloque();
					
				}else {
					maxAmbitos++;
					ambitoActual = maxAmbitos;
					declaracionFuncion.setProfundidadAnidamiento(getBloqueNivelActual().getProfundidadAnidamiento());
					int posicionUJP = codigoGenerado.size();
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.UJP,0));
					declaracionFuncion.setLineaCodigoInicial(posicionUJP+1);
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.SSP,0,Integer.toString(getBloqueNivelActual().getSsp())));
					int posicionSEP = codigoGenerado.size();
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.SEP,0));

					generaCodigoCuerpo(declaracionFuncion.getCuerpo());
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDA,1,"0","0"));//guardo la dirección del return (primera del bloque) en la pila
					generaCodigoExpresion(declaracionFuncion.getReturn());//guardo el valor de la expresión del return
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.STO,-2)); //guardo el return
					int tamanoPilaFuncion = tamanoPilaEvaluacion(posicionSEP);
					codigoGenerado.get(posicionSEP).setArgumento1(Integer.toString(tamanoPilaFuncion));
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.RETF,0));
					codigoGenerado.get(posicionUJP).setArgumento1(Integer.toString(codigoGenerado.size()));

					ambitoActual = getBloqueNivelActual().getBloquePadre().getPosicionBloque();
					
				}
				break;
			case IF:
				InstIf instruccionIf = (InstIf) instruccion;
				generaCodigoExpresion(instruccionIf.getCondicion());
				
				maxAmbitos++;
				ambitoActual = maxAmbitos;
				int momentoSaltoIf = codigoGenerado.size();
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.FJP, -1));
				generaCodigoCuerpo(instruccionIf.getCuerpoIf());
				ambitoActual = getBloqueNivelActual().getBloquePadre().getPosicionBloque(); // no entiendo bien porque llamar al padre
				if(instruccionIf.getCuerpoElse() != null) {
					maxAmbitos++;
					ambitoActual = maxAmbitos;
					int momentoSaltoElse= codigoGenerado.size();
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.UJP, 0));
					codigoGenerado.get(momentoSaltoIf).setArgumento1(Integer.toString(codigoGenerado.size())); // si tiene else salta aquí
					generaCodigoCuerpo(instruccionIf.getCuerpoElse());
					codigoGenerado.get(momentoSaltoElse).setArgumento1(Integer.toString(codigoGenerado.size()));
					//se vuelve a modifical el ámbito actual
					ambitoActual = getBloqueNivelActual().getBloquePadre().getPosicionBloque();
				}else {
					//tengo que saltar al final del códgio
					codigoGenerado.get(momentoSaltoIf).setArgumento1(Integer.toString(codigoGenerado.size()));
				}
				
				break;
			case SWITCH:
				//ixj para los cases creo
				InstSwitch instruccionSwitch = (InstSwitch) instruccion;
				E condicion = instruccionSwitch.getCondicion();
				List<Integer> listaPosicionesSalto = new ArrayList<>();
				maxAmbitos++;
				ambitoActual = maxAmbitos;
				for(Pair<E,List<I>> caso : instruccionSwitch.getCases()) {
					int momentoSaltoDefault = -1;

					if(caso.getKey()!=null) {
					generaCodigoExpresion(new Equal(condicion,caso.getKey(),caso.getKey().getFila(),caso.getKey().getColumna()));
					
					//listaPosicionesSalto.add(momentoSaltoDefault);
					momentoSaltoDefault = codigoGenerado.size();
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.FJP, -1));
					}
					
					generaCodigoCuerpo(caso.getValue());
					
					int momentoSaltoFinal = codigoGenerado.size();
					listaPosicionesSalto.add(momentoSaltoFinal);
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.UJP, 0));
					if(momentoSaltoDefault != -1) codigoGenerado.get(momentoSaltoDefault).setArgumento1(Integer.toString(codigoGenerado.size()));;
				}
				String finalSwitch = Integer.toString(codigoGenerado.size());
				for(int posicion: listaPosicionesSalto) {
					codigoGenerado.get(posicion).setArgumento1(finalSwitch); // tanto los condicionales como los no condicionales saltan al final de switch
				}
				ambitoActual = getBloqueNivelActual().getBloquePadre().getPosicionBloque();
				break;
			case WHILE:
				InstWhile instruccionWhile = (InstWhile) instruccion;
				maxAmbitos++;
				ambitoActual = maxAmbitos;
				int posicionCodigoSalto = codigoGenerado.size();
				generaCodigoExpresion(instruccionWhile.getCondicion());
				int posicionFJP = codigoGenerado.size();
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.FJP, -1));
				generaCodigoCuerpo(instruccionWhile.getCuerpo());
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.UJP, 0,Integer.toString(posicionCodigoSalto)));
				codigoGenerado.get(posicionFJP).setArgumento1(Integer.toString(codigoGenerado.size()));
				
				//actualizamos otra vez el ámbito actual
				ambitoActual = getBloqueNivelActual().getBloquePadre().getPosicionBloque();
				break;
			default:
				
				break;
		}
	}

	//Genera el codigo para una expresion concreta
	//codeR del pdf de Generación de código
	private void generaCodigoExpresion(E expresion) throws Exception {
		if(expresion!=null) {
		if(expresion.tipoSentencia() == EnumeradoTipoGeneral.EXPRESION_BINARIA && expresion.tipoExpresion() != TipoE.SQUAREBRACKET) {
			EBin expresionBinaria = (EBin)expresion;
			generaCodigoExpresion(expresionBinaria.opnd1());
			generaCodigoExpresion(expresionBinaria.opnd2());
			switch(expresionBinaria.tipoExpresion()) {
				case AND:
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.AND,-1));
					break;
				case DIV:
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.DIV,-1));
					break;
				case ELEV:
					//no está en la máquina-P
					break;
				case EQUAL:
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.EQU,-1));
					break;
				case GREATEREQUAL:
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.GEQ,-1));
					break;
				case GREATERTHAN:
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.GRT,-1));
					break;
				case LESSEQUAL:
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LEQ,-1));
					break;
				case LESSTHAN:
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LES,-1));
					break;
				case MUL:
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.MUL,-1));
					break;
				case NOTEQUAL:
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.NEQ,-1));
					break;
				case OR:
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.OR,-1));
					break;
				case RESTA:
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.SUB,-1));
					break;
				case SUMA:
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.ADD,-1));
					break;
				default:
					break;
				
			}
		}
		else {
				switch(expresion.tipoExpresion()) {
					case BASICFALSE:
						codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDC,1,"false"));
						break;
					case BASICTRUE:
						codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDC,1,"true"));
						break;
					case DOLLAR:
						generaCodigoLeft(((Dollar) expresion).opnd1());
						codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.IND,0)); //cojo el valor de esa dirección
						break;
					case DOT:
						generaCodigoLeft(expresion);
						codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.IND,0));
						break;
					case FUNCION:
						LlamadaFuncion llamada = (LlamadaFuncion) expresion;
						InstDeclFun declaracionFuncion =(InstDeclFun)llamada.getReferencia();
						List<E> listaArgumentos = llamada.getArgumentos();
						List<Pair<Tipo,E>> parametros = new ArrayList<>();
						parametros = declaracionFuncion.getArgs();
						
						int diferenciaPA =  getBloqueNivelActual().getProfundidadAnidamiento() - declaracionFuncion.getProfundidadAnidamiento() + 1 ;
						codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.MST,5,Integer.toString(diferenciaPA)));
						
						int i=0;
						for(Pair<Tipo,E> parametro: parametros) {
							//por referencia
							Tipo tipoParametro = parametro.getKey();
							if(tipoParametro.tipoEnumerado() == EnumeradoTipos.PUNTERO) {
								generaCodigoLeft(listaArgumentos.get(i)); //se carga la dirección de memoria del argumento (puntero)
							}else if(tipoParametro.tipoEnumerado() == EnumeradoTipos.STRUCT || tipoParametro.tipoEnumerado() == EnumeradoTipos.ARRAY) {
								generaCodigoExpresion(listaArgumentos.get(i)); //guardas la direccion
								int tamanoTipo =0;
								//Esto creo que no está del todo bien
								if(tipoParametro instanceof TipoStruct) {
									TipoStruct tipoStruct2 = (TipoStruct) ((Iden) listaArgumentos.get(i)).getTipo();
									tamanoTipo = getBloqueNivelActual().getTamanoTipo(tipoStruct2.getNombreStruct());
								}else {
									tamanoTipo = getBloqueNivelActual().getTamanoTipo(((Iden) listaArgumentos.get(i)).getNombre());
									List<Integer> dimensionesArray = getBloqueNivelActual().getDimensionesArray(((Iden) listaArgumentos.get(i)).getNombre());
									int aux = 1;
									for(int dim : dimensionesArray) aux *= dim;
									tamanoTipo *= aux;
								}
								codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.MOVS,tamanoTipo-1,Integer.toString(tamanoTipo)));
							}else {
								//es un parámetro simple
								generaCodigoExpresion(listaArgumentos.get(i)); // no entiendo exactamente porque vale solo est
							}
							i++;
						}
						Iden identificadorFuncion2 = (Iden)declaracionFuncion.getIdentificador();
						codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.CUP,0,Integer.toString(declaracionFuncion.getTamanoArgumentos()),Integer.toString(declaracionFuncion.getLineaCodigoInicial())));
						break;
					case IDEN:
						Iden identificador = (Iden) expresion;
						if(identificador.getTipo().tipoEnumerado() == EnumeradoTipos.STRUCT) {
							//entonces hay que generar código para cargar los atributos
							InstStruct referenciaIdentificador = (InstStruct)identificador.getReferencia(); //esto peta 
							for(I instruccion: referenciaIdentificador.getDeclaraciones()) {
								InstDeclaracion declaracionAtributo = (InstDeclaracion) instruccion;
								Iden identificadorCampo = new Iden(identificador.getNombre() + "." + ((Iden)declaracionAtributo.getIdentificador()).getNombre(),identificador.getFila(),identificador.getColumna());
								identificadorCampo.setTipo(declaracionAtributo.getTipo());
								generaCodigoDireccionIdentificador(identificadorCampo);
								codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.IND,0));
							}
						}
						else {
							generaCodigoLeft(identificador); //genera la direccion de memoria del identificador
							codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.IND,0));
						}
						break;
					case NEW:
						New nuevo = (New) expresion;
						int nuevoTamano = Integer.parseInt(((Num) nuevo.getTam()).num()); //Aqui tenemos el tamano del new entre corchetes
						Tipo nuevoTipo = nuevo.getTipo();
						switch(nuevoTipo.tipoEnumerado()) {
						case STRUCT:
							String nuevoTipoNombre = ((TipoStruct) nuevoTipo).getNombreStruct();
							nuevoTamano *= getBloqueNivelActual().getTamanoTipo(nuevoTipoNombre);
							break;
						default:
							break;
						}
						
						codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDC,1, Integer.toString(nuevoTamano)));
						codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.NEW,-2));
						break;
					case NOT:
						Not not = (Not)expresion;
						generaCodigoExpresion(not.opnd1());
						codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.NOT,0));
						
						break;
					case NUM:
						Num numero = (Num) expresion;
						codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDC,1,numero.num()));
						break;
					case SQUAREBRACKET:
						generaCodigoLeft(expresion);
						codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.IND,0));
						break;
					default:
						break;
					
				}	
			}	
		}
	}

	//Genera el codigo para obtener la dirección un identificador
	private void generaCodigoDireccionIdentificador(E expresion) {
		if(expresion.tipoExpresion() == TipoE.IDEN) {
			Iden identificador = (Iden) expresion;
			
			int diferenciaPA = 0;
			//Diferencia profundidad anidamiento del identificador

				if(getBloqueNivelActual().estaIdentificador(identificador.getNombre())) diferenciaPA = 0;
				else diferenciaPA = 1;
			
			int direccion = getBloqueNivelActual().getDireccionIdentificador(identificador.getNombre());
			codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDA,1, Integer.toString(diferenciaPA), Integer.toString(direccion)));
		}
	}
	
	//Genera el codigo para la parte izquierda de una asignación (coge la direccion donde esta guardada)
	
	//este código en realidad lo que hace es generar el código necesaario para las expresiones de la derecha y carga la dirección de la variable implicada en esta expresión
	//Es realmente el pdf de tradu 
	private void generaCodigoLeft(E expresion) throws Exception {
		switch(expresion.tipoExpresion()) {
			case IDEN:
				Iden identificador = (Iden) expresion;
				SentenciaAbstracta refIdentificador = identificador.getReferencia();
					InstDeclaracion declaracionVariable = (InstDeclaracion) refIdentificador;
					
					int diferenciaPA = 0;
					//Diferencia profundidad anidamiento del identificador
					if(getBloqueNivelActual().estaIdentificador(identificador.getNombre())) diferenciaPA = 0;
					else diferenciaPA = 1;
					
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDA,1, Integer.toString(diferenciaPA), Integer.toString(getBloqueNivelActual().getDireccionIdentificador(identificador.getNombre()))));		
				break;

			case SQUAREBRACKET:
				SquareBracket accesoArray = (SquareBracket) expresion;

				E op1 = accesoArray.opnd1();

				generaCodigoLeft(op1);
				generaCodigoExpresion(accesoArray.opnd2());
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.ADD,-1));
				
				break;
			case DOT:
				Dot dot = (Dot) expresion;
				E struct = dot.opnd1(); 
				E campo = dot.opnd2();
				Iden nombreStruct = null;
				String nombreGeneralStruct = "";
				
				if(struct.tipoExpresion() == TipoE.IDEN) {
					nombreStruct = (Iden) struct;
					nombreGeneralStruct = ((TipoStruct)nombreStruct.getTipo()).getNombreStruct();
				} else if(struct.tipoExpresion() == TipoE.DOLLAR){ //Puede que sea dollar -> cogemos el identificador del dollar
					nombreStruct = ((Iden) ((Dollar) struct).opnd1());
					nombreGeneralStruct = ((TipoStruct) ((TipoPuntero) nombreStruct.getTipo()).getTipoApuntado()).getNombreStruct();
				}				
				
				generaCodigoLeft(struct); //guarda la direccion del struct
				int direccionRelativaCampo= getBloqueNivelActual().getCampoStruct(nombreGeneralStruct,((Iden)campo).getNombre());
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDC,1,Integer.toString(direccionRelativaCampo))); //guardo la dirección relativa al campo
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.ADD,-1)); //las sumo
				break;
			case DOLLAR:
				Dollar dollar = (Dollar) expresion;
				E operando1Dollar = dollar.opnd1();
				generaCodigoLeft(operando1Dollar);
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.IND, 0));
				
				break;
			default:
			break;
		
		}
	}
	
	
	//3) FUNCIONES AUXILIARES PARA EL MANEJO DE BLOQUES
	
	private Bloque getBloqueNivelActual() {
		return listaBloques.get(ambitoActual);
	}

	private void crearNuevoBloque(boolean ambitoFuncion) {
		Bloque bloque = new Bloque(bloqueActual, listaBloques.size(), ambitoFuncion);
		listaBloques.add(bloque);
		bloqueActual = bloque;
	}
	
	private void guardarBloqueActual() {
		proximaDireccion = proximaDireccion - bloqueActual.getTamanoBloque();
		bloqueActual = bloqueActual.getBloquePadre();
	}
	
	private void insertaIdentificadorBloqueActual(String nombreIdentificador, int tamanoIdentificador) {
		this.bloqueActual.insertaIdentificador(nombreIdentificador, tamanoIdentificador);
		this.proximaDireccion += tamanoIdentificador;
	}
	
	//Para un struct dado, calcula el tamano en memoria que ocupan todos sus campos
	private Pair<Integer, Map<String, Integer>> obtenerInformacionStruct(InstStruct struct) {
		int tamanoStruct = 0;
		Map<String, Integer> tamanoCamposStruct = new HashMap<>();
		
		for(I instruccion : struct.getDeclaraciones()) { //Para cada declaracion del struct, sumamos su tamano
			InstDeclaracion declaracion = (InstDeclaracion) instruccion;
			String idenDeclaracion = ((Iden) declaracion.getIdentificador()).getNombre();
			int tamanoCampo = 0;
			
			switch(declaracion.getTipo().tipoEnumerado()) {
			case INT: case BOOLEAN: case PUNTERO:
				//Sumamos 1 (lo que ocupan int y boolean)
				tamanoCampo = 1;
				break;
			case STRUCT:
				//Sumamos el tamano del tipo struct, que lo tendremos almacenado en un bloque previo
				TipoStruct tipoStruct = (TipoStruct) declaracion.getTipo();
				tamanoCampo = bloqueActual.getTamanoTipo(tipoStruct.getNombreStruct());
				break;
			case ARRAY:
				//Sumamos el tamano del tipo array y almacenamos sus dimensiones y tamano del tipo base
				Pair<Integer, Pair<Integer, List<Integer>>> informacionArray = obtenerInformacionArray(declaracion);
				tamanoCampo = informacionArray.getKey();
				
				List<Integer> dimensionesArray = informacionArray.getValue().getValue();
				this.bloqueActual.insertaDimensionesArray(idenDeclaracion, dimensionesArray); //OJO! POSIBLE ERROR, QUIZAS DEBERIA SER idenStruct + "." + idenDeclaracion
				//PERO ESO NO NOS SIRVE PARA EL SQUAREBRACKET :/, HAY QUE PENSARLO
				int tamanoBaseArray = informacionArray.getValue().getKey();
				this.bloqueActual.insertaTamanoTipo(idenDeclaracion, tamanoBaseArray);
				break;		
			default:
				break;
			}
			tamanoCamposStruct.put(idenDeclaracion, tamanoCampo);
			tamanoStruct += tamanoCampo;
		}
		return new Pair(tamanoStruct, tamanoCamposStruct);
	}
	
	//Dada la declaracion de un array, devuelve (tamanoTotal, tamanoTipoBase, Lista Dimensiones)
	private Pair<Integer, Pair<Integer, List<Integer>>> obtenerInformacionArray(InstDeclaracion declaracionArray) {
		//Si la declaracion pasada no es de tipo array => ERROR
		if(declaracionArray.getTipo().tipoEnumerado() != EnumeradoTipos.ARRAY) return null;
		else {
			List<Integer> dimensiones = new ArrayList<Integer>();
			int tamanoArray = 1; //El tamano total sera el multiplicatorio de sus dimensiones por el tipo base
			int tamanoTipoBase = 1;
			TipoArray t = (TipoArray) declaracionArray.getTipo();
			int dimension = obtenerDimensionArray(t.getDimension());
			dimensiones.add(dimension);
			tamanoArray *= dimension;
			while(t.getTipoBase().tipoEnumerado() == EnumeradoTipos.ARRAY) { //Mientras sigan habiendo dimensiones
				t = (TipoArray) t.getTipoBase();
				
				dimension = obtenerDimensionArray(t.getDimension());
				dimensiones.add(dimension);
				tamanoArray *= dimension;
			}

			//Una vez fuera del bucle, habremos llegado al tipo base
			Tipo tipoBase = t.getTipoBase();
			switch(tipoBase.tipoEnumerado()) {
			case STRUCT:
				TipoStruct tipoStruct = (TipoStruct) tipoBase;
				tamanoTipoBase = this.bloqueActual.getTamanoTipo(tipoStruct.getNombreStruct());
				tamanoArray *= tamanoTipoBase;
				break;
			default:
				break;
			}
			return new Pair(tamanoArray, new Pair(tamanoTipoBase, dimensiones));
		}
	}
	
	private int obtenerDimensionArray(E expresion) {
		int dimension = 0;
		switch(expresion.tipoExpresion()) {
		case IDEN: //Case base 1
			Iden iden = (Iden) expresion;
			InstDeclaracion referencia = (InstDeclaracion) iden.getReferencia();
			if(referencia.isConstant()) return obtenerDimensionArray(referencia.getValor().get(0));
			else {
				System.err.println("Error de ejecucion: La dimension del array debe ser un valor constante.");
				return 0; 
			}
		case NUM: //Caso base 2
			return Integer.parseInt(((Num) expresion).num());
		//Casos recursivos
		case SUMA:
			E suma = ((Suma) expresion);
			return obtenerDimensionArray(suma.opnd1()) + obtenerDimensionArray(suma.opnd2());
		case RESTA:
			E resta = ((Resta) expresion);
			return obtenerDimensionArray(resta.opnd1()) - obtenerDimensionArray(resta.opnd2());
		case MUL:
			E mul = ((Mul) expresion);
			return obtenerDimensionArray(mul.opnd1()) * obtenerDimensionArray(mul.opnd2());
		case DIV:
			E div = ((Div) expresion);
			return obtenerDimensionArray(div.opnd1()) / obtenerDimensionArray(div.opnd2());
		default:
			System.err.println("Error de ejecucion: Operacion no soportada para la dimension de un array");
			break;
		}
		
		return dimension;
	}
	
	private int tamanoPilaEvaluacion(int posicionInicial) {
		int maxPila = 0;
		int bloquesPasados = 0;
		int tamanoPilaActual=0;
		for(int i=posicionInicial; i<codigoGenerado.size();++i) {
			InstruccionMaquina instruccion = codigoGenerado.get(i);
			if(bloquesPasados ==0) {
				if(instruccion.getTipoInstruccion() == InstruccionesMaquinaEnum.SSP) {
					bloquesPasados++;
				}else {
					tamanoPilaActual+=instruccion.getCambioPila();
				}
			}else {
				if(instruccion.getTipoInstruccion() == InstruccionesMaquinaEnum.RETF || instruccion.getTipoInstruccion() == InstruccionesMaquinaEnum.RETP) {
					bloquesPasados--;
				}
			}
			maxPila = Math.max(maxPila,tamanoPilaActual);
			
		}
		
		
		
		return maxPila;
	}
	
}