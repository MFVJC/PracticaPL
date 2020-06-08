package ast.I;

import java.util.List;

import ast.E.E;
import ast.T.Tipo;
import javafx.util.Pair;

public class InstDeclFun extends I {
	//hay que cambiar el AS.java y el .cup para añadir este tipo
	//tipo del return
	private Tipo tipo;
	private String tipoF; //Si tipo es null es un procedimiento, en caso contrario es una funcion
	
	private E nombreFuncion;
	private List<Pair<String, E>> args;
	private List<I> cuerpo;
	private E valorReturn;

 public InstDeclFun(String tipoF, E iden, List<Pair<String, E>> args, List<I> cuerpo, E ret) {
		this.tipoF = tipoF;
		this.nombreFuncion = iden;
		this.args = args;
		this.cuerpo = cuerpo;
		this.valorReturn = ret;
	}
 
 public TipoI tipoInstruccion() {return TipoI.DECLFUN;}
 public String toString() {
	   String aux;
	   if(tipo != null) aux = "{{_DeclF_}{" + tipo + "}" + nombreFuncion + "{{_Args__}";
	   else aux = "{{_DeclP_}" + nombreFuncion + "{{_Args__}";
	   
	   int i = 0;
	   for(Pair<String, E> arg : args) {
		   aux += "{{_Arg" + i + "__}{" + arg.getKey() + "}";
		   aux += arg.getValue();
		   aux += "}";
		   i++;
	   }
	  
	   aux += "}{{_Cuer__}";
	   for(I ins : cuerpo) aux += ins.toString();
	   if(valorReturn != null) aux += "{{__Ret__}" + valorReturn + "}";
	   aux += "}}";
	   
	   return aux;
 }
	public String getTipoF() {
		return tipoF;
	}
	
	public void setTipoF(String tipoF) {
		this.tipoF = tipoF;
	}
	
	public E getIden() {
		return nombreFuncion;
	}
	
	public List<Pair<String, E>> getArgs() {
		return args;
	}
	
	public List<I> getCuerpo() {
		return cuerpo;
	}
	
	public E getRet() {
		return valorReturn;
	}
	public Tipo getTipo() {
		return tipo;
	}
}
