package alex;
import asint.ClaseLexica;
import errors.GestionErroresTiny;

public class ALexOperations {
  private AnalizadorLexicoTiny alex;
  public ALexOperations(AnalizadorLexicoTiny alex) {
   this.alex = alex;   
  }
  public UnidadLexica unidadId() {
     return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.IDEN, alex.lexema()); 
  } 
  public UnidadLexica unidadEnt() {
     return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.ENT,alex.lexema()); 
  } 
  public UnidadLexica unidadSuma() {
     return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.MAS, "+"); 
  } 
  public UnidadLexica unidadResta() {
     return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.MENOS, "-"); 
  } 
  public UnidadLexica unidadMul() {
     return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.POR, "*"); 
  } 
  public UnidadLexica unidadDiv() {
     return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.DIV, "/"); 
  }
  public UnidadLexica unidadMod() {
     return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.MOD, "%"); 
  }
  public UnidadLexica unidadIgual() {
     return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.IGUAL, "="); 
  } 
  public UnidadLexica unidadComa() {
     return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.COMA, ","); 
  } 
  public UnidadLexica unidadPuntoComa() {
     return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.PCOMA, ";"); 
  } 
  public UnidadLexica unidadPunto() {
	     return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.PUNTO, "."); 
  } 
  public UnidadLexica unidadEof() {
     return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.EOF, "EOF"); 
  }
  public UnidadLexica unidadAnd() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.AND, "&");
  }
  public UnidadLexica unidadOr() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.OR, "|");
  }
  public UnidadLexica unidadNot() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.NOT, "!");
  }
  public UnidadLexica unidadLT() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.LT, "<");
  }
  public UnidadLexica unidadGT() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.GT, ">");
  }
  public UnidadLexica unidadLE() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.LE, ">=");
  }
  public UnidadLexica unidadGE() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.GE, "<=");
  }
  public UnidadLexica unidadLA() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.LA, "{");
  }
  public UnidadLexica unidadLC() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.LC, "}");
  }
  public UnidadLexica unidadCA() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.CA, "[");
  }
  public UnidadLexica unidadCC() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.CC, "]");
  }
  public UnidadLexica unidadPA() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.PA, "(");
  }
  public UnidadLexica unidadPC() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.PC, ")");
  }  
  public UnidadLexica unidadSaltoLinea() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.SL, "Salto De Linea");
  }  
  public UnidadLexica unidadIf() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.IF, "If");
  }  
  public UnidadLexica unidadDo() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.DO, "Do");
  }  
  public UnidadLexica unidadElev() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.ELEV, "**");
  }  
  public UnidadLexica unidadEqual() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.EQUAL, "==");
  }  
  public UnidadLexica unidadInt() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.INT, "Int");
  }
  public UnidadLexica unidadThen() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.THEN, "Then");
  }  
  public UnidadLexica unidadTrue() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.TRUE, "True");
  }
  public UnidadLexica unidadFalse() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.FALSE, "False");
  }
  public UnidadLexica unidadElse() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.ELSE, "Else");
  }
  public UnidadLexica unidadProc() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.PROC, "Proc");
  }
  public UnidadLexica unidadConst() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.CONST, "Const");
  }
  public UnidadLexica unidadWhile() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.WHILE, "While");
  }
  public UnidadLexica unidadSwitch() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.SWITCH, "Switch");
  }
  public UnidadLexica unidadReturn() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.RETURN, "Return");
  }
  public UnidadLexica unidadBoolean() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.BOOLEAN, "Boolean");
  }
  public UnidadLexica unidadDefault() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.DEFAULT, "Default");
  }
  public UnidadLexica unidadCase() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(),ClaseLexica.CASE, "Case");
  }
  public UnidadLexica unidadStruct() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(), ClaseLexica.STRUCT, "Struct");
  }  
  public UnidadLexica unidadNew() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(), ClaseLexica.NEW, "New");
  }
  public UnidadLexica unidadPuntero() {
	  return new UnidadLexicaMultivaluada(alex.fila(), alex.columna(), ClaseLexica.PUNTERO, "Puntero");
  }
  
  
  public void error() {
    GestionErroresTiny.errorLexico(alex.fila(), alex.columna(), alex.lexema());
  }
}
