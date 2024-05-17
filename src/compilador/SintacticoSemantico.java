/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:                  SEMESTRE: ENE-JUN/2024    HORA: 07-08 HRS
 *:                                   
 *:               
 *:         Clase con la funcionalidad del Analizador Sintactico
 *                 
 *:                           
 *: Archivo       : SintacticoSemantico.java
 *: Autor         : Fernando Gil  ( Estructura general de la clase  )
 *:                 Grupo de Lenguajes y Automatas II ( Procedures  )
 *: Fecha         : 03/SEP/2014
 *: Compilador    : Java JDK 7
 *: Descripción   : Esta clase implementa un parser descendente del tipo 
 *:                 Predictivo Recursivo. Se forma por un metodo por cada simbolo
 *:                 No-Terminal de la gramatica mas el metodo emparejar ().
 *:                 El analisis empieza invocando al metodo del simbolo inicial.
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *: 22/Feb/2015 FGil                -Se mejoro errorEmparejar () para mostrar el
 *:                                 numero de linea en el codigo fuente donde 
 *:                                 ocurrio el error.
 *: 08/Sep/2015 FGil                -Se dejo lista para iniciar un nuevo analizador
 *:                                 sintactico.
 *: 20/FEB/2023 F.Gil, Oswi         -Se implementaron los procedures del parser
 *:                                  predictivo recursivo de leng BasicTec.
 *: 26/Feb/2024 Aranza Gassós,
                Franciso Woo,
                Samuel Alba         -Se comenzaron a agregar los procedures 
 *: 28/Feb/2024 Aranza Gassós,
                Franciso Woo,
                Samuel Alba         -Se finalizaron los procedures 
 *: 16/Mayo/2024 Aranza Gassós,     -Se terminó de agregar la implementacion de
                                    las acciones semánticas
                 Franciso Woo,
                 Samuel Alba  

 *:-----------------------------------------------------------------------------
 */
package compilador;

import general.Linea_BE;
import general.Linea_TS;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SintacticoSemantico {

    private Compilador cmp;
    private boolean analizarSemantica = true;
    private String preAnalisis;
    private String error = "ERROR_TIPO" ;
    private String vacio = "VACIO";
    private ArrayList<String>[] arraySelect = new ArrayList[2];
    private Pattern arrChar = Pattern.compile("array\\(1\\.\\.\\d+,char\\)");
    private Pattern colmnChar = Pattern.compile("^COLUMNA\\(array\\(1\\.\\.\\d+,char\\)\\)");
   
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //
    public SintacticoSemantico(Compilador c) {
        cmp = c;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // Metodo que inicia la ejecucion del analisis sintactico predictivo.
    // analizarSemantica : true = realiza el analisis semantico a la par del sintactico
    //                     false= realiza solo el analisis sintactico sin comprobacion semantica
    public void analizar(boolean analizarSemantica) {
        this.analizarSemantica = analizarSemantica;
        preAnalisis = cmp.be.preAnalisis.complex;

        // * * *   INVOCAR AQUI EL PROCEDURE DEL SIMBOLO INICIAL   * * *
     
        PROGRAMA();
        
    }

    //--------------------------------------------------------------------------
    private void emparejar(String t) {
        if (cmp.be.preAnalisis.complex.equals(t)) {
            cmp.be.siguiente();
            preAnalisis = cmp.be.preAnalisis.complex;
        } else {
            errorEmparejar(t, cmp.be.preAnalisis.lexema, cmp.be.preAnalisis.numLinea);
        }
    }

    //--------------------------------------------------------------------------
    // Metodo para devolver un error al emparejar
    //--------------------------------------------------------------------------
    private void errorEmparejar(String _token, String _lexema, int numLinea) {
        String msjError = "";

        if (_token.equals("id")) {
            msjError += "Se esperaba un identificador";
        } else if (_token.equals("num")) {
            msjError += "Se esperaba una constante entera";
        } else if (_token.equals("num.num")) {
            msjError += "Se esperaba una constante real";
        } else if (_token.equals("literal")) {
            msjError += "Se esperaba una literal";
        } else if (_token.equals("oparit")) {
            msjError += "Se esperaba un operador aritmetico";
        } else if (_token.equals("oprel")) {
            msjError += "Se esperaba un operador relacional";
        } else if (_token.equals("opasig")) {
            msjError += "Se esperaba operador de asignacion";
        } else {
            msjError += "Se esperaba " + _token;
        }
        msjError += " se encontró " + (_lexema.equals("$") ? "fin de archivo" : _lexema)
                + ". Linea " + numLinea;        // FGil: Se agregó el numero de linea

        cmp.me.error(Compilador.ERR_SINTACTICO, msjError);
    }

    // Fin de ErrorEmparejar
    //--------------------------------------------------------------------------
    // Metodo para mostrar un error sintactico
    private void error(String _descripError) {
        cmp.me.error(cmp.ERR_SINTACTICO, _descripError);
    }

    // Fin de error
    //--------------------------------------------------------------------------
    //  *  *   *   *    PEGAR AQUI EL CODIGO DE LOS PROCEDURES  *  *  *  *
    //--------------------------------------------------------------------------
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889  
    private void PROGRAMASQL(Atributos programa) {
        Atributos declaracion = new Atributos();
        Atributos sentencias  = new Atributos();
        if (preAnalisis.equals("declare")
                || preAnalisis.equals("if")
                || preAnalisis.equals("while")
                || preAnalisis.equals("print")
                || preAnalisis.equals("assign")
                || preAnalisis.equals("select")
                || preAnalisis.equals("delete")
                || preAnalisis.equals("insert")
                || preAnalisis.equals("update")
                || preAnalisis.equals("create")
                || preAnalisis.equals("drop")
                || preAnalisis.equals("case")
                || preAnalisis.equals("end")){
            DECLARACION(declaracion);
            SENTENCIAS(sentencias);
            emparejar("end");
            // Inicia accion semantica 1
           
                if (declaracion.tipo.equals(vacio)&& sentencias.tipo.equals(vacio))
                    programa.tipo = vacio;
                else {
                    programa.tipo = error;
                    error("[PROGRAMASQL] hay errores de tipo en el programa. NO.LINEA: "+cmp.be.preAnalisis.getNumLinea());
                }
                //Termina accion semantica 1
            
        } else {
            error("[PROGRAMASQL] Inicio de programa no válido. No. Línea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889  
    private void ACTREGS(Atributos actregs) {
        Atributos igualacion = new Atributos();
        Atributos exprcond = new Atributos();
        Linea_BE id = new Linea_BE();
        
        if (preAnalisis.equals("update")) {
            emparejar("update");
            id = cmp.be.preAnalisis;
            emparejar("id");
            emparejar("set");
            IGUALACION(igualacion);
            //Inicia accion semantica {47}
           
                if(cmp.ss.checarArchivo(id.lexema+".db")){
                    cmp.ts.anadeTipo(id.entrada,"tabla");
                if ( igualacion.tipo.equals(vacio)) 
                    actregs.h = vacio;
                else {
               actregs.h = error;
               error("[ACTREGS] Error. Incompatibilidad de tipos. No. de Línea: " +id.getNumLinea());       
            }
                }else{
                  actregs.h = error;
               error("[ACTREGS] Error. El archivo mencionado no existe. No. de Línea: " + id.getNumLinea());   
                }
           
           //Termina accion semantica {47}
            emparejar("where");
            EXPRCOND(exprcond);
            //Inicia accion semantica {48}
                
                    if (actregs.h.equals(vacio) && exprcond.tipo.equals("boolean"))
                         actregs.tipo = vacio;
                    else{
                        actregs.tipo = error;
                         error("[ACTREGS] Error. Error en la condicion de actualizacion. No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
                    }
                  
                //Termina accion semantica {48}
              
       
     } else 
        error("[ACTREGS] Error. Se esperaba update. No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
        
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void COLUMNAS(Atributos columnas) {
        Atributos columnas_ = new Atributos();
        Linea_BE id = new Linea_BE();
        Matcher matcher1;
        if (preAnalisis.equals("id")) {
            // COLUMNAS-> ID COLUMNAS'
            id = cmp.be.preAnalisis;
            emparejar("id");
            matcher1 = colmnChar.matcher(cmp.ts.buscaTipo(id.entrada));
            
                if(cmp.ts.buscaTipo(id.entrada).equals("COLUMNA(int)")||
                      cmp.ts.buscaTipo(id.entrada).equals("COLUMNA(float)") ||
                        matcher1.matches())
                    columnas.h = "vacio";
                else {
                    columnas.h = error;
                    error("[INSERCION] el id mencionado es inexistente o"
                            + " no le pertenece a una columna" 
                            + cmp.be.preAnalisis.numLinea);
                }
            
            COLUMNAS_(columnas_);
            //Inicia accion semantica {53}
//            if (analizarSemantica) {
//                if(cmp.ts.buscaTipo(id.entrada).equals(null)){
//                    cmp.ts.anadeTipo(id.entrada, "COLUMNAS");
//                    columnas.h = vacio;
//            }else{
//                    columnas.h = error;
//                    error("[COLUMNAS] Error.Ya se ha declarado ese identificador. No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
//                }
//            }
           
                if(columnas.h.equals(vacio) && columnas_.tipo.equals(vacio) )
                    columnas.tipo = vacio;
                
            else{
                columnas.tipo = error;
                error("[COLUMNAS] Error. Incompatibilidad de tipos en columnas. No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
            }
           //Finaliza accion semantica {53}
    } else {
            error("[COLUMNAS] Error.Se esperaba id. No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void COLUMNAS_(Atributos columnas_) {
       Atributos columnas = new Atributos();
        if (preAnalisis.equals(",")) {
            //COLUMNAS' -> ,COLUMNAS
            emparejar(",");
            COLUMNAS(columnas);
            //Inicia accion semantica {54}
           
              columnas_.tipo = columnas.tipo;   
            
            //Finaliza accion semantica {54}
        } else {
            //COLUMNAS-> empty
            //Inicia accion semantica {58}
           
                columnas_.tipo = vacio;
            
            // Finaliza accion semantica {58}
        }
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void DECLARACION(Atributos declaracion) {
        Atributos tipo = new Atributos();
        Atributos declaracion1 = new Atributos();
        Linea_BE idvar = new Linea_BE();
        if (preAnalisis.equals("declare")) {
            //DECLARACION-> declare idvar TIPO DECLARACION 
            emparejar("declare");
            idvar = cmp.be.preAnalisis;
            emparejar("idvar");
            TIPO(tipo);
            //Inicia accion semantica {2}
                if (cmp.ts.buscaTipo(idvar.entrada).equals(""))
                {
                  cmp.ts.anadeTipo(idvar.entrada, tipo.tipo);
                  declaracion.h = vacio;
                }else{
                    declaracion.h = error;
                    error("[DECLARACION] ERROR: Redeclaracion de idvar NO.LINEA: "+ cmp.be.preAnalisis.getNumLinea());
                }
              
            //Finaliza accion semantica {2}
            DECLARACION(declaracion1);
            //Inicia accion semantica {3}
            
           if(declaracion.h.equals(vacio) && declaracion1.tipo.equals(vacio))
                   declaracion.tipo = vacio;
          else {
              declaracion.tipo = error;
             error("[DECLARACION] ERROR: Error de tipo den la declaracion de variables"
                           + "NO. LINEA: "+cmp.be.preAnalisis.getLexema());
              }
            //Finaliza ccion semantica {3}
        }else {
         //Inicia Accion semantica {4}
           
                declaracion.tipo = vacio;
            
         //Finaliza accion semantica {4}
        }
        
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void DESPLIEGUE(Atributos despliegue) {
        Atributos exparit = new Atributos();
        if (preAnalisis.equals("print")) {
            //DESPLIEGUE-> print EXPRARIT
            emparejar("print");
            EXPRARIT(exparit);
            //Inicia accion semantica {30}
            
                if(!exparit.tipo.equals(error))
                    despliegue.tipo = vacio;
                else 
                {
                 despliegue.tipo = vacio;
                 error("[DESPLIEGUE] Error. error de tipos en el despliegue No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
        
                }
            
            //Finaliza accion semantica {30}
        } else {
            error("[DESPLIEGUE] Error.Se esperaba print No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void DELREG(Atributos delreg) {
        Atributos exprcond = new Atributos();
        Linea_BE id = new Linea_BE();
        if (preAnalisis.equals("delete")) {
            // DELREG-> delete from id where EXPRCOND
            emparejar("delete");
            emparejar("from");
            id = cmp.be.preAnalisis;
            emparejar("id");
            //Inicia accion semantica {62}
           
                  if(cmp.ss.checarArchivo(id.lexema+".db")){
                      cmp.ts.anadeTipo(id.entrada, "tabla");
                       delreg.h = vacio;
                   }else{
                       delreg.h = error;
                     error("[DELREG] Error. El id no le pertenece a una tabla No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
                   }
  
            
            //Finaliza accion semantica {62}
            emparejar("where");
            EXPRCOND(exprcond);
            //Inicia accion semantica {63}
          
                if(delreg.h.equals(vacio) && exprcond.tipo.equals("boolean"))
                    delreg.tipo = vacio;
                else{
               delreg.tipo = error;
               error("[DELREG] Error. Error de tipos en en la exprecion condicional No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
               }            
            
            //Finaliza accion semantica {63}
          
        } else {
            error("[DELREG] Error. Falta la palabra reservada delete No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void EXPRESIONES(Atributos expresiones) {
        Atributos exparit = new Atributos();
         Atributos expresiones_ = new Atributos();
        if (preAnalisis.equals("num")
                || preAnalisis.equals("num.num")
                || preAnalisis.equals("idvar")
                || preAnalisis.equals("literal")
                || preAnalisis.equals("id")
                || preAnalisis.equals("("))
                 {
            //EXPRESIONES-> EXPRARIT EXPRESIONES'
            EXPRARIT(exparit);
            EXPRESIONES_(expresiones_);
            //Inicia Accion semantica {56}
                     
                         if((!exparit.tipo.equals(error))
                                 && expresiones_.tipo.equals(vacio))
                            expresiones.tipo = vacio;
                         else{
                             expresiones.tipo = error;
                             error("[EXPRESIONES] Error. Incompatibilidad de tipos en la expresion aritmetica"
                                     + " . No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
                         }
                             
                     
            
            //Finaliza accion semantuca {56}
        } else {
            error("[EXPRESIONES] Error.Se esperaba una expresión. No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void EXPRESIONES_(Atributos expresiones_) {
        Atributos expresiones = new Atributos();
        if (preAnalisis.equals(",")) {
            //EXPRESIONES -> , EXPRESIONES 
            emparejar(",");
            EXPRESIONES(expresiones);
            //Inicia accion semantica {57}
           
                expresiones_.tipo = expresiones.tipo;
            
            //Finaliza accion semantica {57}
        } else {
           //Inicia accion semantica {58}
            
                expresiones_.tipo = vacio;
            
           //Finaliza accion semantica {58}
        }
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void EXPRARIT(Atributos exparit) {
        Atributos exparit1 = new Atributos();
        Atributos operando = new Atributos();
        Atributos exprarit_ = new Atributos();
                if (preAnalisis.equals("num")
                || preAnalisis.equals("num.num")
                || preAnalisis.equals("idvar")
                || preAnalisis.equals("literal")
                || preAnalisis.equals("id")) {

            //EXPRARIT-> OPERANDO EXPRARIT'
            OPERANDO(operando);
            EXPRARIT_(exprarit_);
            //Inicia accion semantica {70} 
               if(operando.tipo != error && exprarit_.tipo != error)  
                   exparit.tipo = operando.tipo;
               else{
                   exparit.tipo = error;
                    error("[EXPRARIT] Error.Incompatibilidad de tipos en la expresión aritmética. No. de Línea: " + cmp.be.preAnalisis.getNumLinea());  
                   }
            //Finaliza accion semantica {70}
            

        } else if (preAnalisis.equals("(")) {
            //EXPRARIT -> (EXPRARIT) EXPRARIT'
            emparejar("(");
            EXPRARIT(exparit1);
            emparejar(")");
            EXPRARIT_(exprarit_);
            //Inicia accion semantica {71}
            
               if(exparit1.tipo != error && exprarit_.tipo != error)  
                   exparit.tipo = exparit1.tipo;
               else{
                   exparit.tipo = error;
                    error("[EXPRARIT] Error.Incompatibilidad de tipos en la expresión aritmética. No. de Línea: " + cmp.be.preAnalisis.getNumLinea());  
                   }
                  
            //Finaliza accion semantica {71}
        } else {
              error("[EXPRARIT] Error.Se esperaba una expresión aritmética. No. de Línea: " + cmp.be.preAnalisis.getNumLinea());

        }

    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void EXPRARIT_(Atributos exprarit_) {
        Atributos exparit = new Atributos();
        if (preAnalisis.equals("opsuma")) {
            //EXPRARIT' -> opsuma EXPRARIT
            emparejar("opsuma");
            EXPRARIT(exparit);
            //Inicia accion semantica {72}
            
                exprarit_.tipo = exparit.tipo;
            
            //Finaliza accion semantica {72}
        } else if (preAnalisis.equals("opmult")) {
            //EXPRARIT' -> opmult EXPRARIT
            emparejar("opmult");
            EXPRARIT(exparit);
            //Inicia accion semantica {73}
           
                exprarit_.tipo = exparit.tipo;
            
            //Finaliza accion semantica {73}
        } else {
           
          //Inicia accion semantica {74}
               //if (analizarSemantica) 
                exprarit_.tipo = vacio;
            //Finaliza accion semantica {74}
        }
        
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void EXPRCOND(Atributos exprcond) {
        Atributos exprel = new Atributos();
        Atributos exprlog = new Atributos();
        
        if (preAnalisis.equals("num")
                || preAnalisis.equals("num.num")
                || preAnalisis.equals("idvar")
                || preAnalisis.equals("literal")
                || preAnalisis.equals("id")
                || preAnalisis.equals("(")) {
            //EXPRCOND -> EXPRREL EXPRLOG
             EXPRREL(exprel);
             EXPRLOG(exprlog);
             //Inicia accion semantica {25}
            
                if(exprel.tipo.equals("boolean") &&
                        exprlog.tipo != error)
                    exprcond.tipo = vacio;
                else{
                    exprcond.tipo = vacio;
                    error("[EXPRCOND] Error, Error en la expresión condicional. No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
        
                }
            
            //Finaliza accion semantica {25}
        } else {
            error("[EXPRCOND] Error, Se esperaba una expresión condicional. No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void EXPRREL(Atributos exprel) {
        Atributos exparit = new Atributos();
        Atributos exparit1 = new Atributos();
        Matcher matcher1;
        Matcher matcher2;
        if (preAnalisis.equals("num")
                || preAnalisis.equals("num.num")
                || preAnalisis.equals("idvar")
                || preAnalisis.equals("literal")
                || preAnalisis.equals("id")
                || preAnalisis.equals("(")) {
            //EXPRREL-> EXPRARIT oprel EXPRARIT
            EXPRARIT(exparit);
            emparejar("oprel");
            EXPRARIT(exparit1);
            matcher1 = arrChar.matcher(exparit.tipo);
            matcher2 = arrChar.matcher(exparit1.tipo);
            //Inicia accion semantica{26}
           
                if(exparit.tipo.equals(exparit.tipo))
                        exprel.tipo = "boolean";
                else if(exparit.tipo.equals("int") && exparit1.tipo.equals("float"))
                     exprel.tipo = "boolean";
                else if(exparit.tipo.equals("float") && exparit1.tipo.equals("int"))
                     exprel.tipo = "boolean";
                else if(matcher1.matches() && matcher2.matches())
                     exprel.tipo = "boolean";
                else{
                    exprel.tipo = error;
                    error("[EXPRREL] Error. Tipos incompatibles en la comparacion. No. de Línea: " + cmp.be.preAnalisis.getNumLinea());

                }
            
            //Finaliza accion semantica {26}
        } else {
            error("[EXPRREL] Error.Se esperaba una expresión relacional. No. de Línea: " + cmp.be.preAnalisis.getNumLinea());

        }
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void EXPRLOG(Atributos exprlog) {
        Atributos exprel = new Atributos();
        if (preAnalisis.equals("and")) {
            //EXPRLOG -> and EXPRREL
            emparejar("and");
            EXPRREL(exprel);
            //Inicia accion semantica {27}
            
                exprlog.tipo = exprel.tipo;
            
            //Termina accion semantica {27}
            
        } else if (preAnalisis.equals("or")) {
            //EXPRLOG -> or EXPRREL
            emparejar("or");
            EXPRREL(exprel);
            //Inicia accion semantica {28}
            
                exprlog.tipo = exprel.tipo;
            
            //Termina accion semantica {28}
        } else {
         //Inicia accion semantica {29}
            
                exprlog.tipo = vacio;
            
            //Termina accion semantica {29}

        }
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void ELIMTAB(Atributos elimniatab) {
        Linea_BE id = new Linea_BE();
        if (preAnalisis.equals("drop")) {
            //ELIMTAB -> drop table id
            emparejar("drop");
            emparejar("table");
            id = cmp.be.preAnalisis;
            emparejar("id");
            //Inicia accion semantica {38}
            
                if(cmp.ts.buscaTipo(id.entrada).equals(null))
                    elimniatab.tipo = vacio;
                else{
                    elimniatab.tipo = error;
                   error("[ELIMTAB] Error. El id mencionado no le pertenece una tabla No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
                }
            
            //Finaliza accion semantica {38}
        } else {
            error("[ELIMTAB] Error. Falta palabra reservada drop No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985
//Primeros(IFELSE) = {if}
    private void IFELSE(Atributos ifelse) {
        Atributos exprcond = new Atributos();
        Atributos sentencias = new Atributos();
        Atributos ifelse_ =new Atributos();
        if (preAnalisis.equals("if")) {
            emparejar("if");
            EXPRCOND(exprcond);
            emparejar("begin");
            SENTENCIAS(sentencias);
            emparejar("end");
            IFELSE_(ifelse_);
            //Inicia accion semantica {21}
          
                if(exprcond.equals("boolean")&& sentencias.tipo.equals(vacio)
                        && ifelse_.tipo.equals(vacio))
                    ifelse.equals(vacio);
                else{
                    ifelse.equals(error);
                    error("[IFELSE]. Error de tipos en la comprobacion de ifelse. NUM LINEA: " + cmp.be.preAnalisis.numLinea);
        
                }
            
            //Finaliza accion semantica {21}
        } else {
            error("[IFELSE]. Se esperaba if. NUM LINEA: " + cmp.be.preAnalisis.numLinea);
        }
    }
//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985
    //Primeros(IFELSE') = {else,empty}

    private void IFELSE_(Atributos ifelse_) {
        Atributos sentencias = new Atributos();
        if (preAnalisis.equals("else")) {
            emparejar("else");
            emparejar("begin");
            SENTENCIAS(sentencias);
            emparejar("end");
            //Inicia accion semantica {22}
            
                ifelse_.tipo = sentencias.tipo;
            
            // Finializa accion semantica {22}
        } else {
            //VACIOOO
           //Inicia accion semantica {23}
           
                ifelse_.tipo = vacio;
            
           //Finaliza accion semantica {23}
        }
//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985
        //Primeros(IGUALACION) = {id}
    }

    private void IGUALACION(Atributos igualacion) {
        Linea_BE id = new Linea_BE();
        Atributos igualacionP = new Atributos();
        Atributos exparit = new Atributos();
        Matcher matcher1;
        Matcher matcher2;
            if (preAnalisis.equals("id")) {
            id = cmp.be.preAnalisis;
            emparejar("id");
            emparejar("opasig");
            EXPRARIT(exparit);
            IGUALACIONP(igualacionP);
            matcher1 = arrChar.matcher(cmp.ts.buscaTipo(id.entrada));
            matcher2 = arrChar.matcher(exparit.tipo);
            
            //Inicia accion semantica {49}
                
                    if(cmp.ts.buscaTipo(id.entrada).equals(exparit.tipo))
                        igualacion.tipo = vacio;
                    else if( matcher1.matches() && matcher2.matches())
                        igualacion.tipo = vacio;
                     else if(cmp.ts.buscaTipo(id.entrada).equals("float")&&
                            exparit.tipo.equals("int"))
                        igualacion.tipo = vacio; 
                    else if(cmp.ts.buscaTipo(id.entrada).equals(null)){
                        igualacion.tipo = error; 
                        error("[IGUALACION]. La variable mencionada aun no ha sido declarada NUM. LINEA: " + cmp.be.preAnalisis.numLinea);
                    }else{
                      igualacion.tipo = error; 
                      error("[IGUALACION].Error: Incompatibilidad de tipos en la asignacion NUM. LINEA: " + cmp.be.preAnalisis.numLinea);   
                    }
                
            //Inicia accion semantica {49}
        } else {
            error("[IGUALACION]. Se esperaba una asignacion valida NUM. LINEA: " + cmp.be.preAnalisis.numLinea);

    }
   }
//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985
    //Primeros(IGUALACIONP) = {',', empty}

    private void IGUALACIONP(Atributos igualacionP) {
        Atributos igualacion = new Atributos();
        if (preAnalisis.equals(",")) {
            emparejar(",");
            IGUALACION(igualacion);
            //Inicia accion semantica {50}
            
                igualacionP.tipo = igualacion.tipo;
            
            //Finaliza accion semantica {50}
        } else {
            //VACIOOO
            //Inicia accion semantica {51}
                igualacionP.tipo = vacio;
            
            //Finaliza accion semantica {51}
        }
    }
//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985
    //Primeros(INSERCION) = {insert}

    private void INSERCION(Atributos insercion) {
        Atributos columnas = new Atributos();
        Atributos expresiones = new Atributos();
        Linea_BE id = new Linea_BE();
        if (preAnalisis.equals("insert")) {
            emparejar("insert");
            emparejar("into");
            id = cmp.be.preAnalisis;
            emparejar("id");
            emparejar("(");
            COLUMNAS(columnas);
            emparejar(")");
            //inicia la accion semnatica {59}
           
                if(cmp.ss.checarArchivo(id.lexema+".db")){
                  cmp.ts.anadeTipo(id.entrada, "tabla");
                if(columnas.tipo.equals(vacio))
                    insercion.h = vacio;
                
                else{
                   insercion.h = error;
                    error("[INSERCION] el incompatibilidad con la columna mencionada" + cmp.be.preAnalisis.getNumLinea());  
                }
              }else{
                   insercion.h = error;
                    error("[INSERCION] el archivo mencionado no existe" + id.getNumLinea());  
                }
            
            //Finaliza la accion semantica {59}
            emparejar("values");
            emparejar("(");
            EXPRESIONES(expresiones);
            emparejar(")");
            //Inicia accion semantica {60}
           
                if(insercion.h.equals(vacio) && expresiones.tipo.equals(vacio))
                    insercion.tipo = vacio;
                else{
                    insercion.tipo = error;
                    error("[INSERCION]  Incompatibilidad de tipos con la expresion" + cmp.be.preAnalisis.numLinea);
            }
            //Finalizar accion semantica {60}
        
    }else {
            error("[INSERCION] Se esperaba una inserccion valida en la tabla" + cmp.be.preAnalisis.numLinea);
        }
   }
//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985
    //Primeros(LISTADOS) = {',',empty}

    private void LISTAIDS(Atributos listaIds) {
        Atributos listaIds1 = new Atributos();
        Linea_BE id = new Linea_BE();
        if (preAnalisis.equals(",")) {
            emparejar(",");
            emparejar("id");
            //Inicia accion semantica {74}
            if(!cmp.ts.buscaTipo(id.entrada).equals(null))
                listaIds.h = vacio;
            else{
            listaIds.h = error;
            error("[LISTAIDS] ERROR: El id mencionado aun no ha sido declarado"
            +cmp.be.preAnalisis.getLexema());
            }
                
            //Finaliz accion semantica {74}
            LISTAIDS(listaIds1);
            //Inicia accion semantica {75}
            if(listaIds.h.equals(vacio) && listaIds1.tipo.equals(vacio))
                listaIds.tipo = vacio;
            else{
            listaIds.tipo = error;
            error("[LISTAIDS] ERROR: incompatibilidad entre ids"
            +cmp.be.preAnalisis.getLexema());
            }
                
        } else {
            //VACIO
            //Finaliz accion semantica {76}
           
                listaIds.tipo = vacio;
            
            //Finaliza accion semantica {76}

        }
    }
//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985
    //Primeros(NULO) = {null,not,empty}

    private void NULO() {
        if (preAnalisis.equals("null")) {
            emparejar("null");
        } else if (preAnalisis.equals("not")) {
            emparejar("not");
            emparejar("null");
        } else {
            //VACIO
        }
    }
//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985
    //Primeros(OPERANDO) = {num,num.num,idvar,literal,id}

    private void OPERANDO(Atributos operando) {
        Linea_BE id = new Linea_BE();
        Linea_BE idVar = new Linea_BE();
        Linea_BE literal = new Linea_BE();
        if (preAnalisis.equals("num")) {
            emparejar("num");
            //Inicia accion semantica {75}
           
                operando.tipo = "int";
            
            //Finaliza accion semantica {75}
        } else if (preAnalisis.equals("num.num")) {
            emparejar("num.num");
            //Inicia accion semantica {76}
           
                operando.tipo = "float";
            
            //Finaliza accion semantica {76}
        } else if (preAnalisis.equals("idvar")) {
            idVar = cmp.be.preAnalisis;
            emparejar("idvar");
            //Inicia accion semantica {77}
           
                operando.tipo = cmp.ts.buscaTipo(idVar.entrada);
            
            //Finaliza accion semantica {77}
            
        }
          else if (preAnalisis.equals("literal")) {
              literal = cmp.be.preAnalisis;
              emparejar("literal");
            //Iniica accion semantica {78}
            operando.tipo = "array(1.."+literal.lexema.length()+",char)";
            //Finaliz accion semantica {78}
        } else if (preAnalisis.equals("id")) {
            id = cmp.be.preAnalisis;
            emparejar("id");
             //Inicia accion semantica {79}
          
                operando.tipo = cmp.ts.buscaTipo(id.entrada);
            
            //Finaliza accion semantica {79}
        } else {
            error("[OPERANDO]Se esperaba un dato valido NUM. LINEA" + cmp.be.preAnalisis.numLinea);
        }

    }
    
//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985

       private void SENTENCIAS(Atributos sentencias) {
           Atributos sentencias1 = new Atributos();
           Atributos sentencia = new Atributos();
        if (preAnalisis.equals("if") ||
            preAnalisis.equals("while") || 
            preAnalisis.equals("print") ||
            preAnalisis.equals("assign") ||
            preAnalisis.equals("select") ||
            preAnalisis.equals("delete") || 
            preAnalisis.equals("insert") ||
            preAnalisis.equals("update") || 
            preAnalisis.equals("create") ||
            preAnalisis.equals("drop") ||
            preAnalisis.equals("case")) {
            SENTENCIA(sentencia);
            SENTENCIAS(sentencias1);
            //Inicia accion semantica 8
           
                if(sentencia.tipo.equals(vacio) && sentencias1.tipo.equals(vacio))
                    sentencias.tipo = vacio;
                else {
                    sentencias.tipo = error;
                    error("[DECLARACION] ERROR: Error de tipo den la declaracion de variables"
                           + "NO. LINEA: "+cmp.be.preAnalisis.getLexema());
                
            }
            //Finaliza accion semantica 8
        } else {
            //VACIO
            //Inicia accion semantica 9

                sentencias.tipo = vacio;
            
            //Finalzia accion semantica 9
            
        }
    }
//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985

    private void SENTENCIA( Atributos sentencia ) {
        Atributos ifelse = new Atributos();
        Atributos senrep = new Atributos();
        Atributos despliegue = new Atributos();
        Atributos sentasig = new Atributos();
        Atributos sentselect = new Atributos();
        Atributos delreg = new Atributos();
        Atributos insercion = new Atributos();
        Atributos actregs = new Atributos();
        Atributos tabla = new Atributos();
        Atributos elimtab = new Atributos();
        Atributos selectiva = new Atributos();
        if (preAnalisis.equals("if")) {
            IFELSE(ifelse);
            //Inicia accion semantica 10
            
                sentencia.tipo = ifelse.tipo;
            
            //Finalzia accion semantica 10
        } else if (preAnalisis.equals("while")) {
            SENREP(senrep);
            //Inicia accion semantica 11
          
                sentencia.tipo = senrep.tipo;
            
            //Finalzia accion semantica 11
        } else if (preAnalisis.equals("print")) {
            DESPLIEGUE(despliegue);
            //Inicia accion semantica 12
           
                sentencia.tipo = despliegue.tipo;
            
            //Finalzia accion semantica 12
        } else if (preAnalisis.equals("assign")) {
            SENTASIG(sentasig);
            //Inicia accion semantica 13
           
                sentencia.tipo = sentasig.tipo;
            
            //Finalzia accion semantica 13
        } else if (preAnalisis.equals("select")) {
            SENTSELECT(sentselect);
            //Inicia accion semantica 14
           
                sentencia.tipo = sentselect.tipo;
            
            //Finalzia accion semantica 14
        } else if (preAnalisis.equals("delete")) {
            DELREG(delreg);
            //Inicia accion semantica 15
          
                sentencia.tipo = delreg.tipo;
            
            //Finalzia accion semantica 15
        } else if (preAnalisis.equals("insert")) {
            INSERCION(insercion);
            //Inicia accion semantica 16
           
                sentencia.tipo = insercion.tipo;
            
            //Finalzia accion semantica 16
        } else if (preAnalisis.equals("update")) {
            ACTREGS(actregs);
            //Inicia accion semantica 17
          
                sentencia.tipo = actregs.tipo;
            
            //Finalzia accion semantica 17
        } else if (preAnalisis.equals("create")) {
            TABLA(tabla);
            //Inicia accion semantica 18
            
                sentencia.tipo = tabla.tipo;
            
            //Finalzia accion semantica 18
        } else if (preAnalisis.equals("drop")){
            ELIMTAB(elimtab);
            //Inicia accion semantica 19
           
                sentencia.tipo = elimtab.tipo;
            
            //Finalzia accion semantica 19
        }
        else if (preAnalisis.equals("case")) {
            SELECTIVA(selectiva);
            //Inicia accion semantica 20
                sentencia.tipo = selectiva.tipo;
            
            //Finalzia accion semantica 20
        } else {
            error("[SENTENCIA]Se esperaba un tipo de sentencia valida NUM. LINEA" + cmp.be.preAnalisis.numLinea);
        }
    }

//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985

    private void SELECTIVA(Atributos selectiva) {
        Atributos selwhen = new Atributos();
        Atributos selelse = new Atributos();
        if (preAnalisis.equals("case")) {
            emparejar("case");
            SELWHEN(selwhen);
            SELELSE(selelse);
            emparejar("end");
            //Inicia accion semantica 9
           
                if(selwhen.tipo.equals(vacio) && selelse.tipo.equals(vacio))
                    selectiva.tipo = vacio;
                else {
                    selectiva.tipo = error;
                    error("[DECLARACION] ERROR: Error de tipo den la declaracion de variables"
                           + "NO. LINEA: "+cmp.be.preAnalisis.getLexema());
                }
            
            //Finalzia accion semantica 9
        } else {
            error("[SELECTIVA] Error. Se esperaba case. NO. LINEA: " + cmp.be.preAnalisis.numLinea);
        }
    }

//--------------------------------------------------------------------------
    // Autor: Samuel Alba Rios No. Control 19130886
    //PRIMERO ( SELWHEN ) = { when }
    private void SELWHEN(Atributos selwhen) {
        Atributos exprcond = new Atributos();
        Atributos sentencia = new Atributos();
        Atributos selwhen_ = new Atributos();
        if (preAnalisis.equals("when")) {
            emparejar("when");
            EXPRCOND(exprcond);
            //Inicia accion semantica 41
           
                if(exprcond.tipo.equals("boolean"))
                    selwhen.h = vacio;
                else {
                    selwhen.h = error;
                    error("[DECLARACION] ERROR: Expresion condicional invalida"
                           + "NO. LINEA: "+cmp.be.preAnalisis.getLexema());
                
                //Finaliza accion semantica 41
            }
            emparejar("then");
            SENTENCIA(sentencia);
            SELWHEN_(selwhen_);
            //Inicia accion semantica 42
            
                if(sentencia.tipo.equals(vacio) && selwhen_.tipo.equals(vacio)
                    && selwhen.h.equals(vacio))
                    selwhen.tipo = vacio;
                else {
                    selwhen.tipo = error;
                    error("[DECLARACION] ERROR: Error de tipo den la declaracion de variables"
                           + "NO. LINEA: "+cmp.be.preAnalisis.getLexema());
                }
            
            //Finalzia accion semantica 42
        } else {
            error("[SELWHEN] Error.Se esperaba when. Linea: " + cmp.be.preAnalisis.numLinea);
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Samuel Alba Rios, No. Control 19130886
    //PRIMERO ( SELWHEN’ ) =  { PRIMEROS ( SELWHEN ) { when }, empty}
    private void SELWHEN_(Atributos selwhen_) {
        Atributos selwhen = new Atributos();
        if (preAnalisis.equals("when")) {
            SELWHEN(selwhen);
           //Inicia accion semantica 39
            
                selwhen_.tipo = selwhen.tipo;
            
            //Finaliza accion semantica 39
        } else {
            // empty
            //Inicia accion semantica 40
            
                selwhen_.tipo = vacio;
            
            //Finaliza accion semantica 40
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Samuel Alba Rios, No. Control 19130886
    //PRIMERO ( SELELSE ) =  { else, empty }
    private void SELELSE(Atributos selelse) {
        Atributos sentencia = new Atributos();
        if (preAnalisis.equals("else")) {
            emparejar("else");
            SENTENCIA(sentencia);
            //Inicia accion semantica 43
            
                selelse.tipo = sentencia.tipo;
            
            //Finaliza accion semantica 43
        } else {
            //empty
            //Inicia accion semantica 44
            
                selelse.tipo = vacio;
            
            //Finaliza accion semantica 44
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Samuel Alba Rios, No. Control 19130886
    //PRIMERO ( SENREP ) =  { while }
    private void SENREP(Atributos senrep) {
        Atributos exprcond = new Atributos();
        Atributos sentencias = new Atributos();
        
        if (preAnalisis.equals("while")) {
            emparejar("while");
            EXPRCOND(exprcond);
            emparejar("begin");
            SENTENCIAS(sentencias);
            emparejar("end");
            //Inicia accion semantica 24
            
                if(exprcond.tipo.equals("boolean") && sentencias.tipo.equals(vacio))
                    senrep.tipo = vacio;
                else{
                    senrep.tipo = error;
                    error("[COMPROBACION] ERROR: Error de tipos en la comprobacion del while "
                            + "NO. LINEA:" + cmp.be.preAnalisis.getLexema());
                
            }
            //Finaliza accion semantica 24

        } else {
            error("[SENREP]Error. Se esperaba while. Linea: " + cmp.be.preAnalisis.numLinea);
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Samuel Alba Rios, No. Control 19130886
    //PRIMERO ( SENTASIG ) =  { assign }
    private void SENTASIG(Atributos sentasig) {
        Atributos exprarit = new Atributos();
        Linea_BE idvar = new Linea_BE();
        Matcher matcher1;
        Matcher matcher2;
        if (preAnalisis.equals("assign")) {
            emparejar("assign");
            idvar = cmp.be.preAnalisis;
            emparejar("idvar");
            emparejar("opasig");
            EXPRARIT(exprarit);
           
            matcher1 = arrChar.matcher(cmp.ts.buscaTipo(idvar.entrada));
            
            matcher2 = arrChar.matcher(exprarit.tipo);
            //Inicia accion semantica 31
           
                if(cmp.ts.buscaTipo(idvar.entrada).equals(exprarit.tipo))
                    sentasig.tipo = vacio;
                else if(cmp.ts.buscaTipo(idvar.entrada).equals("float") && exprarit.tipo.equals("int"))
                    sentasig.tipo = vacio;
                else if(matcher1.matches() && matcher2.matches())
                    sentasig.tipo = vacio;
                else {
                     sentasig.tipo = error;
                error("[INCOMPATIBILIDAD] ERROR: Error de tipo incompatibilidad de tipo en la asignacion"
                           + "NO. LINEA: "+cmp.be.preAnalisis.getLexema());
                }
           
            //Finaliza accion semantica 31
        } else {
            error("[SENTASIG] Error. Se esperaba assign. Linea: " + cmp.be.preAnalisis.numLinea);
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Samuel Alba Rios, No. Control 19130886
    //PRIMERO ( SENSELECT ) =  { select }
    private void SENTSELECT(Atributos sentselect) {
        Atributos sentselectc = new Atributos();
        Atributos exprcond = new Atributos();
        Linea_BE idvar = new Linea_BE();
        Linea_BE id = new Linea_BE();
        Linea_BE id1 = new Linea_BE();
        Matcher matcher1;
        Matcher matcher2;
        if (preAnalisis.equals("select")) {
            emparejar("select");
            idvar = cmp.be.preAnalisis;
            emparejar("idvar");
            emparejar("opasig");
            id = cmp.be.preAnalisis;
            emparejar("id"); 
            emparejar("from");
            id1 = cmp.be.preAnalisis;
            emparejar("id");
           
            matcher1 = arrChar.matcher(cmp.ts.buscaTipo(idvar.entrada));
            matcher2 = colmnChar.matcher(cmp.ts.buscaTipo(id.entrada));
            //Inicia accion semantica 65
           
              if (cmp.ss.checarArchivo(id1.lexema+".db")){
                 cmp.ts.anadeTipo(id1.entrada, "tabla");
                      if(cmp.ts.buscaTipo(idvar.entrada).equals("int") && cmp.ts.buscaTipo(id.entrada).equals("columnas(int)")){
                        sentselect.h = vacio;
                      }else if(cmp.ts.buscaTipo(idvar.entrada).equals("float") && cmp.ts.buscaTipo(id.entrada).equals("columnas(int)")){
                        sentselect.h = vacio;
                      } else if(cmp.ts.buscaTipo(idvar.entrada).equals("float") && cmp.ts.buscaTipo(id.entrada).equals("columnas(float)")){
                        sentselect.h = vacio;
                      }else if(matcher1.matches() && matcher2.matches()){
                        sentselect.h = vacio;
                      }else{
                          sentselect.h = error;
                    error("[SENTSELECT]. Error. Incompatibilidad de tipos en la sentencia."
                            + id.lexema + "linea: "+cmp.be.preAnalisis.getNumLinea());
       
                      }
                  
              }else{
                
                    sentselect.h = error;
                    error("[SENTSELECT]. Error. El archivo mencionado no existe."
                            + id.lexema + "linea: "+cmp.be.preAnalisis.getNumLinea());
                        
                  }
                  
            
            //Finaliza accion semantica 65
            emparejar("where");
            EXPRCOND(exprcond);
            //Inicia accion semantica 66
           
              if( sentselect.h.equals(vacio) && exprcond.tipo.equals("boolean")){
                   sentselect.tipo = vacio;
              }else{
                  sentselect.tipo = error;
                   error("[SENTSELECT]. Error. Incompatibilidad de tipos: " + cmp.be.preAnalisis.numLinea);
              }
            
            //Finaliza accion semantica 66
        } else {
            error("[SENTSELECT]. Error. Se esperaba select. linea: " + cmp.be.preAnalisis.numLinea);
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Samuel Alba Rios, No. Control 19130886
    //PRIMERO ( SENSELECTC ) =  { ,, empty }
    private void SENTSELECTC(Atributos sentselectc) {
        Atributos sentselect = new Atributos();
        Linea_BE idvar = new Linea_BE();
        Linea_BE id = new Linea_BE();
        Matcher matcher1;
        Matcher matcher2;
        if (preAnalisis.equals(",")) {
            emparejar(",");
            idvar = cmp.be.preAnalisis;
            emparejar("idvar");
            emparejar("opasig");
            id = cmp.be.preAnalisis;
            emparejar("id");
            matcher1 = arrChar.matcher(cmp.ts.buscaTipo(idvar.entrada));
            matcher2 = colmnChar.matcher(cmp.ts.buscaTipo(id.entrada));
            //Inicia accion semantica 67
            
              if(cmp.ts.buscaTipo(idvar.entrada).equals("int") && cmp.ts.buscaTipo(id.entrada).equals("columnas(int)")){
                        sentselectc.h = vacio;
                      }else if(cmp.ts.buscaTipo(idvar.entrada).equals("float") && cmp.ts.buscaTipo(id.entrada).equals("columnas(int)")){
                        sentselectc.h = vacio;
                      } else if(cmp.ts.buscaTipo(idvar.entrada).equals("float") && cmp.ts.buscaTipo(id.entrada).equals("columnas(float)")){
                        sentselectc.h = vacio;
                      }else if(matcher1.matches() && matcher2.matches()){
                        sentselectc.h = vacio;
                      }else{
                          sentselectc.h = error;
                    error("[SENTSELECTC]. Error. Incompatibilidad de tipos en la sentencia."
                            + id.lexema + "linea: "+cmp.be.preAnalisis.getNumLinea());
       
                      }
            
            //Finaliza accion semantica 67
            SENTSELECT(sentselect);
            //Inicia accion semantica 68
            
              if(sentselectc.h.equals(vacio) && sentselect.tipo.equals(vacio))
                  sentselectc.tipo = vacio;
            else{
                sentselectc.tipo = error;
                error("[SENTSELECTC]. Error. Incompatibilidad de tipos en la sentencia."
                            + id.lexema + "linea: "+cmp.be.preAnalisis.getNumLinea());
       
                      
            }
            //Finaliza accion semantica 68
        } else {
            // empty
            //Inicia accion semantica 69
           
                sentselectc.tipo = vacio;
            
            //Finaliza accion semantica 69
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Samuel Alba Rios, No. Control 19130886
    //PRIMERO ( TIPO ) =  { int, float, char }
    private void TIPO(Atributos tipo) {
        Linea_BE num = new Linea_BE();
        if (preAnalisis.equals("int")) {
            emparejar("int");
            //Inicia accion semantica 5
           
                tipo.tipo = "int";
            
            //Finaliza accion semantica 5
        }
        else if (preAnalisis.equals("float")) {
            emparejar("float");
            //Inicia accion semantica 6
          
                tipo.tipo = "float";
            
            //Finaliza accion semantica 6
        }
        else if (preAnalisis.equals("char")) {
            emparejar("char");
            emparejar("(");
            num = cmp.be.preAnalisis;
            emparejar("num");
            emparejar(")");
            //Inicia accion semantica 7
          
                tipo.tipo = "array(1.."+num.lexema+",char)";
            
            //Finaliza accion semantica 7
        } else {
            error("[TIPO] Error.Se esperaba tipo de dato. linea: " + cmp.be.preAnalisis.numLinea);
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Samuel Alba Rios, No. Control 19130886
    //PRIMERO ( TABLA ) =  { create }
    private void TABLA(Atributos tabla) {
        Atributos tablacolumnas = new Atributos();
        Linea_BE id = new Linea_BE();
        if (preAnalisis.equals("create")) {
            emparejar("create");
            emparejar("table");
            id = cmp.be.preAnalisis;
            emparejar("id");
            //Inicia accion semantica 32
            
                if(cmp.ts.buscaTipo(id.entrada).equals(null))
                {
                        cmp.ts.anadeTipo(id.entrada, "tabla");
                        tabla.h = vacio;
                }
                else { tabla.tipo = error;
                error("[DECLARACION] ERROR: Identificador de tabla ya fue declarado"
                           + "NO. LINEA: "+cmp.be.preAnalisis.getLexema());
            }
            
            //Finaliza accion semantica 32
            emparejar("(");
            TABLACOLUMNAS(tablacolumnas);
            emparejar(")");
            //Inicia accion semantica 33
            
                if(tabla.h.equals(vacio) && tablacolumnas.tipo.equals(vacio))
                    tabla.tipo = vacio;
            
            else { tabla.tipo = error;
                error("[TIPOS] ERROR: Error de tipos en sentencia CREATE TABLE"
                           + "NO. LINEA: "+cmp.be.preAnalisis.getLexema());
            }
            //Finaliza accion semantica 33
        } else {
            error("[TABLA]Error. Se esperaba create. No. de Línea: " + cmp.be.preAnalisis.numLinea);
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Samuel Alba RIos, No. Control 19130886
    // PRIMERO ( TABLACOLUMNAS ) =  { id }
    private void TABLACOLUMNAS(Atributos tablacolumnas) {
        Atributos tipo = new Atributos();
        Atributos tablacolumnas_ = new Atributos();
        Linea_BE id = new Linea_BE();
        if (preAnalisis.equals("id")) {
            id = cmp.be.preAnalisis;
            emparejar("id");
            TIPO(tipo);
            //Inicia accion semantica 34
          
                if(cmp.ts.buscaTipo(id.entrada).equals(null)){
                    cmp.ts.anadeTipo(id.entrada, "COLUMNA("+tipo.tipo+")");
                tablacolumnas.h = vacio;
                }
                else { tablacolumnas.tipo = error;
                error("[DECLARACION] ERROR: Identificador de columna ya fue declarado"
                           + "NO. LINEA: "+cmp.be.preAnalisis.getLexema());
            }   
            
            //Finaliza accion semantica 34
            NULO();
            TABLACOLUMNAS_(tablacolumnas_);
            //Inicia accion semantica 35
            
                if(tablacolumnas.h.equals(vacio) && tablacolumnas_.tipo.equals(vacio))
                    tablacolumnas.tipo = vacio;
                else { tablacolumnas.tipo = error;
                error("[DECLARACION] ERROR: Error de tipos en la declaracion de columnas"
                           + "NO. LINEA: "+cmp.be.preAnalisis.getLexema());
            }
            //Finaliza accion semantica 35
        } else {
            error("[TABLACOLUMNAS] Error. Se esperaba identificador. No. de Línea: " + cmp.be.preAnalisis.numLinea);
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Samuel Alba RIos, No. Control 19130886
    // PRIMERO ( TABLACOLUMNASP ) =  { ,, empty }
    private void TABLACOLUMNAS_(Atributos tablacolumnas_) {
        Atributos tablacolumnas = new Atributos();
        if (preAnalisis.equals(",")) {
            emparejar(",");
            TABLACOLUMNAS(tablacolumnas);
            //Inicia accion semantica 36
           
                tablacolumnas_.tipo = tablacolumnas.tipo;
            
            //Finaliza accion semantica 36
        } else {
            //empty
            //Inicia accion semantica 37
            
                tablacolumnas_.tipo = vacio;
            
            //Finaliza accion semantica 37
        }
    }
    
    private void PROGRAMA(){
        if (checarArchivo("profes.db")){
            System.out.println("Sí existe profes.db");
            PROGRAMASQL(new Atributos());
        }
    }
    
    private boolean checarArchivo ( String nomarchivo ) {
          FileReader     fr         = null;
          BufferedReader br         = null;
          String         linea      = null;
          String         columna    = null;
          String         tipo       = null;
          String         ambito     = null;
          boolean        existeArch = false;
          int            pos;
          
          try {
            // Intentar abrir el archivo con el dise�o de la tabla  
            fr = new FileReader ( nomarchivo );
            cmp.ts.anadeTipo(cmp.be.preAnalisis.getEntrada(), "tabla");
            br = new BufferedReader ( fr );
                
            // Leer linea x linea, cada linea es la especificacion de una columna
	        linea = br.readLine ();
	        while ( linea != null  ) {
  	        // Extraer nombre y tipo de dato de la columna
               try
               {
                   columna = linea.substring (  0, 24 ).trim ();
               }
               catch (Exception err)
               {
                   columna = "ERROR";
               }
               try
               {
                   tipo    = linea.substring ( 29     ).trim ();
               }
               catch (Exception err)
               {
                   tipo = "ERROR";
               }
               try
               {
                   ambito  = nomarchivo.substring( 0, nomarchivo.length ()- 3 );
               }
               catch(Exception err)
               {
                   ambito = "ERROR";
               }
               // Agregar a la tabla de simbolos
               Linea_TS lts = new Linea_TS ( "id", 
                                             columna, 
                                             "COLUMNA(" + tipo + ")", 
                                             ambito
                                            );
               // Checar si en la Tabla de Simbolos existe la entrada para un 
               // lexema y ambito iguales al de columna y ambito de la tabla .db
               if ( ( pos = cmp.ts.buscar ( columna, ambito ) ) > 0 ) {
                   // YA EXISTE: Si no tiene tipo asignarle el tipo columna(t) 
                   if ( cmp.ts.buscaTipo ( pos ).trim ().isEmpty () )
                       cmp.ts.anadeTipo  ( pos, tipo );
               } else {
                   // NO EXISTE: Buscar si en la T. de S. existe solo el lexema de la columna
                   if ( ( pos = cmp.ts.buscar ( columna ) ) > 0 ) {
                       // SI EXISTE: checar si el ambito esta en blanco
                       Linea_TS aux = cmp.ts.obt_elemento ( pos );
                       if ( aux.getAmbito ().trim ().isEmpty () ) {
                         // Ambito en blanco rellenar el tipo y el ambito  
                         cmp.ts.anadeTipo   ( pos, "COLUMNA("+ tipo+ ")"   );
                         cmp.ts.anadeAmbito ( pos, ambito );
                         
                       } else {
                         // Insertar un nuevo elemento a la tabla de simb.
                         cmp.ts.insertar ( lts );
                       }
                   } else {
                       // NO EXISTE: insertar un nuevo elemento a la tabla de simb.
                       cmp.ts.insertar ( lts );
                   }
                }
                   
                // Leer siguiente linea
	            linea   = br.readLine ();
	        }
            existeArch  = true;
          } catch ( IOException ex ) {
  	          System.out.println ( ex );
	      } finally {
              // Cierra los streams de texto si es que se crearon
              try {
                if ( br != null )
                    br.close ();
                if ( fr != null )
                    fr.close();
              } catch ( IOException ex ) {}
          }           
          return existeArch;
        }
	
    
    
}
