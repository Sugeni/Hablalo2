package isware.uneg.es.chateando.Logica;

public class ListaString {


    static public boolean enLista( String [] lista, String text){

        for( int i = 0; i< lista.length ; i++){

            if( lista[i].equals(text)){
                return true;
            }
        }

        return false;

    }

    static public String agregarLista( String[] lista, String dato){
        String resultado = dato;

        for(int i = 0 ; i< lista.length ; i++){

            resultado= resultado +" "+ lista[i];

        }

        return resultado;

    }

    static public String eliminarLista( String[] lista, String dato){
        String resultado = "";
        for(int i = 0 ; i< lista.length ; i++){

            if( lista [i].equals(dato)){
                continue;
            }

            resultado= resultado +" "+ lista[i];

        }

        return resultado;

    }


}
